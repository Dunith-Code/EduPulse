package com.edupulse.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditService {

    private static final int MAX_DETAILS = 1000;

    private final AuditLogRepository logs;

    public AuditService(AuditLogRepository logs) {
        this.logs = logs;
    }

    /** Joins the caller's transaction, so the audit row commits or rolls back with the action. */
    @Transactional
    public void record(UUID instituteId, UUID actorId, String action, String entityType,
                       UUID entityId, String details) {
        String safe = details != null && details.length() > MAX_DETAILS
                ? details.substring(0, MAX_DETAILS) : details;
        logs.save(new AuditLog(instituteId, actorId, action, entityType, entityId, safe));
    }
}