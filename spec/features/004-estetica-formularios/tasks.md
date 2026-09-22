# Tasks — Feature-004: Estética coherente de `ProductFormView`

> Issue: #36 · Checklist de implementación en orden.

## Análisis
- [ ] T1 — Revisar `ProductFormView.createScene()` (estilos de login reutilizados).
- [ ] T2 — Revisar los 5 modos (`formAlta`, `formModificar`, `formBaja`, `formBuscar`, `formBuscarNombre`) y su salida del `VBox`.
- [ ] T3 — Revisar `SceneManager.showProductForm` (re-escaleo por modo) y `styles/app.css` (clases disponibles).

## Implementación
- [ ] T4 — Cabecera: `menu-header`/`menu-subheader` + "← Volver" alineado (esquema del módulo).
- [ ] T5 — Estructura común: tarjeta de ancho fijo y campos alineados para los 5 modos.
- [ ] T6 — Buscar por nombre: resultados en `TableView<Product>` compacta (o tarjetas), no `Label` multilínea.
- [ ] T7 — Buscar por ID: recuadro de resultado estilizado en lugar del label plano.
- [ ] T8 — `SceneManager.showProductForm`: tamaño de ventana **constante** entre modos (sin `sizeToScene`).
- [ ] T9 — `app.css`: clases nuevas (`form-card`, `result-table` u otras) respetando el tema oscuro.
- [ ] T10 — Mantener `prefill` intacto en modificar/baja.

## Validación
- [ ] T11 — Verificar CA1..CA6 de `spec.md`.
- [ ] T12 — `mvn clean verify` en verde.
- [ ] T13 — Prueba visual de los 5 modos (estructura igual, tema oscuro, tamaño estable).
- [ ] T14 — Regresión funcional: alta/modificar/baja/búsquedas operan igual (sin prefill roto).

## Cierre
- [ ] T15 — Registrar en `CHANGELOG.md` (`[ADD]/[CHANGE]`).
- [ ] T16 — Marcar `done` en `roadmap.md` y cerrar issue #36.