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
    private static final String URL = "jdbc:sqlite:inventario.db";

    static {
        // Al cargar la clase, nos aseguramos de que las tablas existan.
        // Si el archivo no existe, SQLite lo crea solo.
        initDB();
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo conectar a la base de datos", e);
        }
    }

    /**
     * Crea las tablas si no existen todavía.
     * Se ejecuta una sola vez al arrancar la app.
     */
    private static void initDB() {
        String crearProductos = """
                CREATE TABLE IF NOT EXISTS products (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    name  TEXT    NOT NULL,
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
            stmt.execute(crearProductos);
            stmt.execute(createUsers);
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }
}