package com.ontheworld.pos.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductRequest {

    @NotBlank
    private String sku;

    @NotBlank
    private String barcode;

    @NotBlank
    private String nameTh;

    @NotBlank
    private String nameEn;

    @NotNull
    private UUID categoryId;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal cost;

    private Integer reorderThreshold = 10;
    private String imageUrl;
}
