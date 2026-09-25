package org.phora.domain.service;

import org.phora.domain.model.User;
import org.phora.domain.repository.UserRepository;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Optional;

public class LoginService {

  private final UserRepository userRepository;

  // Parámetros de seguridad estándar (NIST)
  private static final int ITERATIONS = 65536;
  private static final int KEY_LENGTH = 256;
  private static final int SALT_LENGTH = 16;

  public LoginService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean authenticate(String username, String rawPassword) {
    Optional<User> userOptional = userRepository.findByUsername(username);

    if (userOptional.isEmpty()) {
      return false;
    }

    User user = userOptional.get();

    return verifyPassword(rawPassword, user.getPasswordHash());
  }

  /**
   * Indica si el usuario debe cambiar su contraseña en el próximo inicio
   * de sesión (usuarios recién sembrados en la instalación base).
   */
  public boolean mustChangePassword(String username) {
    return userRepository.findByUsername(username)
            .map(User::isMustChangePassword)
            .orElse(false);
  }

  /**
   * Reemplaza la contraseña del usuario y limpia la marca de cambio pendiente.
   * Devuelve false si la nueva contraseña no cumple la longitud mínima.
   */
  public boolean changePassword(String username, String rawPassword) {
    if (rawPassword == null || rawPassword.length() < 6) {
      return false;
    }
    userRepository.updatePassword(username, hashPassword(rawPassword), false);
    return true;
  }

  /**
   * Genera un hash PBKDF2 para una contraseña plana.
   * Úsalo al crear o registrar un usuario nuevo.
   */
  public static String hashPassword(String rawPassword) {
    try {
      SecureRandom random = new SecureRandom();
      byte[] salt = new byte[SALT_LENGTH];
      random.nextBytes(salt);

      byte[] hash = pbkdf2(rawPassword.toCharArray(), salt);

      // Retorna un string con formato ITERACIONES:SAL:HASH
      return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    } catch (Exception e) {
      throw new RuntimeException("Error al generar el hash criptográfico", e);
    }
  }

  private boolean verifyPassword(String rawPassword, String storedHash) {
    try {
      String[] parts = storedHash.split(":");
      if (parts.length != 3) return false;

      int iterations = Integer.parseInt(parts[0]);
      byte[] salt = Base64.getDecoder().decode(parts[1].trim());
      byte[] hash = Base64.getDecoder().decode(parts[2].trim());

      // PASAMOS las iteraciones y el tamaño del hash recuperados de la BD
      byte[] testHash = pbkdf2(rawPassword.toCharArray(), salt, iterations, hash.length * 8);

      int diff = hash.length ^ testHash.length;
      for (int i = 0; i < hash.length && i < testHash.length; i++) {
        diff |= hash[i] ^ testHash[i];
      }
      return diff == 0;
    } catch (Exception e) {
      return false;
    }
  }

  // Sobrecargamos pbkdf2 para la creación de nuevos hashes
  private static byte[] pbkdf2(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
    return pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);
  }

  // Método definitivo que procesa dinámicamente según lo requerido
  private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
    PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    return skf.generateSecret(spec).getEncoded();
  }
}