package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.customer.CustomerRequest;
import com.ontheworld.pos.dto.customer.CustomerResponse;
import com.ontheworld.pos.dto.customer.LoyaltyRequest;
import com.ontheworld.pos.dto.customer.LoyaltyTransactionResponse;
import com.ontheworld.pos.entity.Customer;
import com.ontheworld.pos.entity.LoyaltyTransaction;
import com.ontheworld.pos.entity.LoyaltyTransactionType;
import com.ontheworld.pos.exception.BadRequestException;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.mapper.CustomerMapper;
import com.ontheworld.pos.repository.CustomerRepository;
import com.ontheworld.pos.repository.LoyaltyTransactionRepository;
import com.ontheworld.pos.service.AuditService;
import com.ontheworld.pos.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;
    private final AuditService auditService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               CustomerMapper customerMapper,
                               LoyaltyTransactionRepository loyaltyTransactionRepository,
                               AuditService auditService) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.loyaltyTransactionRepository = loyaltyTransactionRepository;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = customerRepository.save(customerMapper.toEntity(request));
        auditService.log("Customer", customer.getId().toString(), "CREATE",
                "Created customer " + customer.getNameTh(), currentUsername());
        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse getCustomer(UUID id) {
        return customerRepository.findById(id)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(UUID id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
        customerMapper.updateEntity(request, customer);
        customerRepository.save(customer);
        auditService.log("Customer", id.toString(), "UPDATE",
                "Updated customer " + customer.getNameTh(), currentUsername());
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID id, String performedBy) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
        customer.softDelete(performedBy);
        customerRepository.save(customer);
        auditService.log("Customer", id.toString(), "DELETE",
                "Soft deleted customer " + customer.getNameTh(), performedBy);
    }

    @Override
    @Transactional
    public CustomerResponse addLoyaltyPoints(UUID id, LoyaltyRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + request.getPoints());
        customerRepository.save(customer);
        loyaltyTransactionRepository.save(new LoyaltyTransaction(
                customer, LoyaltyTransactionType.MANUAL_ADD,
                request.getPoints(), request.getReason(), null, currentUsername(), null));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse redeemLoyaltyPoints(UUID id, LoyaltyRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
        if (customer.getLoyaltyPoints() < request.getPoints()) {
            throw new BadRequestException("Insufficient loyalty points: available "
                    + customer.getLoyaltyPoints() + ", requested " + request.getPoints());
        }
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() - request.getPoints());
        customerRepository.save(customer);
        loyaltyTransactionRepository.save(new LoyaltyTransaction(
                customer, LoyaltyTransactionType.REDEEM,
                request.getPoints(), request.getReason(), null, currentUsername(), null));
        return customerMapper.toResponse(customer);
    }

    @Override
    public List<LoyaltyTransactionResponse> getLoyaltyHistory(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
        return loyaltyTransactionRepository.findByCustomerOrderByCreatedAtDesc(customer)
                .stream()
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<CustomerResponse> listCustomers(String query, Pageable pageable) {
        Page<Customer> page;
        if (query != null && !query.isBlank()) {
            page = customerRepository.searchByQuery(query, pageable);
        } else {
            page = customerRepository.findAll(pageable);
        }
        return new PageResponse<>(
                page.stream().map(customerMapper::toResponse).collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    private LoyaltyTransactionResponse toTransactionResponse(LoyaltyTransaction tx) {
        LoyaltyTransactionResponse response = new LoyaltyTransactionResponse();
        response.setId(tx.getId());
        response.setType(tx.getType().name());
        response.setPoints(tx.getPoints());
        response.setReason(tx.getReason());
        response.setReferenceId(tx.getReferenceId());
        response.setCreatedBy(tx.getCreatedBy());
        response.setCreatedAt(tx.getCreatedAt());
        return response;
    }

    private String currentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return "system";
    }
}
