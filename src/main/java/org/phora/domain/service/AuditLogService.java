package org.phora.domain.service;

import org.phora.domain.model.AuditLog;
import org.phora.domain.repository.AuditLogRepository;
import java.util.List;

public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Registra un nuevo movimiento de forma rápida.
     */
    public void logAction(String username, String actionType, String description) {
        AuditLog log = new AuditLog(username, actionType, description);
        auditLogRepository.save(log);
    }

    /**
     * Obtiene todo el historial de movimientos para mostrarlo en la tabla.
     */
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}