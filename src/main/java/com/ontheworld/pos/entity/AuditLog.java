package com.ontheworld.pos.entity;

import com.ontheworld.pos.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    @Column(nullable = false)
    private String entityName;

    @Column(nullable = false)
    private String entityId;

    @Column(nullable = false, length = 1000)
    private String action;

    @Column(nullable = false, length = 4000)
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    /** Branch of the user who performed the action (null for system/ADMIN with no branch) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    public AuditLog(String entityName, String entityId, String action,
                    String detail, UserAccount user) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.action = action;
        this.detail = detail;
        this.user = user;
        this.branch = (user != null) ? user.getBranch() : null;
    }
}
