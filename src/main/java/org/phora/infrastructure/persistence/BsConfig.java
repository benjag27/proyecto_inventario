package org.phora.infrastructure.persistence;

import org.phora.domain.service.LoginService;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Configuración de conexión a SQLite.
 * La base de datos vive en una carpeta oculta dentro del home del usuario
 * (~/.phora_inventario/inventario.db), independiente del directorio desde
 * donde se ejecute la app, evitando sobrescrituras por sincronizaciones o
 * actualizaciones, y soportando instalaciones con permisos restringidos
 * (ej: Program Files en Windows). No requiere instalación de ningún servidor.
 */
public class BsConfig {

    // Ruta dinámica: la BD se crea en ~/.phora_inventario/, junto al programa
    private static final String DB_DIR = System.getProperty("user.home")
            + File.separator + ".phora_inventario";
    private static final String DB_FILE = "inventario.db";
    private static final String URL;
    private static final Logger logger = Logger.getLogger(LoginService.class.getName());

    // Credenciales por defecto de primera ejecución (seed)
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    static {
        URL = "jdbc:sqlite:" + DB_DIR + File.separator + DB_FILE;
        initDB();
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            // Habilita las claves foráneas (PRAGMA es por conexión en SQLite).
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            return conn;
        } catch (SQLException e) {
            logger.info("Error en getConnection(): " + e.getMessage());
            throw new RuntimeException("No se pudo conectar a la base de datos", e);
        }
    }

    /**
     * Crea las tablas si no existen todavía y siembra el usuario administrador
     * por defecto la primera vez (cuando la tabla users está vacía).
     */
    private static void initDB() {
        File dir = new File(DB_DIR);
        if (!dir.exists() && dir.mkdirs()) {
            logger.info("Directorio de base de datos creado: " + DB_DIR);
        }

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

        String createProductBarcodes = """
            CREATE TABLE IF NOT EXISTS product_barcodes (
                id         INTEGER PRIMARY KEY AUTOINCREMENT,
                product_id INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                barcode    TEXT    NOT NULL UNIQUE
            );
            """;

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(createProducts);
            stmt.execute(createUsers);
            stmt.execute(createAuditLogs);
            stmt.execute(createProductBarcodes);
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }

        seedDefaultAdmin();
    }

    /**
     * Crea el usuario administrador por defecto únicamente si la tabla users
     * está vacía (primera ejecución / instalación limpia).
     */
    private static void seedDefaultAdmin() {
        String countSql = "SELECT COUNT(*) FROM users";
        String insertSql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String hash = LoginService.hashPassword(DEFAULT_ADMIN_PASSWORD);
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, DEFAULT_ADMIN_USERNAME);
                    ps.setString(2, hash);
                    ps.executeUpdate();
                }
                logger.info("Usuario administrador por defecto creado en primera ejecución");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al sembrar el usuario administrador", e);
        }
    }
}