package com.ontheworld.pos.dto.sale;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SaleResponse {

    private UUID id;
    private String receiptNumber;
    private UUID branchId;
    private String branchNameTh;
    private String branchNameEn;
    private UUID cashierId;
    private String cashierName;
    private UUID customerId;
    private BigDecimal subTotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private LocalDateTime createdAt;
}
