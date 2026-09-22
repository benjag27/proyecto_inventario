# Plan — Feature-001: Dar de baja seguro

> Issue: #32 · Depende de: `spec/features/001-baja-segura/spec.md`

## Enfoque

Feature **100% de presentación**: el problema vive en la capa `presentation`. La capa de aplicación ya tiene los casos de uso necesarios (`FindByName`, `FindProduct`, `DeleteProduct`), por lo que **no se toca `domain`, `application` ni `infrastructure`**.

Se trabaja sobre `ProductPanelView` (disparador y selección) y `ProductFormView` (formulario de baja con confirmación), además de `SceneManager` solo si se necesita una nueva ruta de navegación.

## Capas y archivos afectados

| Archivo | Cambio |
|---|---|
| `presentation/ProductPanelView.java` | Botón "Dar de baja": si hay selección → abrir `BAJA` con `prefill`; si no → abrir flujo de **búsqueda para baja** (nombre o ID). |
| `presentation/ProductFormView.java` | Modo `BAJA`: campo **ID deshabilitado** cuando hay `prefill`; mostrar nombre/stock del producto; agregar **confirmación explícita** (`Confirmar`/`Cancelar`). Nuevo modo de búsqueda para baja (o vista dedicada) usando `FindByName` y `FindProduct`. |
| `presentation/SceneManager.java` | Solo si hace falta una ruta para el flujo de búsqueda de baja (reutilizar `showProductForm` si es viable). |
| `CHANGELOG.md` / `spec/...` | Registrar la implementación al cerrar el issue. |

## Estrategia de implementación

1. **Inducción al "Dar de baja":** en `ProductPanelView`:
   - `table.getSelectionModel().getSelectedItem() != null` → `showProductForm(BAJA, seleccionado)`.
   - `null` → navegar al flujo de búsqueda (elegir entre búsqueda por nombre y por ID).
2. **Formulario `BAJA` con ID fijo:**
   - Si `prefill != null`: `txtId.setDisable(true)` (o `setEditable(false)`), `txtId` con el ID del producto elegido.
   - Incluir label de contexto: `Producto: <nombre> · Stock: <n>`.
   - Cambiar "Eliminar producto" por confirmación: `Alert` (CONFIRMATION) con "¿Eliminar el producto *X* (ID *n*)?" antes de ejecutar `DeleteProduct`.
3. **Búsqueda para baja (sin selección):**
   - Campo para ID (con `FindProduct`) y campo para nombre (con `FindByName`) o una vista de resultados.
   - Al elegir resultado → pasar al formulario `BAJA` con ese producto como `prefill` (ID ya fijo).
   - Resultados vacíos → mensaje de error y permanecer en la búsqueda.

## Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Borrado accidental | Confirmación explícita + ID solo lectura + mostrar nombre/stock del producto. |
| Deleción de un producto distinto al buscado | El ID siempre proviene del producto elegido (`prefill`), nunca se tipea a mano. |
| Duplicar lógica de búsqueda | Reutilizar `FindByName` / `FindProduct` (ya expuestos en `AppContext`). |
| Input inválido (ID no numérico) | Reutilizar el manejo de `NumberFormatException` ya existente. |

## Verificación

- `mvn clean verify` (compila + tests).
- Prueba manual de los escenarios: sin selección + búsqueda sin resultados; búsqueda con resultados; con selección; cancelación de la confirmación; eliminación exitosa y refresco del listado.

## Cierre

- Actualizar `CHANGELOG.md` con las entradas `[ADD]/[CHANGE]`.
- Marcar la feature `done` en `roadmap.md`.
- Cerrar el issue #32 en GitHub.