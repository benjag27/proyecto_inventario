package org.phora.domain.repository;

import org.phora.domain.model.AuditLog;
import java.util.List;

public interface AuditLogRepository {
    void save(AuditLog log);
    List<AuditLog> findAll();
}