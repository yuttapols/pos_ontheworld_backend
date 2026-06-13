package com.ontheworld.pos.dto.sale;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class SaleRequest {

    private UUID customerId;

    @NotEmpty
    private List<SaleItemRequest> items;

    @DecimalMin("0.0")
    private BigDecimal discount = BigDecimal.ZERO;

    @DecimalMin("0.0")
    private BigDecimal tax = BigDecimal.ZERO;

    @NotNull
    private String paymentMethod;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal paymentAmount;
}
