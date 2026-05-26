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
@Table(name = "product_stocks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "branch_id"})
})
public class ProductStock extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(nullable = false)
    private Integer quantity = 0;

    public ProductStock(Product product, Branch branch, Integer quantity) {
        this.product = product;
        this.branch = branch;
        this.quantity = quantity;
    }
}
