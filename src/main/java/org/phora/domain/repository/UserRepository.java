package org.phora.domain.repository;

import org.phora.domain.model.User;
import java.util.Optional;

public interface UserRepository {
  // Como es un solo usuario, lo buscamos por su username
  Optional<User> findByUsername(String username);
}