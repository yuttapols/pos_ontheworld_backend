package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.entity.AuditLog;
import com.ontheworld.pos.entity.UserAccount;
import com.ontheworld.pos.repository.AuditLogRepository;
import com.ontheworld.pos.repository.UserAccountRepository;
import com.ontheworld.pos.service.AuditService;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserAccountRepository userAccountRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository, UserAccountRepository userAccountRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public AuditLog log(String entityName, String entityId, String action, String detail, String username) {
        UserAccount user = userAccountRepository.findByUsername(username).orElse(null);
        AuditLog auditLog = new AuditLog(entityName, entityId, action, detail, user);
        return auditLogRepository.save(auditLog);
    }
}
