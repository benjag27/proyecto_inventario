package org.phora.infrastructure.persistence;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
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
 *
 * En la primera ejecución no se crea ni se siembra nada por código: se copia
 * la base pre-sembrada que viaja como recurso (/db/inventario_base.db), que ya
 * incluye el esquema completo y el usuario administrador. Así el código de
 * distribución no contiene credenciales ni lógica de seed.
 */
public class BsConfig {

    // Ruta dinámica: la BD se crea en ~/.phora_inventario/, junto al programa
    private static final String DB_DIR = System.getProperty("user.home")
            + File.separator + ".phora_inventario";
    private static final String DB_FILE = "inventario.db";
    private static final String BASE_DB_RESOURCE = "/db/inventario_base.db";
    private static final String URL;
    private static final Logger logger = Logger.getLogger(BsConfig.class.getName());

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
     * En la primera ejecución copia la base pre-sembrada (esquema + administrador)
     * desde los recursos de la aplicación. En instalaciones posteriores solo aplica
     * las migraciones pendientes, sin tocar los datos del usuario.
     */
    private static void initDB() {
        File dir = new File(DB_DIR);
        if (!dir.exists() && dir.mkdirs()) {
            logger.info("Directorio de base de datos creado: " + DB_DIR);
        }

        File dbFile = new File(DB_DIR, DB_FILE);
        if (!dbFile.exists()) {
            copyBaseDatabase(dbFile);
        }

        migrate();
    }

    private static void copyBaseDatabase(File dbFile) {
        try (InputStream base = BsConfig.class.getResourceAsStream(BASE_DB_RESOURCE)) {
            if (base == null) {
                throw new IllegalStateException(
                        "No se encontró la base de datos base en los recursos: " + BASE_DB_RESOURCE);
            }
            Files.copy(base, Path.of(dbFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
            logger.info("Base de datos inicial creada desde la base de distribución");
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la base de datos inicial", e);
        }
    }

    /**
     * Aplica las migraciones de esquema sobre instalaciones ya existentes,
     * de modo que una base creada por versiones anteriores siga funcionando.
     */
    private static void migrate() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            if (!columnExists(conn, "users", "must_change_password")) {
                stmt.execute("ALTER TABLE users ADD COLUMN must_change_password INTEGER NOT NULL DEFAULT 0");
                logger.info("Migración aplicada: users.must_change_password");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al migrar la base de datos", e);
        }
    }

    private static boolean columnExists(Connection conn, String table, String column) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }
}
