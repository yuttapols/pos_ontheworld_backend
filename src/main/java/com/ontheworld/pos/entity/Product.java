package com.ontheworld.pos.entity;

import com.ontheworld.pos.entity.base.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product extends SoftDeletableEntity {

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false, unique = true)
    private String barcode;

    @Column(nullable = false)
    private String nameTh;

    @Column(nullable = false)
    private String nameEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal cost;

    @Column(nullable = false)
    private Integer reorderThreshold = 10;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductStock> stocks = new HashSet<>();

    public Product(String sku, String barcode, String nameTh, String nameEn, Category category,
                   BigDecimal price, BigDecimal cost, String imageUrl) {
        this.sku = sku;
        this.barcode = barcode;
        this.nameTh = nameTh;
        this.nameEn = nameEn;
        this.category = category;
        this.price = price;
        this.cost = cost;
        this.imageUrl = imageUrl;
    }
}
