package org.phora.infrastructure.persistence;

import org.phora.domain.service.LoginService;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BsConfigTest {

  @Test
  void seedsDefaultAdminWithValidCredentials() throws Exception {
    // Fuerza la carga de BsConfig (static block) y la provisión de la DB
    LoginService loginService = new LoginService(new UserRepositoryImpl());
    Connection conn = BsConfig.getConnection();

    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
      rs.next();
      assertEquals(1, rs.getInt(1), "Debe existir exactamente un usuario tras el seed");
    }

    assertTrue(loginService.authenticate("admin", "admin123"),
        "El seed debe autenticar admin/admin123 (primera ejecución)");
  }

  @Test
  void seededAdminHashIsNotPlaintext() throws Exception {
    Connection conn = BsConfig.getConnection();

    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT password_hash FROM users WHERE username='admin'")) {
      assertTrue(rs.next(), "El usuario admin debe existir");
      String hash = rs.getString(1);
      assertTrue(hash.contains(":"), "El hash debe tener formato iteraciones:salt:hash");
      assertTrue(!hash.equals("admin123"), "La contraseña no debe almacenarse en texto plano");
    }
  }
}