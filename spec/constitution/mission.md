# Misión

## Producto

**Phora** — Sistema de gestión de inventario de escritorio local.

Aplicación ligera, de cero configuración, para el seguimiento de almacén y productos.

## Para quién construimos

- **Usuario final objetivo:** comercios locales (multi-negocio: la app se distribuye e instala, y se adapta, en varios negocios distintos).
- **Adaptación por negocio:** la app ofrece flexibilidad de adaptación extra para cada negocio que la quiera usar, logrando una **experiencia personalizada** según sus necesidades y rubro.
- **Modelo de operación:** un negocio = una sola computadora. La aplicación corre **100% local** en esa máquina.
- **Datos:** cada instalación genera su propio archivo `inventario.db` en la carpeta de datos del usuario. No hay servidores, ni red, ni sincronización.
- **Personas que la usan en cada negocio:** un **administrador** (dueño/encargado) + **empleados** con sus propias cuentas.

## Qué construimos

- Autenticación segura multiusuario con roles (admin + empleados).
- Gestión completa de productos: alta, modificación, baja y búsqueda en vivo por nombre.
- Control de inventario local con stock y precios.
- Registro de auditoría de acciones (quién hizo qué y cuándo).
- Interfaz de escritorio moderna, sencilla y multiplataforma, con tema oscuro.
- Adaptación extra por negocio para una experiencia personalizada.

## Por qué base de datos embebida

Elegimos SQLite embebida porque el producto resuelve un problema **físicamente local**:

1. **Un archivo por instalación:** cada negocio tiene su propia base, generada al instalar, sin depender de un servidor central.
2. **Cero configuración:** no hay que instalar, configurar ni mantener ningún motor de base de datos.
3. **Offline por diseño:** el inventario de un negocio vive y se consulta en esa única computadora.
4. **Privacidad y control:** los datos no salen de la máquina del cliente.

## Escalabilidad y visión de futuro

El sistema se diseña para **escalar** — de ahí la arquitectura en capas (ver `tech-stack.md`). El despliegue actual es local/embebido, pero el diseño deja preparado el camino:

- **¿Por qué por capas?** Para poder cambiar la infraestructura (base de datos, transporte) **sin tocar la lógica de negocio** (`domain` / `application`). El día que cambiemos de BD, solo se reemplazan las implementaciones `*Impl` de `infrastructure`.
- **Etapa A — Hoy:** SQLite embebida, un `inventario.db` por instalación, una PC por negocio.
- **Etapa B — Versión con BD en la nube (preparada):** variante del sistema que **no depende del archivo local**; el inventario vive en una base de datos en la nube (accesible por red).
- **Etapa C — Multiusuario con concurrencia:** varios usuarios (incluso desde distintas computadoras) consultan y modifican el mismo inventario simultáneamente, con datos **consistentes** (transacciones y control de concurrencia).
- **Etapa D — Asistente con inteligencia artificial y análisis económicos:** asistencia inteligente para el manejo del inventario y análisis económicos del negocio (tendencias, rentabilidad, decisiones) basados en sus propios datos.

Cada etapa se documentará como feature formal en `spec/features/` cuando se decida implementarla.

## Principios del proyecto

1. **Cero configuración:** la app debe funcionar apenas se instala, sin pasos manuales.
2. **Local first:** sin servicios externos; todo vive en la computadora del negocio.
3. **Seguridad por defecto:** nunca almacenar contraseñas en texto plano; hashing con PBKDF2.
4. **Datos resguardados:** el `inventario.db` vive en una carpeta de datos del usuario (no junto al ejecutable), para sobrevivir a reinstalaciones y actualizaciones.
5. **Cambios pequeños y verificables:** cada feature se desarrolla desde su carpeta en `spec/features/`, se registra en el `CHANGELOG.md` y sigue el flujo de ramas del `Manual.md`.
6. **Escalabilidad:** todo se diseña para poder migrar la infraestructura (BD local → nube, multiusuario, concurrencia) sin reescribir la lógica de negocio.