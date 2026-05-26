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

    public AuditLog(String entityName, String entityId, String action,
                    String detail, UserAccount user) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.action = action;
        this.detail = detail;
        this.user = user;
    }
}
