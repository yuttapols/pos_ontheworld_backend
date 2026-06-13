package com.ontheworld.pos.controller.v1;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.sale.SaleRequest;
import com.ontheworld.pos.dto.sale.SaleResponse;
import com.ontheworld.pos.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches/{branchId}/sales")
@Tag(name = "Sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Create a new sale for a branch")
    public ResponseEntity<UUID> createSale(@PathVariable UUID branchId,
                                           @Valid @RequestBody SaleRequest request) {
        return ResponseEntity.ok(saleService.createSale(branchId, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "List sales for a branch (paginated, newest first)")
    public ResponseEntity<PageResponse<SaleResponse>> list(
            @PathVariable UUID branchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(saleService.listSales(branchId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Get sale by ID")
    public ResponseEntity<SaleResponse> get(@PathVariable UUID branchId,
                                             @PathVariable UUID id) {
        return ResponseEntity.ok(saleService.getSale(branchId, id));
    }
}
