package com.ontheworld.pos.controller.v1;

import com.ontheworld.pos.dto.inventory.StockRequest;
import com.ontheworld.pos.dto.inventory.StockResponse;
import com.ontheworld.pos.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/increase")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Increase stock for a product at a branch")
    public ResponseEntity<Void> increase(@Valid @RequestBody StockRequest request) {
        inventoryService.increaseStock(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/decrease")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Decrease stock for a product at a branch")
    public ResponseEntity<Void> decrease(@Valid @RequestBody StockRequest request) {
        inventoryService.decreaseStock(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "List stock levels — filter by branchId and/or productId")
    public ResponseEntity<List<StockResponse>> stock(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) UUID productId) {
        return ResponseEntity.ok(inventoryService.listStock(branchId, productId));
    }
}
