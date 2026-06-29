package org.phora.domain.repository;

import org.phora.domain.model.User;
import java.util.Optional;

public interface UserRepository {
  Optional<User> findByUsername(String username);
}