package org.phora.infrastructure.persistence;

import org.phora.domain.model.User;
import org.phora.domain.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

public class UserRepositoryImpl implements UserRepository {
  private static final Logger logger = Logger.getLogger(UserRepositoryImpl.class.getName());

  @Override
  public Optional<User> findByUsername(String username) {
    String sql = "SELECT username, password_hash, must_change_password FROM users WHERE username = ?";
    try (Connection conn = BsConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(new User(
                  rs.getString("username"),
                  rs.getString("password_hash"),
                  rs.getInt("must_change_password") == 1));
        }
      }
    } catch (SQLException e) {
      logger.info("Error finding user: " + e.getMessage());
    }
    return Optional.empty();
  }

  @Override
  public void updatePassword(String username, String passwordHash, boolean mustChangePassword) {
    String sql = "UPDATE users SET password_hash = ?, must_change_password = ? WHERE username = ?";
    try (Connection conn = BsConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, passwordHash);
      stmt.setInt(2, mustChangePassword ? 1 : 0);
      stmt.setString(3, username);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo actualizar la contraseña", e);
    }
  }
}
