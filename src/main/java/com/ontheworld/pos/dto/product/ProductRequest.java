package com.ontheworld.pos.dto.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductRequest {

    @NotBlank(message = "SKU is required")
    @Size(min = 2, max = 50, message = "SKU must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9\\-_]+$", message = "SKU must contain only letters, numbers, hyphens, or underscores")
    private String sku;

    // optional — if not provided the system auto-generates an ITW-xxxxxxxxx barcode
    @Size(min = 4, max = 30, message = "Barcode must be between 4 and 30 characters")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "Barcode must contain only letters, numbers, or hyphens")
    private String barcode;

    @NotBlank(message = "Thai name is required")
    @Size(min = 1, max = 255, message = "Thai name must not exceed 255 characters")
    private String nameTh;

    @NotBlank(message = "English name is required")
    @Size(min = 1, max = 255, message = "English name must not exceed 255 characters")
    private String nameEn;

    @NotNull(message = "Category is required")
    private UUID categoryId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.00", message = "Cost cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Cost must have at most 2 decimal places")
    private BigDecimal cost;

    @Min(value = 0, message = "Reorder threshold cannot be negative")
    @Max(value = 99999, message = "Reorder threshold is too large")
    private Integer reorderThreshold = 10;

    // imageUrl is intentionally excluded — set only via POST /{id}/image (Cloudinary upload)

    /** optional — ADMIN can specify; BRANCH_ADMIN auto-gets their branch; null = global */
    private UUID branchId;
}
