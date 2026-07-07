package org.phora.infrastructure.persistence;

import org.phora.domain.model.User;
import org.phora.domain.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

  @Override
  public Optional<User> findByUsername(String username) {
    String sql = "SELECT username, password_hash FROM users WHERE username = ?";
    try (Connection conn = BsConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(new User(
                  rs.getString("username"),
                  rs.getString("password_hash")));
        }
      }
    } catch (SQLException e) {
      System.err.println("Error finding user: " + e.getMessage());
    }
    return Optional.empty();
  }
}