package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.product.ProductRequest;
import com.ontheworld.pos.dto.product.ProductResponse;
import com.ontheworld.pos.entity.Category;
import com.ontheworld.pos.entity.Product;
import com.ontheworld.pos.exception.BadRequestException;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.mapper.ProductMapper;
import com.ontheworld.pos.repository.CategoryRepository;
import com.ontheworld.pos.repository.ProductRepository;
import com.ontheworld.pos.service.AuditService;
import com.ontheworld.pos.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final AuditService auditService;

    public ProductServiceImpl(ProductRepository productRepository,
                               CategoryRepository categoryRepository,
                               ProductMapper productMapper,
                               AuditService auditService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new BadRequestException("Product SKU already exists");
        }
        if (productRepository.findByBarcode(request.getBarcode()).isPresent()) {
            throw new BadRequestException("Product barcode already exists");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.getCategoryId()));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product = productRepository.save(product);
        auditService.log("Product", product.getId().toString(), "CREATE",
                "Created product " + product.getSku() + " - " + product.getNameTh(), currentUsername());
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.getCategoryId()));

        productMapper.updateEntity(request, product);
        product.setCategory(category);
        product = productRepository.save(product);
        auditService.log("Product", id.toString(), "UPDATE",
                "Updated product " + product.getSku() + " - " + product.getNameTh(), currentUsername());
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id, String performedBy) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        product.softDelete(performedBy);
        productRepository.save(product);
        auditService.log("Product", id.toString(), "DELETE",
                "Soft deleted product " + product.getSku() + " - " + product.getNameTh(), performedBy);
    }

    @Override
    public PageResponse<ProductResponse> listProducts(String query, UUID categoryId, Pageable pageable) {
        Page<Product> page;
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));
            page = productRepository.findByCategory(category, pageable);
        } else if (query != null && !query.isBlank()) {
            page = productRepository.searchByQuery(query, pageable);
        } else {
            page = productRepository.findAll(pageable);
        }
        return new PageResponse<>(
                page.stream().map(productMapper::toResponse).collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    @Override
    public ProductResponse getProduct(UUID id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    @Override
    public ProductResponse getProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with barcode: " + barcode));
    }

    @Override
    public ProductResponse getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with SKU: " + sku));
    }

    private String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }
}
