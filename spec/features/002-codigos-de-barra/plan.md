# Plan — Feature-002: Códigos de barras

> Issue: [#33](https://github.com/benjag27/proyecto_inventario/issues/33) · Depende de: `spec/features/002-codigos-de-barra/spec.md`

## Enfoque

Feature de **datos + persistencia + UI**, transversal a las 4 capas. Requiere:

1. **Nueva tabla** `product_barcodes` (1-N con `products`).
2. **Entidad `Product`** con `List<String> barcodes`.
3. **Sync en `add`/`update`** del repositorio con transacciones.
4. **Formularios** de alta y modificación que permitan agregar/quitar códigos.

## Capas y archivos afectados

| Capa | Archivo | Cambio |
|---|---|---|
| Domain | `src/main/java/org/phora/domain/model/Product.java` | Nuevo `List<String> barcodes` (builder + getter + setter); `ProductRepository` **no cambia** (los códigos viven dentro del agregado). |
| Application | `src/main/java/org/phora/application/AddProduct.java` | Nueva firma `execute(..., List<String> barcodes, String activeUser)`; validar duplicados dentro de la misma lista; registrar códigos en el audit. |
| Application | `src/main/java/org/phora/application/UpdateProduct.java` | Nueva firma con `List<String> barcodes`; llamar `product.setBarcodes(...)`; registrar códigos en el audit. |
| Infrastructure | `src/main/java/org/phora/infrastructure/persistence/BsConfig.java` | `CREATE TABLE IF NOT EXISTS product_barcodes (...)` + ejecutarla en `initDB()`. |
| Infrastructure | `src/main/java/org/phora/infrastructure/persistence/ProductRepositoryImpl.java` | Transacciones en `add`/`update`; sincronizar códigos (insertar nuevos / borrar removidos); cargar códigos en `findById`/`findAll`/`findByName`; mapear `SQLException` por `UNIQUE`. |
| Infrastructure | `src/main/java/org/phora/infrastructure/AppContext.java` | **Sin cambios** (los casos de uso ya están instanciados; solo cambian sus parámetros). |
| Presentation | `src/main/java/org/phora/presentation/ProductFormView.java` | Bloque de códigos en `formAlta()` y `formModificar()` (agregar + eliminar individual por fila); `Modo.BUSCAR` muestra códigos en el detalle. |
| Presentation | `src/main/java/org/phora/presentation/ProductPanelView.java` | Columna "Códigos" en la tabla. |

## Estrategia de implementación

### 1. Base de datos (`BsConfig.initDB()`)

```sql
CREATE TABLE IF NOT EXISTS product_barcodes (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    barcode    TEXT    NOT NULL UNIQUE
)
```

- Se ejecuta junto a `products`, `users` y `audit_logs`. Al ser `CREATE TABLE IF NOT EXISTS`, **una BD existente la crea sin tocar datos** (no hace falta migración).
- `ON DELETE CASCADE`: al dar de baja un producto, sus códigos se borran solos.

### 2. Domain (`Product`)

- `private final List<String> barcodes` (inmutable con `List.copyOf` para no exponer la referencia).
- Builder `.barcodes(List<String>)` con default `List.of()` (producto **opcionalmente sin códigos**).
- Getter `getBarcodes()` y setter `setBarcodes(List<String>)` (mismo patrón que los setters actuales que usa `UpdateProduct`).

### 3. Persistencia (`ProductRepositoryImpl`)

- `add(Product)`:
  - `conn.setAutoCommit(false)`.
  - Insert producto con `RETURN_GENERATED_KEYS` para obtener `product.getId()`.
  - Insert cada código (bucle); si uno viola `UNIQUE` → `rollback` + traducir a mensaje ("El código ya está registrado en otro producto").
  - `commit` al finalizar.
- `update(Product)`:
  - Transacción con `setAutoCommit(false)`/`commit`/`rollback`.
  - Actualizar `name/price/stock`.
  - **Sync de códigos:** `SELECT barcode FROM product_barcodes WHERE product_id = ?` → borrar los que ya no están en la nueva lista (`DELETE ... WHERE product_id = ? AND barcode = ?`) e insertar los nuevos. Esto cubre "agregar uno" y "eliminar uno sin eliminar el resto".
- `findById`/`findAll`/`findByName`: tras construir el producto base, `SELECT barcode FROM product_barcodes WHERE product_id = ?` y `product.setBarcodes(...)`. (Optimización futura: una sola query agrupada si la tabla crece.)

### 4. Application

- `AddProduct.execute(String name, double price, int stock, List<String> barcodes, String activeUser)`:
  - Validar que no haya duplicados dentro de la misma lista (antes de llegar a BD).
  - Descripción del audit con los códigos (o "sin códigos").
- `UpdateProduct.execute(String name, double price, int stock, List<String> barcodes, int id, String activeUser)`:
  - `product.setBarcodes(barcodes)` → repo sincroniza.
  - Descripción del audit con los códigos finales.

### 5. Presentation

- `ProductFormView.formAlta()`: bloque "Códigos de barras (opcional)" →
  - `TextField` + botón "Agregar código".
  - Lista (filas) de códigos ya agregados, cada una con botón "✖" para quitarlo.
  - Si la lista queda vacía, se da de alta sin códigos.
- `ProductFormView.formModificar()`: lo mismo, pero **precargando** los códigos existentes (todos) — se pueden agregar más o eliminar uno individual.
- `ProductFormView.formBuscar()` / `formBuscarNombre()`: mostrar códigos en el detalle del resultado.
- `ProductPanelView.buildTable()`: columna "Códigos" (códigos unidos por coma; vacío si no tiene).

## Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Códigos huérfanos si falla la mitad del `add`/`update` | Transacciones (`setAutoCommit(false)` + `commit`/`rollback`) en `ProductRepositoryImpl`. |
| Un código repetido rompe a mitad del alta | Capturar `SQLException` por `UNIQUE`, hacer `rollback` y mostrar mensaje claro en la UI. |
| Duplicados dentro de la misma lista | Validación a nivel de caso de uso antes de persistir. |
| Romper la firma de `AddProduct.execute`/`UpdateProduct.execute` (llamadores en `ProductFormView`) | Cambiar los llamadores en el **mismo commit**; no queda código roto a medias. |
| Códigos repetidos en la misma lista | Validación a nivel de caso de uso antes de llegar a la BD. |
| Código sin normalizar (espacios) | `trim()` al capturar y antes de persistir (y en el repo al leer). |

## Verificación

- `mvn clean install` compila.
- `mvn test` en verde.
- Prueba manual: alta sin códigos, alta con varios, modificar agregando uno, modificar eliminando un único código, buscar producto por código, intentar duplicar un código → error.

## Cierre

- Registrar en `CHANGELOG.md` (`[ADD]`/`[CHANGE]`).
- Marcar `done` en `roadmap.md` y cerrar el issue #33.
