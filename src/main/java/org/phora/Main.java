package org.phora;

import org.phora.infrastructure.AppContext;
import org.phora.presentation.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // El contexto vive durante toda la ejecución de la app
    private AppContext context;

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        this.context = new org.phora.infrastructure.AppContext();
        try (java.sql.Connection conn = org.phora.infrastructure.persistence.BsConfig.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {

            // Verificamos si la tabla ya tiene al usuario admin
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE username = 'admin'");
            if (rs.next() && rs.getInt(1) == 0) {

                // Usamos el repositorio que ya vive adentro del AppContext
                org.phora.domain.repository.UserRepository userRepo = new org.phora.infrastructure.persistence.UserRepositoryImpl();
                org.phora.domain.service.LoginService loginService = new org.phora.domain.service.LoginService(userRepo);

                // Generamos el hash con tu PBKDF2
                String realPbkdf2Hash = loginService.hashPassword("admin123");

                // Insertamos en la BD de forma directa y segura
                java.sql.PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO users (username, password_hash) VALUES (?, ?)"
                );
                pstmt.setString(1, "admin");
                pstmt.setString(2, realPbkdf2Hash);
                pstmt.executeUpdate();

                System.out.println("=== Usuario 'admin' inyectado con éxito usando tu PBKDF2 ===");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Aviso: No se pudo verificar/inyectar el usuario admin: " + e.getMessage());
        }

        SceneManager sceneManager = new SceneManager(primaryStage, context);

        // 3. Arrancamos el flujo
        sceneManager.showLogin();

    }
}