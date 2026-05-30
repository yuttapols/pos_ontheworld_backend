package com.ontheworld.pos.dto.branch;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BranchStatsResponse {
    private UUID branchId;
    private String branchNameTh;
    private String branchNameEn;
    private long employeeCount;       // MANAGER + CASHIER in branch
    private long todaySaleCount;
    private BigDecimal todaySaleTotal;
    private long outOfStockCount;     // products with quantity = 0 at this branch
}
