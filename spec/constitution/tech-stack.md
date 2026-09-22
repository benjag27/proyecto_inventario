# Tech Stack

## Core

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java (JDK) | 17 |
| GUI Framework | JavaFX | 21.0.2 |
| Base de datos | SQLite (embebida) via `sqlite-jdbc` | 3.43.0.0 |
| Build System | Apache Maven | 3.x+ |
| Tests | JUnit Jupiter | 5.10.2 |

## Decisiones tecnológicas y su "por qué"

### SQLite embebida
- **Por qué:** el producto es un inventario que corre en **una sola computadora por negocio**. SQLite permite que **cada instalación genere su propio archivo `inventario.db`**, sin instalar, configurar ni mantener ningún servidor.
- **Datos locales por diseño:** sin red, sin sincronización, sin servicios externos. Offline y privado.
- **`sqlite-jdbc` fijado en `3.43.0.0`:** última versión sin dependencia de `slf4j` en su `module-info`, compatible con `jlink` sin trucos adicionales.
- **Ciclo de vida:** las tablas se auto-crean con `CREATE TABLE IF NOT EXISTS` al arrancar (`BsConfig.initDB()`).

### Ruta del archivo de base de datos
- **Definido e implementado:** el `inventario.db` se crea en una **carpeta oculta dentro del home del usuario** → `~/.phora_inventario/inventario.db` (Linux/macOS) y `%USERPROFILE%\.phora_inventario\inventario.db` (Windows). Se calcula dinámicamente según el SO previa creación del directorio (`BsConfig.java`). **Criterio de aislamiento:** independiente del directorio desde donde se ejecute la app; evitar sobrescrituras de datos durante sincronizaciones/actualizaciones del código, según README.
- **Bootstrapping:** en la primera ejecución (tabla `users` vacía), `BsConfig` crea automáticamente el usuario administrador por defecto (`admin` / `admin123`) con hashing PBKDF2. La contraseña nunca se guarda en texto plano.

### JavaFX 21
- **Por qué:** GUI de escritorio multiplataforma (Windows + Linux) nativa, con tema oscuro, para una app local de una sola PC.

### jlink + jpackage
- **`jlink`:** runtime JRE reducido y autocontenido por plataforma → el usuario final **no necesita Java instalado**.
- **`jpackage`:** genera instaladores formales por plataforma: `.msi` (Windows) y `.deb` (Linux). **Ambas plataformas son igualmente prioritarias (50/50).**

## Arquitectura

- Proyecto **modular** (`module-info.java`), paquete base `org.phora`.
- **4 capas desacopladas:**
  - `domain` → entidades (`Product`, `User`, `AuditLog`), contratos de repositorios (interfaces) y servicios de negocio (`LoginService`, `AuditLogService`).
  - `application` → casos de uso con verbos en infinitivo (`AddProduct`, `FindByName`, `UpdateProduct`, `DeleteProduct`, `FindProduct`, `ListProducts`).
  - `infrastructure` → implementaciones `Impl` de repositorios (JDBC/SQLite) y `BsConfig` (conexión); ensamblado de dependencias en `AppContext`.
  - `presentation` → vistas `*View` (JavaFX) y navegación centralizada en `SceneManager` (las vistas no manipulan el `Stage`).
- Patrones: builder (`Product.Builder`), inyección manual de dependencias en `AppContext`.

**¿Por qué la arquitectura por capas? → Escalabilidad.** El objetivo es poder crecer en tamaño (más productos, más negocios, más usuarios) y **migrar de infraestructura sin reescribir la lógica de negocio**. Los contratos están en `domain/repository/` (`ProductRepository`, `UserRepository`, `AuditLogRepository`): hoy se implementan con JDBC/SQLite, pero ese punto de extensión es exactamente lo que permite una versión futura con base de datos en la nube.

## Visión de evolución (escalabilidad)

- **Punto de extensión principal:** las interfaces de `domain/repository/` son la "costura" (seam). Una versión con BD en la nube = nuevas implementaciones `*Impl` que hablan con un servidor remoto, **sin tocar `domain` ni `application`**.
- **Escalabilidad en tamaño:** el modelo de capas absorbe crecimiento; si SQLite deja de ser suficiente (concurrencia, volumen), se migra el motor de BD.
- **Etapa futura — BD en la nube:** variante del sistema que no depende del archivo local; persistencia en un motor de BD remoto (a definir: servidor propio o servicio gestionado).
- **Etapa futura — Multiusuario con concurrencia:** varios usuarios consultando y modificando el mismo inventario a la vez con **consistencia**: transacciones y aislamiento (ACID), control de concurrencia y resolución de conflictos (ej: optimistic locking), roles/permisos y autenticación centralizada.

## Seguridad

- Hashing de contraseñas con **PBKDF2WithHmacSHA256** (implementación nativa `javax.crypto`, sin librerías de terceros conflictivas con `jlink`).
- Comparación de credenciales en **tiempo constante** (mitigación de Timing Attacks).

## Normativas externas

- **`Manual.md`** (raíz): protocolo obligatorio de ramas git (`main` / `dev` / `feature/`) y flujo diario de merge. **Todo desarrollo sigue este flujo.**
- **`CHANGELOG.md`** (raíz): historial normativo del proyecto. **Todo cambio relevante debe registrar una entrada con formato `[TIPO] Descripción`** (`TIPO` ∈ `ADD`, `FIX`, `CHANGE`, `REMOVE`, `REFACTOR`).
- **`spec/features/`**: cada feature nueva se documenta en su carpeta `NNN-nombre/` con `spec.md`, `plan.md` y `tasks.md`.

## Convenciones

- **Idioma:** código y APIs en **inglés**; documentación (`spec/`, `README`, `CHANGELOG`) en **español** (salvo títulos de productos).
- **Esquema de datos:** tablas en inglés (`products`, `users`, `audit_logs`).
- **Nombres:** clases en PascalCase, métodos/variables en camelCase, paquetes en minúsculas.
- **Formato:** indentación de 4 espacios, llaves en la misma línea (K&R).
- **Repositorios:** interfaces en `domain/repository/`; implementaciones con sufijo `Impl` en `infrastructure/persistence/`.
- **Vistas:** sufijo `View` en `presentation/`; navegación por `SceneManager`.

## Commands

```bash
mvn clean install      # compilar y empaquetar
mvn javafx:run         # ejecutar en modo desarrollo (Fase 1 del CHANGELOG)
mvn test               # correr tests
mvn clean javafx:jlink # generar imagen de runtime (Fase 2)
mvn clean install      # base previa al empaquetado
```

El protocolo completo de compilación, validación y empaquetado (`jlink` + `jpackage` para `.msi`/`.deb`) está normado en la sección **"PROTOCOLO OBLIGATORIO DE COMPILACIÓN Y DESPLIEGUE"** del `CHANGELOG.md`.