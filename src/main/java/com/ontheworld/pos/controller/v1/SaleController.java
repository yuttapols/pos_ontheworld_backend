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
@RequestMapping("/api/v1/sales")
@Tag(name = "Sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Create a new sale")
    public ResponseEntity<UUID> createSale(@Valid @RequestBody SaleRequest request) {
        return ResponseEntity.ok(saleService.createSale(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Get sale by ID")
    public ResponseEntity<SaleResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(saleService.getSale(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "List all sales with pagination")
    public ResponseEntity<PageResponse<SaleResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(saleService.listSales(pageable));
    }
}
