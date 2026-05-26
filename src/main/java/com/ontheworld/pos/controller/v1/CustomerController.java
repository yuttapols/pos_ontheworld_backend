package com.ontheworld.pos.controller.v1;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.customer.CustomerRequest;
import com.ontheworld.pos.dto.customer.CustomerResponse;
import com.ontheworld.pos.dto.customer.LoyaltyRequest;
import com.ontheworld.pos.dto.customer.LoyaltyTransactionResponse;
import com.ontheworld.pos.service.CustomerService;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Create a new customer")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Update a customer")
    public ResponseEntity<CustomerResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Soft delete a customer")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @AuthenticationPrincipal UserDetails user) {
        customerService.deleteCustomer(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/loyalty/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Manually add loyalty points to a customer")
    public ResponseEntity<CustomerResponse> addLoyalty(@PathVariable UUID id,
                                                       @Valid @RequestBody LoyaltyRequest request) {
        return ResponseEntity.ok(customerService.addLoyaltyPoints(id, request));
    }

    @PostMapping("/{id}/loyalty/redeem")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Redeem loyalty points from a customer")
    public ResponseEntity<CustomerResponse> redeemLoyalty(@PathVariable UUID id,
                                                          @Valid @RequestBody LoyaltyRequest request) {
        return ResponseEntity.ok(customerService.redeemLoyaltyPoints(id, request));
    }

    @GetMapping("/{id}/loyalty/history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CASHIER')")
    @Operation(summary = "Get loyalty point transaction history for a customer")
    public ResponseEntity<List<LoyaltyTransactionResponse>> loyaltyHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getLoyaltyHistory(id));
    }

    @GetMapping
    @Operation(summary = "List customers with pagination and search")
    public ResponseEntity<PageResponse<CustomerResponse>> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(customerService.listCustomers(q, pageable));
    }
}
