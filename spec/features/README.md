# Features — Guía de documentación

Aquí se documentan las features del proyecto antes de implementarlas. Cada feature tiene una carpeta numerada `NNN-nombre-corto/`.

> **Estado actual:** las carpetas `spec/`, `plan.md` y `tasks.md` se versionan en el repositorio (rama `dev`) para que **todo el equipo trabaje con el mismo plan** de cada feature. La correspondencia feature ↔ issue se registra en el `spec/constitution/roadmap.md` y en la sección "Gestión de features (Issues)" del `CHANGELOG.md`. Los responsables van en cada issue de GitHub.

## Estructura de cada feature

- `spec.md` — **Qué** hace la feature y criterios de aceptación. Define el alcance, los límites y cómo se verifica que está terminada.
- `plan.md` — **Cómo** se implementa: capas/archivos afectados, enfoque técnico, riesgos y pasos grandes.
- `tasks.md` — Checklist de tareas accionables y verificables, en orden de ejecución.

## Convenciones

- **Numeración:** `NNN` en 3 dígitos según el orden del `roadmap.md` (`001-`, `002-`, ...).
- **Nombre corto:** en minúsculas, separado por guiones, describiendo la feature (ej: `001-movimientos-stock`).
- **Idioma:** contenido en español.
- **Ciclo:** una feature pasa de `backlog` a `in-progress` solo cuando su `spec.md` está aprobado.
- **Registro:** al implementarla, registrar los cambios en `CHANGELOG.md` con formato `[TIPO]`.
- **Ramas:** el desarrollo sigue el flujo de ramas del `Manual.md` (`feature/nombre-de-la-tarea` → `dev` → `main`).

## Flujo sugerido

1. Definir la feature en `spec/constitution/roadmap.md` (prioridad + estado).
2. Crear `spec/features/NNN-nombre/spec.md` y aprobarlo.
3. Redactar `plan.md`.
4. Implementar siguiendo `tasks.md` en una rama `feature/...`.
5. Verificar, integrar a `dev`, registrar en `CHANGELOG.md` y cerrar la feature en el roadmap.