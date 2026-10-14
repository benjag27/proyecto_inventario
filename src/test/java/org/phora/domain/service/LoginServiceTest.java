package org.phora.domain.service;

import org.phora.domain.model.User;
import org.phora.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba la normalización del usuario en LoginService sin tocar la base real
 * (usa un repositorio en memoria para aislar el comportamiento).
 */
class LoginServiceTest {

  private static class InMemoryUserRepository implements UserRepository {
    private User user;

    InMemoryUserRepository(User user) {
      this.user = user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
      if (user != null && user.getUsername().equals(username)) {
        return Optional.of(user);
      }
      return Optional.empty();
    }

    @Override
    public void updatePassword(String username, String passwordHash, boolean mustChangePassword) {
      this.user = new User(username, passwordHash, mustChangePassword);
    }
  }

  private LoginService serviceWithAdmin(String rawPassword, boolean mustChange) {
    String hash = LoginService.hashPassword(rawPassword);
    return new LoginService(new InMemoryUserRepository(new User("admin", hash, mustChange)));
  }

  @Test
  void authenticatesWithExactUsername() {
    assertTrue(serviceWithAdmin("admin123", false).authenticate("admin", "admin123"));
  }

  @Test
  void authenticatesDespiteSurroundingWhitespaceInUsername() {
    LoginService service = serviceWithAdmin("admin123", false);
    assertTrue(service.authenticate("admin ", "admin123"), "Un espacio al final no debe impedir el login");
    assertTrue(service.authenticate("  admin", "admin123"), "Los espacios al inicio tampoco");
    assertTrue(service.authenticate("\tadmin ", "admin123"), "Tabulaciones y espacios combinados tampoco");
  }

  @Test
  void rejectsWrongPassword() {
    assertFalse(serviceWithAdmin("admin123", false).authenticate("admin", "123"));
  }

  @Test
  void rejectsNullInputsWithoutThrowing() {
    LoginService service = serviceWithAdmin("admin123", false);
    assertFalse(service.authenticate(null, "admin123"));
    assertFalse(service.authenticate("admin", null));
  }

  @Test
  void mustChangePasswordNormalizesUsername() {
    assertTrue(serviceWithAdmin("admin123", true).mustChangePassword("admin "));
  }

  @Test
  void changePasswordNormalizesUsername() {
    LoginService service = serviceWithAdmin("claveVieja", true);

    assertTrue(service.changePassword("admin ", "claveNueva1"));
    assertTrue(service.authenticate("admin", "claveNueva1"), "Debe autenticar con la clave nueva");
    assertFalse(service.mustChangePassword("admin"), "El cambio debe limpiar la marca pendiente");
  }
}
