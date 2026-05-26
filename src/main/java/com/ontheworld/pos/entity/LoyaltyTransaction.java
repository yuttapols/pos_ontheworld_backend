package com.ontheworld.pos.entity;

import com.ontheworld.pos.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "loyalty_transactions")
public class LoyaltyTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoyaltyTransactionType type;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false)
    private String reason;

    @Column
    private UUID referenceId;

    @Column(nullable = false)
    private String createdBy;

    public LoyaltyTransaction(Customer customer, LoyaltyTransactionType type,
                               Integer points, String reason, UUID referenceId, String createdBy) {
        this.customer = customer;
        this.type = type;
        this.points = points;
        this.reason = reason;
        this.referenceId = referenceId;
        this.createdBy = createdBy;
    }
}
