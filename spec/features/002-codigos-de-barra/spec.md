# Feature-002 — Códigos de barras para productos (opcionales y múltiples)

> Carpeta: `spec/features/002-codigos-de-barra/` · Issue: [#33](https://github.com/benjag27/proyecto_inventario/issues/33) · Estado: `backlog`

## Objetivo

Que cada producto pueda tener **códigos de barras opcionales y múltiples**, gestionables desde el **alta** y la **modificación** del producto:

1. Un producto puede darse de alta **sin** ningún código de barras (campo opcional).
2. Un mismo producto puede tener **varios** códigos a la vez (los códigos cambian con el tiempo: reediciones, reposición de packing, cambio de proveedor) y el negocio debe seguir reconociendo el producto por cualquiera de ellos.
3. En **Modificar producto**, poder **agregar más códigos** y **eliminar uno solo** sin borrar el resto (y sin eliminar el producto).
4. Un mismo código de barras **no puede repetirse** entre productos (unicidad).

## Contexto y flujo actual

`Product` no tiene ningún campo de código de barras: la tabla `products` solo guarda `id`, `name`, `price` y `stock` (`BsConfig.initDB()`), y `AddProduct`/`ProductFormView` (modo `ALTA`) capturan únicamente nombre, precio y stock.

Como el código puede ser **múltiple** (1:N) y debe poder agregarse/eliminarse de a uno, una columna simple en `products` no alcanza: se necesita una tabla nueva `product_barcodes` enlazada por `product_id`.

## Comportamiento esperado

- **Alta de producto:** campo de códigos de barras **opcional**; se pueden cargar varios. Dejarlo vacío permite dar de alta sin códigos.
- **Listado:** la tabla de productos muestra una columna "Códigos" con los códigos del producto.
- **Modificación:** al editar un producto se listan sus códigos existentes, se permite **agregar** códigos nuevos y **eliminar uno individual** (cada código con su propio botón de quitar). Guardar persiste la lista final.
- **Consultas:** al buscar por ID se muestra el detalle del producto **incluyendo sus códigos**.
- **Baja de producto:** al eliminar el producto, sus códigos se borran en cascada (sin lógica extra).
- **Unicidad (regla de negocio):** si un código ya está registrado en otro producto (o repetido en el mismo), se muestra un mensaje de error claro y no se persiste.

## Criterios de aceptación

- [ ] **CA1** — Se puede dar de alta un producto **sin** códigos de barras.
- [ ] **CA2** — Se puede dar de alta un producto **con varios** códigos de barras.
- [ ] **CA3** — Al modificar un producto se pueden **agregar** códigos nuevos a los existentes.
- [ ] **CA4** — Al modificar un producto se puede **eliminar un código individual** sin borrar el resto ni el producto.
- [ ] **CA5** — El mismo código de barras **no puede repetirse** en dos productos (mensaje de error claro).
- [ ] **CA6** — El listado del panel de productos muestra una columna **"Códigos"**.
- [ ] **CA7** — La búsqueda por ID muestra el detalle del producto **incluyendo sus códigos**.
- [ ] **CA8** — Al dar de baja un producto se eliminan también sus códigos (cascada).

## Fuera de alcance

- Generación o impresión de etiquetas con código de barras.
- Lectura con scanner (hardware). La entrada es textual, pero el modelo de datos queda listo para un scanner futuro.
- Validación universal de estándares (EAN-13, UPC, etc.): solo se normaliza el texto (`trim`) y se aplica unicidad a nivel BD.

## Estado de implementación

- [x] Spec aprobada → pasó a `in-progress`
- [x] Implementada en `dev` (rama `feature/codigos-de-barra`) — pendiente de merge a `main` vía PR y cierre del issue #33