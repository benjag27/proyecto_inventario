# Phora — Inventory Management System

> A lightweight desktop application built with JavaFX and SQLite for local, zero-configuration warehouse and product tracking.

---

## Downloads & Installation

The installers package their own standalone runtime — **you do not need Java installed on your system** to run the application.

Download the latest stable binaries from the **[Releases](https://github.com/benjag27/proyecto_inventario/releases)** section:

| Platform | Installer |
|---|---|
| **Windows** | Download the `.msi` file and follow the installation wizard |
| **Linux (Debian/Ubuntu)** | Download the `.deb` package and run: `sudo apt install ./inventariophora_1.0.0-1_amd64.deb` |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| GUI Framework | JavaFX 21 |
| Database | SQLite (Embedded) |
| Build System | Apache Maven |

---

## Development Setup

### Prerequisites

- Java Development Kit (JDK) 17
- Apache Maven 3.x or higher

### 1. Clone and compile

```bash
git clone https://github.com/benjag27/proyecto_inventario.git
cd proyecto_inventario
mvn clean install
```

### 2. Run in development mode

```bash
mvn javafx:run
```

### 3. Generate runtime image

Builds a stripped-down, platform-specific custom runtime using `jlink`:

```bash
mvn javafx:jlink
```

The output is placed in `target/inventario-app/`. Run it with:

```bash
# Linux / Mac
./target/inventario-app/bin/inventario

# Windows
target\inventario-app\bin\inventario.bat
```

---

## Default Access Credentials

Use the following credentials to log in after a clean setup or first install:

| Field | Value |
|---|---|
| Username | `admin` |
| Password | `admin123` |

> ⚠️ These credentials are automatically provisioned into your local database on the very first launch.

---

## Release History

### v1.0.0-beta — First Beta Release

A lightweight, cross-platform desktop application designed for efficient warehouse and inventory management.

- **Secure Authentication** — Multi-user system with PBKDF2WithHmacSHA256 password hashing
- **Product Management** — Full CRUD operations for managing products, pricing, and live stock levels
- **Real-time Search** — Partial name search that filters results as you type
- **Persistent SQLite Storage** — Auto-managed embedded database with zero-configuration deployment on Windows and Linux
- **Modern GUI** — Clean, dark-themed layout built with JavaFX
