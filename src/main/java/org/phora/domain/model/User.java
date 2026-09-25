package org.phora.domain.model;

public class User {
  private final String username;
  private final String passwordHash;
  private final boolean mustChangePassword;

  public User(String username, String passwordHash, boolean mustChangePassword) {
    this.username = username;
    this.passwordHash = passwordHash;
    this.mustChangePassword = mustChangePassword;
  }

  public String getUsername() {
    return username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public boolean isMustChangePassword() {
    return mustChangePassword;
  }
}
