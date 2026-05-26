package com.ontheworld.pos.dto.inventory;

import lombok.Data;

import java.util.UUID;

@Data
public class StockResponse {

    private UUID productId;
    private String productSku;
    private String productNameTh;
    private String productNameEn;
    private UUID branchId;
    private String branchNameTh;
    private String branchNameEn;
    private Integer quantity;
}
