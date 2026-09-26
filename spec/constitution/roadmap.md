# Roadmap

Orden de desarrollo de features del proyecto, orientado a desarrollo con IA.

## Ciclo de una feature

Toda feature nueva sigue su carpeta en `spec/features/NNN-nombre/`:

1. `spec.md` — qué hace la feature + criterios de aceptación (debe aprobarse antes de implementar).
2. `plan.md` — cómo se implementa (capas afectadas, archivos, riesgos).
3. `tasks.md` — checklist de tareas accionables.

Reglas:
- Una feature solo pasa a `in-progress` cuando su `spec.md` está aprobado.
- Todo cambio se registra en `CHANGELOG.md` (`[TIPO]`).
- Todo desarrollo sigue el flujo de ramas del `Manual.md` (**rama propia por feature** `feature/nombre-de-la-tarea` o `docs/...` → **pull request contra `dev`** → **el revisor aprueba y hace el merge** → el revisor **decide `dev` → `main`**).
- **Nada de push directo a `dev`/`main`**: cada cambio vive en su rama hasta que el revisor lo apruebe.

## Estado actual (respaldado por el CHANGELOG)

> Fase base — correspondiente a la release `1.0-SNAPSHOT` / `1.0.0-beta`. Todo esto está **implementado**.

| Área | Estado |
|---|---|
| Arquitectura formal en 4 capas (`domain`, `application`, `infrastructure`, `presentation`) | done |
| Autenticación multiusuario (login + PBKDF2WithHmacSHA256, comparación en tiempo constante) | done |
| CRUD de productos (`AddProduct`, `UpdateProduct`, `DeleteProduct`, `FindProduct`) | done |
| Búsqueda en vivo por nombre (`FindByName`) | done |
| Persistencia SQLite embebida con auto-creación de tablas y datos iniciales | done |
| Base de datos en `~/.phora_inventario/` (ruta fija independiente del cwd) + bootstrap del admin (`admin`/`admin123`, PBKDF2) en primera ejecución | done |
| Auditoría de acciones (`audit_logs` + vista `AuditLogView`) | done |
| GUI JavaFX con tema oscuro y menú principal | done |
| Protocolo de compilación/despliegue (`jlink` + `jpackage`, `.msi` y `.deb`) | done |
| Test automatizado de bootstrap de base de datos (`BsConfigTest`) | done |

## Visión de evolución (hitos estratégicos)

Estrategia global de escalabilidad. Son **hitos** que se descompondrán en features concretas (`spec/features/NNN-.../`) cuando se decida avanzar; el backlog operativo sigue "por definir".

| Fase | Descripción | Estado |
|---|---|---|
| A | Despliegue actual: SQLite embebida, un `inventario.db` por instalación, una PC por negocio | done |
| B | Versión alternativa con **base de datos en la nube**, no dependiente del archivo local | planned |
| C | **Multiusuario con concurrencia**: varios usuarios consultan y modifican el mismo inventario con consistencia (transacciones, resolución de conflictos, roles) | later |
| D | **Asistente con IA y análisis económicos**: asistencia inteligente para el inventario y análisis económicos del negocio (tendencias, rentabilidad, decisiones) sobre sus datos | later |

Principios de la evolución:

- La **arquitectura por capas** es lo que habilita las fases B y C: se reemplaza la infraestructura (`*Impl` de `domain/repository/`), no la lógica de negocio.
- La versión local (fase A) y la futura nube (fase B) **conviven como variantes** del sistema dentro del mismo código base.

## Backlog de features

> Las features se definen aquí con su estado y su issue en GitHub. Cada una tiene su carpeta en `spec/features/NNN-nombre/`.

| # | Feature | Issue | Estado |
|---|---|---|---|
| 001 | **Dar de baja seguro** — búsqueda por nombre/ID y confirmación explícita del ID al eliminar | [#32](https://github.com/benjag27/proyecto_inventario/issues/32) | `done` · implementada y mergeada en `dev` |
| 002 | **Códigos de barras** — opcionales y múltiples por producto; agregar y eliminar uno solo desde la modificación | [#33](https://github.com/benjag27/proyecto_inventario/issues/33) | `in-progress` (implementada en `dev`) |
| 003 | **Transiciones suaves** — paso continuo entre Productos y sus operaciones sin corte seco (fade/deslizamiento, ~200–400 ms) ni salto de tamaño | [#35](https://github.com/benjag27/proyecto_inventario/issues/35) | `done` · fade-in (~250 ms) centralizado en `SceneManager.show` |
| 004 | **Estética coherente de los formularios** — `ProductFormView` con cabecera del módulo, campos alineados, resultados presentables y tamaño de ventana estable en los 5 modos | [#36](https://github.com/benjag27/proyecto_inventario/issues/36) | `done` · cabecera del módulo (`menu-header`) en `createScene` |

Pendiente técnico conocido (no es una feature, es deuda de configuración):

| Item | Estado |
|---|---|
| ~~Mover `inventario.db` a `~/.phora_inventario/`~~ — resuelto en `BsConfig.java` | done |

## Reglas de este documento

1. Agregar una feature = crear carpeta `spec/features/NNN-nombre/` con `spec.md`, `plan.md` y `tasks.md`.
2. El orden en este archivo es la prioridad; reordenar aquí **antes** de empezar a implementar.
3. Las features ya implementadas no se re-incluyen como backlog: su historial vive en `CHANGELOG.md`.