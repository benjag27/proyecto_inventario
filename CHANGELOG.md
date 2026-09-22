
# CHANGELOG — proyecto_inventario
> Documento interno de desarrollo y control operativo. Registra los cambios relevantes por versión.
> Formato normativo: [TIPO] Descripción — donde TIPO puede ser ADD, FIX, CHANGE, REMOVE, REFACTOR.

---

## [1.0-SNAPSHOT] — En desarrollo continuo

### Gestión de features (Issues)
> Los responsables/assignees se definen **en cada issue de GitHub**. Esta tabla es la traza de estado para cualquier agente o integrante del equipo: qué falta, qué está en curso y qué se terminó.

**Por hacer / En curso (backlog):**

| Feature | Issue | Estado |
|---|---|---|
| Feature-001 — **Dar de baja seguro**: búsqueda por nombre/ID y confirmación explícita del ID al eliminar | [#32](https://github.com/benjag27/proyecto_inventario/issues/32) | `backlog` · spec en `spec/features/001-baja-segura/` |
| Feature-002 — **Códigos de barras**: opcionales y múltiples por producto; alta y modificación con agregar/quitar uno solo | [#33](https://github.com/benjag27/proyecto_inventario/issues/33) | `backlog` · spec en `spec/features/002-codigos-de-barra/` |
| Feature-003 — **Transiciones suaves**: paso continuo entre Productos y sus operaciones sin corte seco ni salto de tamaño | [#35](https://github.com/benjag27/proyecto_inventario/issues/35) | `backlog` · spec en `spec/features/003-transiciones-suaves/` |
| Feature-004 — **Estética coherente de los formularios**: `ProductFormView` con cabecera del módulo, campos alineados, resultados presentables y tamaño estable | [#36](https://github.com/benjag27/proyecto_inventario/issues/36) | `backlog` · spec en `spec/features/004-estetica-formularios/` |

**Hechas (implementadas y cerradas):**

| Feature | Referencia | Estado |
|---|---|---|
| Flujo de Productos (listado + operaciones laterales + interfaz ensanchada) | Integrada vía PR #31 (ver subsección *Flujo de Productos*) | `done` |

- [ADD] **Feature-001 documentada y versionada** con especificación completa (`spec/features/001-baja-segura/` con `spec.md`, `plan.md` y `tasks.md`) e **issue creado en GitHub (#32)**; las carpetas de features se comparten en el repo para que el equipo implemente con el mismo plan (cada LLM que tome el issue trabaja sobre el mismo `spec.md`/`plan.md`); queda pendiente de implementación (flujo `feature/... → dev → main`)
- [ADD] **Feature-002 documentada y versionada** con especificación completa (`spec/features/002-codigos-de-barra/` con `spec.md`, `plan.md` y `tasks.md`) e **issue creado en GitHub (#33)**; especifica códigos de barras opcionales y múltiples por producto (alta con cero o varios códigos, modificación con agregar/eliminar uno solo, unicidad); pendiente de implementación (flujo `feature/... → dev → main`)
- [ADD] **Feature-003 documentada y versionada** con especificación completa (`spec/features/003-transiciones-suaves/`) e **issue creado en GitHub (#35)**; centraliza en `SceneManager.show(...)` una transición suave (fade/deslizamiento ~250 ms) para el paso Productos ↔ operaciones y evita el salto de tamaño de ventana (`sizeToScene`), a partir del análisis del corte seco actual y del re-escaleo por modo; pendiente de implementación
- [ADD] **Feature-004 documentada y versionada** con especificación completa (`spec/features/004-estetica-formularios/`) e **issue creado en GitHub (#36)**; unifica la estética de los 5 modos de `ProductFormView` (cabecera del módulo en lugar de estilos de login, campos alineados, resultados de búsqueda presentables en vez de un `Label` multilínea, tamaño de ventana estable); colabora con la Feature-003; pendiente de implementación

### Organización del desarrollo (especificación orientada a IA)
- [ADD] Estructura `spec/` para desarrollo orientado a IA: carpeta `constitution/` con los documentos de constitución del proyecto y carpeta `features/` para documentar cada feature futura (`NNN-nombre/` con `spec.md`, `plan.md` y `tasks.md`)
- [ADD] `spec/constitution/mission.md` — qué se construye y para quién: sistema de inventario de escritorio local, multi-negocio, una PC por negocio, base de datos embebida (un `inventario.db` por instalación), modelo de uso admin + empleados
- [ADD] `spec/constitution/tech-stack.md` — tecnologías, convenciones y razones de cada decisión (SQLite embebida local, JavaFX 21, jlink/jpackage, Windows y Linux 50/50)
- [ADD] `spec/constitution/roadmap.md` — orden de features con ciclo `spec.md → plan.md → tasks.md`; estado actual respaldado por este CHANGELOG y backlog "por definir"
- [ADD] Carpeta `código/` como referencia estructural dentro del modelo de carpetas del proyecto
- [ADD] Convenio normativo: todo cambio relevante futuro se registra en este archivo `CHANGELOG.md` con formato `[TIPO] Descripción`
- [ADD] Racional de escalabilidad de la arquitectura por capas: las interfaces de repositorio (`domain/repository/`) son el punto de extensión para migrar la infraestructura de datos (SQLite local → base de datos en la nube) sin tocar la lógica de negocio (`domain` / `application`)
- [ADD] Visión de evolución definida en `spec/constitution/roadmap.md`: (A) despliegue local embebido actual, (B) versión con base de datos en la nube no dependiente del archivo local, (C) multiusuario con concurrencia y consistencia de datos (transacciones, resolución de conflictos, roles), (D) asistente con inteligencia artificial y análisis económicos del negocio sobre sus propios datos
- [CHANGE] Decisión de arquitectura de datos (pendiente de implementar): el `inventario.db` debe migrar de la ruta relativa (`jdbc:sqlite:inventario.db` en `BsConfig.java`) a una carpeta oculta dentro del home del usuario (`~/.phora_inventario/`), para soportar instalación en Windows (`Program Files` sin permisos de escritura), sobrevivir a reinstalaciones y actualizaciones, y evitar sobrescrituras de datos durante sincronizaciones del código
- [CHANGE] **Ruta de la base de datos implementada:** `BsConfig.java` migra a `~/.phora_inventario/inventario.db` (carpeta oculta en el home, creada dinámicamente según el SO), independiente del directorio de ejecución — resuelve el login fallido al abrir la app desde terminal (el `.db` se creaba según el cwd)
- [ADD] **Bootstrapping del usuario administrador:** en la primera ejecución (tabla `users` vacía), `BsConfig.seedDefaultAdmin()` crea `admin` / `admin123` con hashing PBKDF2; la contraseña nunca se guarda en texto plano
- [CHANGE] `LoginService.hashPassword` y los helpers `pbkdf2` pasan a ser estáticos (funciones puras), permitiendo al bootstrap generar el hash sin instanciar el repositorio
- [ADD] Test automatizado `BsConfigTest` (JUnit 5) verificando el seed del admin en la primera ejecución y el formato no-plano del hash
- [CHANGE] Flujo de ramas operativo definido en `Manual.md`: `main` queda reservado como rama de **distribución** (solo releases e instaladores, prohibido push directo); `dev` pasa a ser la rama de **producción activa** donde trabaja el equipo (base de `feature/*`, integración del día a día)
- [ADD] Estado actual del producto (estructura `spec/`, documentación, fix de bootstrap de login y ruta de BD) sincronizado en la rama `dev` del remoto (`origin/dev`) para desarrollo colaborativo

### Flujo de Productos (interfaz)
- [CHANGE] Flujo del módulo Productos: al abrir **Productos** se muestra directamente el **listado del inventario** (buscador en vivo por nombre + contador) y las operaciones (**dar de alta, modificar, dar de baja, buscar por ID, buscar por nombre**) como **botones en un panel lateral**
- [ADD] Nueva vista `ProductPanelView` que integra listado + panel lateral de operaciones; los botones *Modificar* y *Dar de baja* actúan sobre el producto **seleccionado en la tabla**
- [CHANGE] `ProductFormView` ahora acepta un producto **pre-cargado (prefill)** para pre-llenar los formularios de modificar/baja/buscar desde la selección; la navegación de vuelta apunta al nuevo panel
- [REMOVE] `ProductMenuView` y `ProductListView`: su funcionalidad quedó absorbida por `ProductPanelView`
- [CHANGE] Interfaz gráfica ensanchada: menú principal **1420×840**, pantalla de productos **1300×760** y formularios **760×540** (tarjeta de contenido 480px)

### Arquitectura base
- [ADD] Definición de arquitectura formal en 4 capas desacopladas: `domain`, `application`, `infrastructure`, `presentation`
- [ADD] Paquete base `org.phora` bajo la estructura jerárquica de ciclo de vida Maven estándar
- [ADD] `AppContext.java` como componente centralizado y único punto de ensamblado de dependencias (inyección manual nativa)
- [ADD] `SceneManager.java` como controlador unificado de navegación; se prohíbe que las vistas manipulen o conozcan el `Stage` de forma directa

### Domain (Capa de Reglas de Negocio)
- [ADD] Entidad `Product` implementada con el patrón Builder (`Product.Builder`) y constructor privado para garantizar la inmutabilidad y consistencia
- [ADD] Entidad de seguridad `User` con campos dedicados para `username` y `passwordHash`
- [ADD] Interfaz contractual `ProductRepository` con operaciones abstractas `add`, `update`, `delete`, `findById`
- [ADD] Interfaz contractual `UserRepository` exponiendo exclusivamente el método de consulta `findByUsername`
- [ADD] Servicio de dominio `LoginService` en `domain/service` a cargo del procesamiento lógico de credenciales

### Application (Casos de Uso)
- [ADD] `AddProduct` — caso de uso para el alta de productos; valida la no vaciedad de las cadenas
- [ADD] `UpdateProduct` — realiza modificaciones operativas de nombre y stock localizados por ID
- [ADD] `DeleteProduct` — elimina un registro de producto del sistema mediante su ID
- [ADD] `FindProduct` — realiza la consulta y recuperación exacta de un producto por ID
- [ADD] `FindByName` — nuevo caso de uso encargado de la búsqueda semántica y filtrado dinámico de colecciones por coincidencia parcial de texto (búsqueda de stock por nombre)
- [FIX] `FindProduct.execute()` corregido: se descarta la salida cruda a consola (`void`) sustituyéndola por el retorno de un contenedor estructurado `Optional<Product>` para consumo de la UI
- [FIX] `UpdateProduct.execute()` corregido: se fuerza la invocación correspondiente a `productRepository.update()`, mitigando la pérdida de datos en memoria volátil al actualizar stock
- [CHANGE] Firmas de `DeleteProduct.execute()` y `UpdateProduct.execute()` redefinidas a tipo `boolean` para reportar estados de fallo a la UI ante registros inexistentes

### Infrastructure (Persistencia y Datos)
- [ADD] Componente operativo `ProductRepositoryImpl` — implementación JDBC nativa para transacciones de productos
- [ADD] Componente operativo `UserRepositoryImpl` — implementación JDBC nativa para credenciales de acceso
- [ADD] Módulo de configuración `BsConfig` a cargo de proveer los hilos de conexión a datos
- [CHANGE] Motor relacional de base de datos migrado de **MySQL** a **SQLite** para permitir distribución 100% autocontenida sin servicios externos
- [CHANGE] Reescritura del subsistema `BsConfig` apuntando a la cadena local de conexión `jdbc:sqlite:inventario.db`, incorporando sentencias `CREATE TABLE IF NOT EXISTS` autoejecutables en el arranque
- [CHANGE] Tablas del esquema renombradas al idioma inglés estándar: `productos` → `products`, `usuarios` → `users`

### Seguridad / Autenticación Criptográfica
- [CHANGE] Migración sucesiva de librerías criptográficas: se descartan dependencias de terceros conflictivas con la modularidad JRE (como `jbcrypt` y `spring-security-crypto`) a favor de una implementación nativa basada en el algoritmo **PBKDF2WithHmacSHA256** mediante el paquete puro `javax.crypto`, logrando compatibilidad inmediata en el árbol `jlink`
- [ADD] Mitigación de vulnerabilidades de análisis de ejecución (Timing Attacks) en `LoginService` mediante comparaciones vectoriales en tiempo constante

### Presentation (Capa JavaFX Pura)
- [ADD] `LoginView` — pantalla de ingreso estructurada en formato de tarjeta centrada con validación rígida de campos vacíos
- [ADD] `MainMenuView` — panel principal posicionado dinámicamente mediante resortes estructurales (`Region`) y alineación absoluta al centro (`Pos.CENTER`) en resoluciones altas
- [ADD] `ProductMenuView` — submenú secundario provisto de la nueva tarjeta interactiva "Buscar por Nombre"
- [ADD] `ProductFormView` — vista inteligente de formularios gobernada por estados (enum `Modo`) que adapta la UI e inyecta dinámicamente el nuevo flujo adaptado para la consulta de stock por nombre semántico
- [FIX] Alineaciones visuales asimétricas corregidas forzando propiedades `Pos.CENTER` y aplicando un escalado dinámico homogéneo en la inicialización de contenedores

---

##  PROTOCOLO OBLIGATORIO DE COMPILACIÓN Y DESPLIEGUE

Queda estrictamente reglamentado el uso de las siguientes fases de comandos de forma aislada e incremental para la generación correcta de ejecutables distributivos limpios.

### Fase 1: Compilación Básica y Ejecución en Entorno de Desarrollo
Para levantar la aplicación de forma rápida y probar cambios locales del código fuente:
```bash
mvn clean javafx:run
```
### Fase 2: Generación del JRE Autocontenido y Reducido (jlink)

Este paso realiza el enlazado modular aislando un entorno virtual de Java mínimo y optimizado para la aplicación, libre de dependencias del JRE del sistema operativo destino:

Bash

```
mvn clean javafx:jlink
```

- **Resultado operativo esperado:** Generación completa de la estructura binaria en el directorio relativo: `target/inventario-app/`
### Fase 3: Ejecución Manual y Validación Técnica del Runtime

Antes de empaquetar un instalador, es imperativo validar de forma aislada que el entorno generado por `jlink` levanta de forma correcta ejecutando el script nativo según su plataforma:

- **En entornos de desarrollo Windows (CMD / PowerShell):**
```
    target\inventario-app\bin\inventario.bat
    
```

- **En entornos de desarrollo Unix / Linux / macOS (Terminal):*
    ```
    chmod +x target/inventario-app/bin/inventario
    ./target/inventario-app/bin/inventario
    ```


### Fase 4: Empaquetado Formal del Instalador de Producción (jpackage)

Una vez verificado que el runtime modular es estable, proceda a invocar la herramienta CLI `jpackage` nativa de su JDK de forma aislada para compilar el empaquetado final. Los instaladores resultantes se depositarán de forma estricta en la ruta técnica asignada: `target/installer/`

#### 💻 Ejecución en Entornos Windows (Generación de Instalador ejecutable `.msi`)


```
jpackage --type msi ^
  --runtime-image target/inventario-app ^
  --module org.phora/org.phora.Main ^
  --name "InventarioApp" ^
  --vendor "Phora" ^
  --app-version 1.0.0 ^
  --dest target/installer ^
  --win-dir-chooser ^
  --win-shortcut
```

_(Nota Windows: Requiere tener instalado previamente **Wix Toolset** en el PATH del sistema para evitar excepciones de compilación)._

#### 🐧 Ejecución en Entornos Linux (Generación de Paquete distribuible `.deb` para Debian/Ubuntu)



```
jpackage --type deb \
  --runtime-image target/inventario-app \
  --module org.phora/org.phora.Main \
  --name "inventario-app" \
  --vendor "Phora" \
  --app-version 1.0.0 \
  --dest target/installer \
  --linux-shortcut
```

_(Nota Linux: Requiere contar con las herramientas nativas del subsistema `dpkg-dev` activas en la terminal)._