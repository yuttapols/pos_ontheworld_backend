package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.customer.CustomerRequest;
import com.ontheworld.pos.dto.customer.CustomerResponse;
import com.ontheworld.pos.dto.customer.LoyaltyRequest;
import com.ontheworld.pos.dto.customer.LoyaltyTransactionResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    CustomerResponse getCustomer(UUID id);
    CustomerResponse updateCustomer(UUID id, CustomerRequest request);
    PageResponse<CustomerResponse> listCustomers(String query, Pageable pageable);
    void deleteCustomer(UUID id, String performedBy);
    CustomerResponse addLoyaltyPoints(UUID id, LoyaltyRequest request);
    CustomerResponse redeemLoyaltyPoints(UUID id, LoyaltyRequest request);
    List<LoyaltyTransactionResponse> getLoyaltyHistory(UUID id);
}
