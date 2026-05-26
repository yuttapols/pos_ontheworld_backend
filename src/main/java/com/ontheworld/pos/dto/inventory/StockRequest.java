package com.ontheworld.pos.dto.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class StockRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private UUID branchId;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotBlank
    private String reason;
}
