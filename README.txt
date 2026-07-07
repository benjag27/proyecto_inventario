# Sistema de Gestión de Inventario - Phora

Aplicación de escritorio para la gestión de productos, desarrollada en JavaFX con soporte de base de datos local mediante SQLite.

---

## Descargas e Instalación

Los instaladores empaquetan su propio entorno de ejecución, por lo que **no es necesario tener Java instalado en el sistema** para correr la aplicación.

Las versiones estables se encuentran disponibles en la sección de **Releases** de este repositorio:

* **Windows:** Descargar el archivo `.msi` y ejecutar el asistente de instalación.
* **Linux (Ubuntu/Debian):** Descargar el archivo `.deb` e instalarlo desde la terminal con:
    ```bash
    sudo apt install ./inventariophora_1.0.0-1_amd64.deb
    ```

---

## Tecnologías

* Java 17
* JavaFX (Interfaz Gráfica)
* SQLite (Base de Datos Local)
* Maven (Gestión de dependencias)

---

## Compilación y Construcción (Desarrollo)

Para replicar el entorno de desarrollo, compilar el código fuente o generar nuevos instaladores nativos, seguir los pasos a continuación.

### Prerrequisitos
* Java Development Kit (JDK) 17
* Apache Maven

### 1. Clonar el repositorio y compilar
```bash
git clone https://github.com/benjag27/proyecto_inventario.git
cd proyecto_inventario-main
mvn clean javafx:jlink
