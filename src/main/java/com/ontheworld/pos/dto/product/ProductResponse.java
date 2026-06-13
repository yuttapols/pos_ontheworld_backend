package com.ontheworld.pos.dto.product;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProductResponse implements Serializable {

    private UUID id;
    private String sku;
    private String barcode;
    private String nameTh;
    private String nameEn;
    private UUID categoryId;
    private String categoryNameTh;
    private String categoryNameEn;
    private UUID branchId;
    private String branchNameTh;
    private String branchNameEn;
    private BigDecimal price;
    private BigDecimal cost;
    private Integer reorderThreshold;
    private String imageUrl;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
