package org.phora.infrastructure.persistence;

import org.phora.domain.model.User;
import org.phora.domain.repository.UserRepository;
import org.phora.domain.service.LoginService;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BsConfigTest {

  private static final Path BASE_DB = Path.of("src/main/resources/db/inventario_base.db");

  @Test
  void distributedDatabaseContainsAdminWithPasswordChangePending() throws Exception {
    // Valida el artefacto que realmente se distribuye, no la base local ya migrada.
    assertTrue(Files.exists(BASE_DB), "La base de distribución debe existir");

    try (InputStream base = BsConfigTest.class.getResourceAsStream("/db/inventario_base.db")) {
      assertNotNull(base, "La base de distribución debe ir empaquetada en los recursos");
      Path temporal = Files.createTempFile("inventario_base", ".db");
      try {
        Files.copy(base, temporal, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        try (Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:" + temporal);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT username, password_hash, must_change_password FROM users")) {
          assertTrue(rs.next(), "La base de distribución debe traer el usuario admin");
          assertEquals("admin", rs.getString("username"));
          assertTrue(rs.getString("password_hash").contains(":"),
              "El hash debe tener formato iteraciones:salt:hash");
          assertFalse("admin123".equals(rs.getString("password_hash")),
              "La contraseña no debe almacenarse en texto plano");
          assertEquals(1, rs.getInt("must_change_password"),
              "El admin de fábrica debe estar obligado a cambiar la contraseña");
        }
      } finally {
        Files.deleteIfExists(temporal);
      }
    }
  }

  @Test
  void distributedAdminCredentialsAreValid() throws Exception {
    // admin/admin123 debe autenticar contra el hash sembrado en la distribución.
    UserRepository repo = new UserRepositoryImpl();
    LoginService loginService = new LoginService(repo);

    Optional<User> admin = repo.findByUsername("admin");
    assertTrue(admin.isPresent(), "El usuario admin debe existir en la base activa");

    String stored = admin.get().getPasswordHash();
    assertTrue(loginService.authenticate("admin", "admin123"),
        "La base de distribución debe autenticar admin/admin123");
    assertFalse(loginService.authenticate("admin", "claveIncorrecta"),
        "Una contraseña incorrecta no debe autenticar");
    assertNotNull(stored);
  }

  @Test
  void changePasswordReplacesHashAndClearsPendingFlag() {
    UserRepository repo = new UserRepositoryImpl();
    LoginService loginService = new LoginService(repo);
    String usuario = "test_cambio_clave";

    crearUsuarioTemporal(usuario, loginService.hashPassword("claveVieja"));

    try {
      assertTrue(loginService.mustChangePassword(usuario), "El usuario temporal nace con cambio pendiente");

      assertTrue(loginService.changePassword(usuario, "claveNueva1"));
      assertFalse(loginService.mustChangePassword(usuario), "El cambio debe limpiar la marca pendiente");
      assertTrue(loginService.authenticate(usuario, "claveNueva1"), "Debe autenticar con la clave nueva");
      assertFalse(loginService.authenticate(usuario, "claveVieja"), "La clave vieja debe quedar invalidada");
    } finally {
      borrarUsuarioTemporal(usuario);
    }
  }

  @Test
  void changePasswordRejectsTooShortPassword() {
    UserRepository repo = new UserRepositoryImpl();
    LoginService loginService = new LoginService(repo);
    String usuario = "test_cambio_corto";

    crearUsuarioTemporal(usuario, loginService.hashPassword("claveVieja"));

    try {
      assertFalse(loginService.changePassword(usuario, "12345"), "Debe rechazar claves de menos de 6 caracteres");
      assertTrue(loginService.authenticate(usuario, "claveVieja"), "La clave debe quedar sin cambios");
    } finally {
      borrarUsuarioTemporal(usuario);
    }
  }

  private void crearUsuarioTemporal(String username, String passwordHash) {
    try (Connection conn = BsConfig.getConnection();
         PreparedStatement ps = conn.prepareStatement(
                 "INSERT OR REPLACE INTO users (username, password_hash, must_change_password) VALUES (?, ?, 1)")) {
      ps.setString(1, username);
      ps.setString(2, passwordHash);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo crear el usuario temporal", e);
    }
  }

  private void borrarUsuarioTemporal(String username) {
    try (Connection conn = BsConfig.getConnection();
         PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE username = ?")) {
      ps.setString(1, username);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo borrar el usuario temporal", e);
    }
  }
}
