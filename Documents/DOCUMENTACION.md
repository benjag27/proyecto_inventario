# Inventario - Documentación del Proyecto

## 1. Descripción general

Plantilla genérica de sistema de inventario, desarrollada en JavaFX, pensada para adaptarse a distintos tipos de negocio (ferretería, farmacia, kiosco, etc.) sin tener que reescribir la lógica central cada vez.

El alcance actual es **CRUD simple de productos**: alta, baja, modificación y consulta. No incluye historial de movimientos de stock (entradas/salidas) en esta etapa.

## 2. Stack tecnológico

| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje principal |
| JavaFX 21 | Interfaz de escritorio (sin FXML, construida en código Java puro) |
| Maven | Gestión de dependencias y build |
| JDBC | Acceso a base de datos |
| JUnit 5 | Tests unitarios |

## 3. Arquitectura

El proyecto está organizado en 4 capas, cada una con una responsabilidad clara y sin mezclarse entre sí:

```
presentation  →  application  →  domain  ←  infrastructure
```

- **`presentation`** depende de **`application`**
- **`application`** depende de **`domain`**
- **`infrastructure`** *implementa* las interfaces de **`domain`**
- **`domain`** no depende de ninguna otra capa — es el núcleo

> Regla clave: `domain` nunca importa nada de `infrastructure`. Esto permite cambiar la base de datos o la forma de persistencia sin tocar las reglas de negocio.

### Responsabilidad de cada capa

| Capa | Responsabilidad | Ejemplo en el proyecto |
|---|---|---|
| `domain/model` | Qué ES una entidad y sus reglas propias | `Product` |
| `domain/repository` | Contrato de persistencia (interfaz, sin implementación) | `ProductRepository` |
| `domain/service` | Coordina varias entidades/repositorios para una regla de negocio | *(no usado aún — ver sección 7)* |
| `application` | Traduce una acción del usuario en llamadas al dominio | `AddProduct`, `UpdateProduct`, `DeleteProduct`, `FindProduct`, `CheckStock` |
| `infrastructure/persistence` | Implementación real de cómo se guardan/leen los datos | `ProductRepositoryImpl`, `BsConfig` |
| `infrastructure` (raíz) | Ensamblado de dependencias (qué implementación se usa en cada caso) | `AppContext` |
| `presentation` | Vistas JavaFX, sin FXML | `ProductView`, `SceneManager` |

## 4. Estructura de carpetas

```
src/main/java/org/phora/
├── Main.java
├── application/
│   ├── AddProduct.java
│   ├── UpdateProduct.java
│   ├── DeleteProduct.java
│   ├── FindProduct.java
│   └── CheckStock.java
├── domain/
│   ├── model/
│   │   └── Product.java
│   └── repository/
│       └── ProductRepository.java
├── infrastructure/
│   ├── AppContext.java
│   └── persistence/
│       ├── BsConfig.java
│       └── ProductRepositoryImpl.java
└── presentation/
    ├── ProductView.java
    └── SceneManager.java
```

## 5. Convenciones de código

- **Casos de uso (`application/`)**: nombrados como `Verbo + Sustantivo` (`AddProduct`, `DeleteProduct`). Mantener esta consistencia al agregar nuevos.
- **Repositorios**: la interfaz vive en `domain/repository/`, la implementación en `infrastructure/persistence/` con el sufijo `Impl` (`ProductRepository` → `ProductRepositoryImpl`).
- **Búsqueda por id vs. por nombre**:
  - Operaciones que modifican o eliminan (`update`, `delete`) siempre identifican el registro por `id`, nunca por nombre, para evitar ambigüedad si hay nombres duplicados.
  - `findByName` se usa solo para que el usuario *encuentre* un producto desde la UI, y devuelve una lista (puede haber 0, 1 o varios resultados).
  - `findById` devuelve `Optional<Product>`, ya que el id buscado puede no existir; evita usar `null` o excepciones para ese caso esperado.

## 6. Cómo correr el proyecto

**Requisitos**: JDK 17+, Maven.

```bash
mvn clean install
mvn javafx:run
```

## 7. Decisiones de diseño

- **Sin FXML**: las vistas se construyen en código Java puro dentro de `presentation/`, sin separar Controller/ViewModel en archivos distintos (MVVM "liviano"). Se eligió así para mantener la cantidad de archivos baja en una app de este tamaño.
- **Sin `domain/service` por ahora**: como el alcance actual es CRUD simple (cada operación toca una sola entidad), los casos de uso llaman directo al `ProductRepository`. Si en el futuro una operación necesita coordinar más de una entidad o repositorio (ej: registrar movimientos de stock), ahí se introduce un `service` con esa lógica puntual.
- **`Optional<Product>` en `findById`**: evita el uso de `null` y obliga a quien llama a manejar explícitamente el caso "no encontrado".
- **Inyección de dependencias manual (`AppContext`)**: sin frameworks como Spring. Es el único archivo del proyecto que conoce todas las implementaciones concretas y las conecta entre capas.

## 8. Pendientes / Roadmap

- [ ] Definir si se necesita historial de movimientos de stock (`MovimientoStock` + `InventoryService`) — no incluido en el alcance actual.
- [ ] Conectar `BsConfig` a una base de datos real (actualmente a definir/confirmar motor: MySQL, PostgreSQL, SQLite).
- [ ] Revisar si `delete` en `ProductRepository` debería recibir `int id` en vez de `Product` completo, para simplificar `DeleteProduct`.
- [ ] Agregar tests de integración para `ProductRepositoryImpl` una vez conectada la base de datos real.
