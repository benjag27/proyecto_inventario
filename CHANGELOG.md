
# CHANGELOG — proyecto_inventario
> Documento interno de desarrollo y control operativo. Registra los cambios relevantes por versión.
> Formato normativo: [TIPO] Descripción — donde TIPO puede ser ADD, FIX, CHANGE, REMOVE, REFACTOR.

---

## [1.0.0] — 2026-11-03

### Autenticación — base distribuida y cambio obligatorio de contraseña (PR #39)
- [ADD] Base de datos **pre-sembrada** que viaja como recurso (`src/main/resources/db/inventario_base.db`) con el esquema completo y el usuario `admin`; en la primera ejecución `BsConfig` la copia a `~/.phora_inventario/inventario.db`, de modo que el código de distribución ya no siembra credenciales
- [ADD] `BsConfig.migrate()` aplica las migraciones pendientes sobre instalaciones existentes **sin tocar los datos del usuario**
- [ADD] Flujo de **cambio obligatorio de contraseña**: columna `must_change_password` en `users`, `LoginService.mustChangePassword`/`changePassword`, vista `ChangePasswordView` y navegación `SceneManager.showChangePassword` desde `LoginView`
- [ADD] `UserRepository.updatePassword(username, hash, mustChangePassword)` y `User.isMustChangePassword()`
- [ADD] Test `BsConfigTest`: la base de distribución trae el `admin` con cambio de contraseña pendiente, las credenciales `admin`/`admin123` son válidas, y `changePassword` reemplaza el hash y limpia la marca

### Correcciones (fixes)
- [FIX] `LoginService`: se normaliza el nombre de usuario con `trim()` en `authenticate`, `mustChangePassword` y `changePassword` (helper `normalizeUsername`), y se agregan guardas de `null`; un espacio accidental al inicio o fin impedía el ingreso (`WHERE username = 'admin '` nunca coincide con `admin`) y, además, podía omitir la pantalla de cambio obligatorio de contraseña

### Flujo de Productos — Feature-002: Códigos de barras (implementada, issue #33)
- [ADD] Tabla `product_barcodes` (relación 1-N con `products`, `barcode UNIQUE`, `ON DELETE CASCADE`) auto-creada en `BsConfig.initDB()`; se habilita `PRAGMA foreign_keys = ON` por conexión para garantizar el borrado en cascada de los códigos al dar de baja un producto
- [ADD] Entidad `Product` con lista inmutable de códigos de barras (`List<String>` en el builder + getter/setter); se agrega `setId` para el alta
- [CHANGE] `ProductRepositoryImpl`: `add` y `update` pasan a transacciones (`setAutoCommit(false)` + `commit`) para persistir producto y códigos de forma atómica; `update` **sincroniza** la lista (agrega los códigos nuevos y borra los removidos, permitiendo eliminar uno solo); `findById`, `findAll` y `findByName` cargan los códigos de cada producto; un código duplicado entre productos se rechaza con mensaje "El código de barras ya está registrado en otro producto" (`IllegalArgumentException`)
- [CHANGE] `AddProduct` y `UpdateProduct` reciben `List<String> barcodes` (normalización `trim`, descarte de vacíos y rechazo de duplicados en la misma lista) y reflejan los códigos en la descripción del `AuditLog`
- [ADD] `ProductFormView`: editor de códigos de barras en alta y modificación (campo + "Agregar código" y una fila por código con botón para quitarlo individualmente); el bloque es opcional (puede quedar vacío) y se precarga con los códigos existentes al modificar; las búsquedas (por ID y por nombre) muestran los códigos en el detalle
- [ADD] `ProductPanelView`: nueva columna "Códigos" en el listado del inventario
- [ADD] Test `ProductBarcodeTest` (alta sin códigos, alta con varios, agregar un código, eliminar uno solo manteniendo el resto, unicidad entre productos y borrado en cascada)

### Códigos de barras — validación de entrada (scanner y tipeo)
- [ADD] `BarcodeRules` (`domain/model`): único punto de definición de las reglas de un código de barras — largo máximo `64` (cubre EAN/UPC y Code128), solo caracteres ASCII imprimibles, `trim` y rechazo de duplicados dentro del mismo producto; admite códigos alfanuméricos variables tanto escaneados como tipeados
- [CHANGE] `AddProduct` y `UpdateProduct` delegan en `BarcodeRules.normalizeAll()`; se elimina `AddProduct.normalizeBarcodes()` y el acoplamiento estático desde `UpdateProduct`
- [CHANGE] `ProductFormView`: el campo de código usa un `TextFormatter` que impide tipear o pegar más de `BarcodeRules.MAX_LENGTH`, y al agregar valida con `BarcodeRules.normalize()` mostrando el mensaje de error
- [ADD] Test `BarcodeRulesTest` (largo máximo y excedido, `trim`, nulos, caracteres de control, alfanumérico, duplicados)

### Empaquetado y distribución (Linux)
- [ADD] `scripts/empaquetar-linux.sh`: automatiza el protocolo de despliegue (compilar + testear → `jlink` → `jpackage`) con opciones `--type app-image|deb|both`, `--version`, `--name`, `--dest`, `--maintainer`, `--skip-tests` y `--offline`; valida JDK 17+, herramientas de `.deb` y que `db/inventario_base.db` quede dentro del runtime antes de empaquetar
- [ADD] El script genera `Inventario-<version>-linux-x64.tar.gz`, el app-image portable que se adjunta a la release para descargar, descomprimir y ejecutar sin instalar nada
- [ADD] `Manual.md` §4 (empaquetado para Linux) y §5 (primera release): requisitos, comandos, salidas, verificación, instalación/desinstalación y pasos del tag + GitHub Release
- [CHANGE] `pom.xml` pasa de `1.0-SNAPSHOT` a `1.0.0` para cortar la primera release
- [ADD] Primera release `v1.0.0` con el app-image portable (`.tar.gz`) y el instalador `.deb` adjuntos en GitHub Releases

### Limpieza de documentación y comentarios
- [REMOVE] Se elimina `spec/` (constitución y features) y la carpeta vacía `código/`: eran documentos de proceso que duplicaban el CHANGELOG y el Manual
- [REMOVE] Se quitan comentarios obvios o ambiguos del código; se conservan los que explican decisiones no evidentes (seguridad, transacciones, ruta de la BD, reglas de códigos de barras)
- [REMOVE] Se quita del CHANGELOG el protocolo de compilación duplicado (ahora vive en `scripts/empaquetar-linux.sh` y `Manual.md` §4)

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

## COMPILACIÓN Y EMPAQUETADO

El flujo está automatizado en `scripts/empaquetar-linux.sh` y documentado en `Manual.md` §4.
