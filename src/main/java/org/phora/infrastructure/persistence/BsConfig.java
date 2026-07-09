package org.phora.infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Configuración de conexión a SQLite.
 * El archivo inventario.db se crea automáticamente en la carpeta
 * donde corre la app — no requiere instalación de ningún servidor.
 */
public class BsConfig {

    // Ruta relativa: el .db queda junto al ejecutable
    private static final String URL;

    // 2. BLOQUE ESTÁTICO DE INICIALIZACIÓN DINÁMICA
    static {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        String appDir;

        if (os.contains("win")) {
            String appData = System.getenv("LOCALAPPDATA");
            if (appData == null) {
                appDir = userHome + java.io.File.separator + "PhoraInventario";
            } else {
                appDir = appData + java.io.File.separator + "PhoraInventario";
            }
        } else {
            appDir = userHome + java.io.File.separator + ".phora_inventario";
        }

        java.io.File dir = new java.io.File(appDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Asignamos la constante final de forma definitiva
        URL = "jdbc:sqlite:" + appDir + java.io.File.separator + "inventario.db";

        // Inicializamos las tablas
        initDB();
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Error en getConnection(): " + e.getMessage());
            throw new RuntimeException("No se pudo conectar a la base de datos", e);
        }
    }

    /**
     * Crea las tablas si no existen todavía.
     * Se ejecuta una sola vez al arrancar la app.
     */
    private static void initDB() {
        String createProducts = """
                CREATE TABLE IF NOT EXISTS products (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    name  TEXT    NOT NULL,
                    price REAL    NOT NULL DEFAULT 0.0,
                    stock INTEGER NOT NULL DEFAULT 0
                )
                """;

        String createUsers = """
                CREATE TABLE IF NOT EXISTS users (
                    id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    username      TEXT    NOT NULL UNIQUE,
                    password_hash TEXT    NOT NULL
                )
                """;

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(createProducts);
            stmt.execute(createUsers);
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }
}