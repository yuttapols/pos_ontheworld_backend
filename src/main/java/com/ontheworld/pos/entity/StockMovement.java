package com.ontheworld.pos.entity;

import com.ontheworld.pos.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stock_movements")
public class StockMovement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(nullable = false)
    private Integer quantityChange;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime occurredAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount performedBy;

    public StockMovement(Product product, Branch branch, Integer quantityChange,
                         String reason, UserAccount performedBy) {
        this.product = product;
        this.branch = branch;
        this.quantityChange = quantityChange;
        this.reason = reason;
        this.performedBy = performedBy;
    }
}
