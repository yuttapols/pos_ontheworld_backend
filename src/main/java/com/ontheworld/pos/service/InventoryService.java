package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.inventory.StockRequest;
import com.ontheworld.pos.dto.inventory.StockResponse;

import java.util.List;
import java.util.UUID;

public interface InventoryService {
    void increaseStock(UUID branchId, StockRequest request);
    void decreaseStock(UUID branchId, StockRequest request);
    List<StockResponse> listStock(UUID branchId, UUID productId);
}
