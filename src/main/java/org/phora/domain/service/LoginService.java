package org.phora.domain.service;

import org.phora.domain.model.User;
import org.phora.domain.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Optional;

public class LoginService {
  private final UserRepository userRepository;

  public LoginService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean authenticate(String username, String rawPassword) {
    Optional<User> userOptional = userRepository.findByUsername(username);

    // Si el usuario no existe, devolvemos false inmediatamente
    if (userOptional.isEmpty()) {
      return false;
    }

    User user = userOptional.get();

    // BCrypt compara el password plano con el hash guardado automáticamente
    // checkpw maneja la sal (salt) internamente, por eso es seguro
    String hash = BCrypt.hashpw("admin123", BCrypt.gensalt());
    System.out.println(hash);
    return BCrypt.checkpw(rawPassword, user.getPasswordHash());
  }
}