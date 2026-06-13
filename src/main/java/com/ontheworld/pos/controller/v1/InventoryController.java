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
@RequestMapping("/api/v1/branches/{branchId}/inventory")
@Tag(name = "Inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "List stock levels for a branch (optionally filter by productId)")
    public ResponseEntity<List<StockResponse>> list(@PathVariable UUID branchId,
                                                     @RequestParam(required = false) UUID productId) {
        return ResponseEntity.ok(inventoryService.listStock(branchId, productId));
    }

    @PostMapping("/increase")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Increase stock for a product at this branch")
    public ResponseEntity<Void> increase(@PathVariable UUID branchId,
                                         @Valid @RequestBody StockRequest request) {
        inventoryService.increaseStock(branchId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/decrease")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Decrease stock for a product at this branch")
    public ResponseEntity<Void> decrease(@PathVariable UUID branchId,
                                          @Valid @RequestBody StockRequest request) {
        inventoryService.decreaseStock(branchId, request);
        return ResponseEntity.noContent().build();
    }
}
