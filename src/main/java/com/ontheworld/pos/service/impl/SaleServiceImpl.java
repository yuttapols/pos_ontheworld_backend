package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.sale.SaleItemRequest;
import com.ontheworld.pos.dto.sale.SaleRequest;
import com.ontheworld.pos.dto.sale.SaleResponse;
import com.ontheworld.pos.entity.*;
import com.ontheworld.pos.mapper.SaleMapper;
import com.ontheworld.pos.exception.BadRequestException;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.repository.*;
import com.ontheworld.pos.service.AuditService;
import com.ontheworld.pos.service.SaleService;
import com.ontheworld.pos.entity.LoyaltyTransaction;
import com.ontheworld.pos.entity.LoyaltyTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final CustomerRepository customerRepository;
    private final ProductStockRepository productStockRepository;
    private final PaymentRepository paymentRepository;
    private final AuditService auditService;
    private final UserAccountRepository userAccountRepository;
    private final SaleMapper saleMapper;
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;

    public SaleServiceImpl(SaleRepository saleRepository,
                           ProductRepository productRepository,
                           BranchRepository branchRepository,
                           CustomerRepository customerRepository,
                           ProductStockRepository productStockRepository,
                           PaymentRepository paymentRepository,
                           AuditService auditService,
                           UserAccountRepository userAccountRepository,
                           SaleMapper saleMapper,
                           LoyaltyTransactionRepository loyaltyTransactionRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
        this.customerRepository = customerRepository;
        this.productStockRepository = productStockRepository;
        this.paymentRepository = paymentRepository;
        this.auditService = auditService;
        this.userAccountRepository = userAccountRepository;
        this.saleMapper = saleMapper;
        this.loyaltyTransactionRepository = loyaltyTransactionRepository;
    }

    @Override
    @Transactional
    public UUID createSale(SaleRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + request.getBranchId()));
        UserAccount cashier = currentUser();
        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + request.getCustomerId()));
        }

        String receiptNumber = generateReceiptNumber();
        Sale sale = new Sale(receiptNumber, branch, cashier, customer);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + itemRequest.getProductId()));
            ProductStock stock = productStockRepository.findByProductAndBranch(product, branch)
                    .orElseThrow(() -> new BadRequestException("Stock not available for product: " + product.getSku()));
            if (stock.getQuantity() < itemRequest.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getSku());
            }
            stock.setQuantity(stock.getQuantity() - itemRequest.getQuantity());
            productStockRepository.save(stock);

            BigDecimal lineTotal = itemRequest.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
                    .subtract(itemRequest.getDiscount());
            sale.getItems().add(new SaleItem(sale, product, itemRequest.getQuantity(),
                    itemRequest.getPrice(), itemRequest.getDiscount(), lineTotal));
            subtotal = subtotal.add(lineTotal);
        }

        sale.setSubTotal(subtotal);
        sale.setDiscount(request.getDiscount());
        sale.setTax(request.getTax());
        sale.setTotal(subtotal.subtract(request.getDiscount()).add(request.getTax()));
        sale = saleRepository.save(sale);

        paymentRepository.save(new Payment(sale, request.getPaymentMethod(), request.getPaymentAmount()));

        if (customer != null) {
            int pointsEarned = sale.getTotal().intValue() / 10;
            if (pointsEarned > 0) {
                customer.setLoyaltyPoints(customer.getLoyaltyPoints() + pointsEarned);
                customerRepository.save(customer);
                loyaltyTransactionRepository.save(new LoyaltyTransaction(
                        customer, LoyaltyTransactionType.EARN, pointsEarned,
                        "Earned from sale " + receiptNumber, sale.getId(), cashier.getUsername(), branch));
            }
        }

        auditService.log("Sale", sale.getId().toString(), "CREATE",
                "Created sale " + receiptNumber, cashier.getUsername());

        return sale.getId();
    }

    @Override
    public SaleResponse getSale(UUID id) {
        return saleRepository.findById(id)
                .map(saleMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Sale not found: " + id));
    }

    @Override
    public PageResponse<SaleResponse> listSales(String callerUsername, Pageable pageable) {
        UserAccount caller = userAccountRepository.findByUsername(callerUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + callerUsername));
        Page<Sale> page = switch (caller.getRole()) {
            case ADMIN -> saleRepository.findAll(pageable);
            default -> saleRepository.findByBranch(caller.getBranch(), pageable);
        };
        return new PageResponse<>(
                page.stream().map(saleMapper::toResponse).collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    private String generateReceiptNumber() {
        return "POS-" + LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private UserAccount currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userAccountRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("Cashier user not found"));
        }
        throw new BadRequestException("Unable to resolve current user");
    }
}
