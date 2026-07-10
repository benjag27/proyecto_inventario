package org.phora.infrastructure.persistence;

import org.phora.domain.service.LoginService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
/**
 * Configuración de conexión a SQLite.
 * El archivo inventario.db se crea automáticamente en la carpeta
 * donde corre la app — no requiere instalación de ningún servidor.
 */
public class BsConfig {

    // Ruta relativa: el .db queda junto al ejecutable
    private static final String URL;
    private static final Logger logger = Logger.getLogger(LoginService.class.getName());
    // 2. BLOQUE ESTÁTICO DE INICIALIZACIÓN DINÁMICA
    static {



        URL = "jdbc:sqlite:inventario.db";
        initDB();
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            logger.info("Error en getConnection(): " + e.getMessage());
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

        String createAuditLogs = """
            CREATE TABLE IF NOT EXISTS audit_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                action_type TEXT NOT NULL,
                description TEXT NOT NULL,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            """;

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(createProducts);
            stmt.execute(createUsers);
            stmt.execute(createAuditLogs);
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }
}