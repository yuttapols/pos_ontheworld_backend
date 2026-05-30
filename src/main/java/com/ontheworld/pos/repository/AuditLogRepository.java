package com.ontheworld.pos.repository;

import com.ontheworld.pos.entity.AuditLog;
import com.ontheworld.pos.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByBranch(Branch branch, Pageable pageable);
    Page<AuditLog> findAll(Pageable pageable);
}
