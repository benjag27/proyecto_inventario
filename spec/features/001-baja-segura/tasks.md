# Tasks — Feature-001: Dar de baja seguro

> Issue: #32 · Checklist de implementación en orden.

## Análisis
- [ ] T1 — Revisar `ProductPanelView.buildSideActions()` (manejo actual del botón "Dar de baja").
- [ ] T2 — Revisar `ProductFormView` (modo `BAJA`) y el `prefill` existente.
- [ ] T3 — Confirmar casos de uso disponibles: `FindByName`, `FindProduct`, `DeleteProduct` en `AppContext`.

## Implementación
- [ ] T4 — `ProductPanelView`: sin selección → habilitar flujo de **búsqueda para baja** (nombre/ID).
- [ ] T5 — `ProductPanelView`: con selección → abrir `BAJA` con `prefill`.
- [ ] T6 — `ProductFormView` (modo `BAJA`): **ID deshabilitado** cuando hay `prefill` + label con nombre/stock.
- [ ] T7 — `ProductFormView`: agregar **confirmación explícita** (`Alert` de confirmación) antes de `DeleteProduct`.
- [ ] T8 — Flujo de búsqueda: por **nombre** (`FindByName`) con selección de resultado.
- [ ] T9 — Flujo de búsqueda: por **ID** (`FindProduct`) con selección de resultado.
- [ ] T10 — Búsqueda sin resultados: mostrar mensaje claro y permanecer en la búsqueda.
- [ ] T11 — Tras eliminar: volver a `ProductPanelView` con listado refrescado.
- [ ] T12 — `SceneManager`: agregar ruta(s) si el flujo de búsqueda requiere navegación nueva.

## Validación
- [ ] T13 — Verificar CA1..CA7 criterios de aceptación de `spec.md`.
- [ ] T14 — `mvn clean verify` en verde.
- [ ] T15 — Prueba manual de los escenarios de `plan.md`.

## Cierre
- [ ] T16 — Registrar cambios en `CHANGELOG.md` (`[ADD]/[CHANGE]`).
- [ ] T17 — Marcar `done` en `roadmap.md` y cerrar el issue #32.