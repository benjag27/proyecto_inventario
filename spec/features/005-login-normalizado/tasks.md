# Tasks — Feature-005: Login tolerante a espacios

> Issue: #42 · Checklist de implementación en orden.

## Análisis
- [x] T1 — Revisar `LoginService.authenticate` y confirmar la falta de normalización del usuario.
- [x] T2 — Confirmar que `findByUsername` compara por igualdad (`WHERE username = ?`).

## Implementación
- [x] T3 — Helper `normalizeUsername` (`trim` + guarda de `null`).
- [x] T4 — Aplicarlo en `authenticate`.
- [x] T5 — Aplicarlo en `mustChangePassword` y `changePassword`.
- [x] T6 — Test `LoginServiceTest` (repositorio en memoria).

## Validación
- [x] T7 — Verificar CA1..CA5 de `spec.md`.
- [x] T8 — `mvn -Dtest=LoginServiceTest test` en verde (6/6).

## Cierre
- [x] T9 — Registrar `[FIX]` en `CHANGELOG.md`.
- [ ] T10 — Marcar `done` en `roadmap.md` y cerrar el issue #42.
