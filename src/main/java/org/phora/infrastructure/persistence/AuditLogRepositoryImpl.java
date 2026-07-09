package org.phora.infrastructure.persistence;

import org.phora.domain.model.AuditLog;
import org.phora.domain.repository.AuditLogRepository;
import org.phora.infrastructure.persistence.BsConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditLogRepositoryImpl implements AuditLogRepository {

    @Override
    public void save(AuditLog log) {
        String sql = "INSERT INTO audit_logs (username, action_type, description) VALUES (?, ?, ?)";

        try (Connection conn = BsConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, log.getUsername());
            pstmt.setString(2, log.getActionType());
            pstmt.setString(3, log.getDescription());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el registro de auditoría", e);
        }
    }

    @Override
    public List<AuditLog> findAll() {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT id, username, action_type, description, timestamp FROM audit_logs ORDER BY id DESC";

        try (Connection conn = BsConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                AuditLog log = new AuditLog(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("action_type"),
                        rs.getString("description"),
                        rs.getString("timestamp")
                );
                logs.add(log);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener los registros de auditoría", e);
        }
        return logs;
    }
}