package com.ontheworld.pos.service;

import com.ontheworld.pos.entity.AuditLog;

public interface AuditService {
    AuditLog log(String entityName, String entityId, String action, String detail, String username);
}
