PHORA — INVENTORY MANAGEMENT SYSTEM
A lightweight desktop application built with JavaFX and SQLite for local, zero-configuration warehouse, product tracking, and operational auditing.

DOWNLOADS AND INSTALLATION
The packages include their own standalone runtime — you do not need Java installed on your system to run the application.
Download the latest stable binaries from the Releases section (https://github.com/benjag27/proyecto_inventario/releases).

Platform: Linux (portable, no installation required) — recommended
Asset: Inventario-1.0.0-linux-x64.tar.gz
Steps: extract the archive and run Inventario/bin/Inventario (double-click it, or run ./Inventario/bin/Inventario from a terminal).
The folder is self-contained and can be copied to another Linux machine as-is.

Platform: Linux (Debian/Ubuntu)
Asset: inventario_1.0.0-1_amd64.deb
Install: sudo apt install ./inventario_1.0.0-1_amd64.deb
Uninstall: sudo apt remove inventario

Platform: Windows
Installer: Download the .msi file and follow the installation wizard.

TECH STACK AND ARCHITECTURE
Language: Java 17
GUI Framework: JavaFX 21
Database: SQLite (Embedded)
Build System: Apache Maven
Code Quality: SonarQube Cloud Integration

KEY FEATURES

Core Inventory Engine: Full CRUD system for managing product lines, pricing, and live stock counts.

Barcode Support: Multiple alphanumeric barcodes per product (scanned or typed), with input validation and uniqueness checks.

Operational Audit Log: Automatically tracks all warehouse movements (creating, modifying, or deleting products), logging the exact timestamp and the active operator's username inside the Movimientos dashboard.

Secure Authentication: Multi-user system featuring advanced PBKDF2 with HMAC-SHA256 password hashing.

Isolated User Storage: Databases are managed securely inside the user's home directory (~/.phora_inventario/ on Linux) to avoid local data overwrites during code synchronization or updates.

Real-time Search: Instant, partial-name text filtering as you type.

Modern GUI: Clean, responsive dark-themed dashboard built entirely with JavaFX.

DEVELOPMENT SETUP
Prerequisites:

Java Development Kit (JDK) 17

Apache Maven 3.x or higher

1. Clone and compile:
   git clone https://github.com/benjag27/proyecto_inventario.git
   cd proyecto_inventario
   mvn clean install

2. Run in development mode:
   mvn javafx:run

3. Generate runtime image:
   Builds a stripped-down, platform-specific custom runtime using jlink:
   mvn javafx:jlink

The output is placed in target/inventario-app/. Run it with:
Linux / Mac: ./target/inventario-app/bin/inventario
Windows: target\inventario-app\bin\inventario.bat

4. Generate the Linux packages (portable app-image + .deb):
   scripts/empaquetar-linux.sh

The output is placed in target/installer/:
   Inventario/                          portable app-image
   Inventario-1.0.0-linux-x64.tar.gz    release asset (portable)
   inventario_1.0.0-1_amd64.deb         Debian/Ubuntu installer

DEFAULT ACCESS CREDENTIALS
Use the following credentials to log in after a clean setup, a code reset, or a first install:
Username: admin
Password: admin123
Aviso de Database Bootstrapping: For development convenience, these credentials are automatically provisioned and securely hashed right out of the Main application execution lifecycle whenever an empty database instance is found.

RELEASE HISTORY
v1.0.0 — Initial Release (Current)
Linux assets: Inventario-1.0.0-linux-x64.tar.gz (portable) and inventario_1.0.0-1_amd64.deb.

Core Inventory Engine — Full CRUD system for managing product lines, pricing, and live stock counts.

Operational Audit Log — Added the "Movimientos" Dashboard that automatically tracks every creation, update, and deletion with timestamp and operator username.

Secure Authentication — Multi-user system featuring advanced PBKDF2 with HMAC-SHA256 password hashing.

Isolated User Storage — Database safely managed inside the user's home directory (~/.phora_inventario/) to secure data across updates.

Real-time Search — Instant partial-name text filtering as you type.

Modern GUI — Clean, dark-themed dashboard built entirely with JavaFX
