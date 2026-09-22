# Tasks — Feature-003: Transiciones suaves

> Issue: #35 · Checklist de implementación en orden.

## Análisis
- [ ] T1 — Revisar `SceneManager.show(...)` (corte seco actual: `stage.setScene` directo).
- [ ] T2 — Revisar `SceneManager.showProductForm` (`freeSize` → `sizeToScene` → `lockSize`).
- [ ] T3 — Confirmar vistas que navegan productos ↔ operaciones (`ProductPanelView`, `ProductFormView`).

## Implementación
- [ ] T4 — `SceneManager.show(...)`: aplicar `FadeTransition` de ~250 ms sobre `scene.getRoot()` al mostrar.
- [ ] T5 — (Opcional) deslizamiento leve (`TranslateTransition`) como complemento del fade.
- [ ] T6 — `showProductForm`: reemplazar el re-escaleo brusco por tamaño estable por modo o ajuste animado ~200 ms.
- [ ] T7 — Verificar que el regreso al panel entra con la misma transición suave.

## Validación
- [ ] T8 — Verificar CA1..CA6 de `spec.md`.
- [ ] T9 — `mvn clean verify` en verde.
- [ ] T10 — Prueba manual: cada botón de operaciones ida/vuelta sin parpadeos ni saltos.
- [ ] T11 — Regresión: login, menú principal, auditoría siguen navegando.

## Cierre
- [ ] T12 — Registrar en `CHANGELOG.md` (`[ADD]/[CHANGE]`).
- [ ] T13 — Marcar `done` en `roadmap.md` y cerrar issue #35.