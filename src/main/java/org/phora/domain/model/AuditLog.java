package org.phora.domain.model;

public class AuditLog {
    private int id;
    private String username;
    private String actionType;
    private String description;
    private String timestamp; // Usamos String por simplicidad con SQLite o LocalDateTime

    // Constructor completo
    public AuditLog(int id, String username, String actionType, String description, String timestamp) {
        this.id = id;
        this.username = username;
        this.actionType = actionType;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Constructor para insertar nuevos movimientos (sin ID ni fecha, los maneja la BD)
    public AuditLog(String username, String actionType, String description) {
        this.username = username;
        this.actionType = actionType;
        this.description = description;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getActionType() { return actionType; }
    public String getDescription() { return description; }
    public String getTimestamp() { return timestamp; }
}