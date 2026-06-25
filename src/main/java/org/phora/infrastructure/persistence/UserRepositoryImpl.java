package org.phora.infrastructure.persistence;

import org.mindrot.jbcrypt.BCrypt;
import org.phora.domain.model.User;
import org.phora.domain.repository.UserRepository;
import java.sql.*;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

  @Override
  public Optional<User> findByUsername(String username) {
    String sql = "SELECT username, password_hash FROM users WHERE username = ?";

    // Aquí usarías tu conexión a BD (puedes inyectarla o usar un gestor de
    // conexiones)
    try (Connection conn = BsConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, username);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        return Optional.of(new User(
            rs.getString("username"),
            rs.getString("password_hash")));
      }
    } catch (SQLException e) {
      System.err.println("Error buscando usuario: " + e.getMessage());
    }
    return Optional.empty();
  }

}