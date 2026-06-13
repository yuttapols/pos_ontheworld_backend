package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.product.ProductRequest;
import com.ontheworld.pos.dto.product.ProductResponse;
import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Category;
import com.ontheworld.pos.entity.Product;
import com.ontheworld.pos.exception.BadRequestException;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.mapper.ProductMapper;
import com.ontheworld.pos.repository.BranchRepository;
import com.ontheworld.pos.repository.CategoryRepository;
import com.ontheworld.pos.repository.ProductRepository;
import com.ontheworld.pos.service.AuditService;
import com.ontheworld.pos.service.BarcodeService;
import com.ontheworld.pos.service.CloudinaryService;
import com.ontheworld.pos.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BranchRepository branchRepository;
    private final ProductMapper productMapper;
    private final AuditService auditService;
    private final CloudinaryService cloudinaryService;
    private final BarcodeService barcodeService;

    public ProductServiceImpl(ProductRepository productRepository,
                               CategoryRepository categoryRepository,
                               BranchRepository branchRepository,
                               ProductMapper productMapper,
                               AuditService auditService,
                               CloudinaryService cloudinaryService,
                               BarcodeService barcodeService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.branchRepository = branchRepository;
        this.productMapper = productMapper;
        this.auditService = auditService;
        this.cloudinaryService = cloudinaryService;
        this.barcodeService = barcodeService;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(UUID branchId, ProductRequest request, String callerUsername) {
        Branch branch = requireBranch(branchId);

        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new BadRequestException("SKU '" + request.getSku() + "' already exists");
        }

        String barcode = (request.getBarcode() != null && !request.getBarcode().isBlank())
                ? request.getBarcode()
                : generateUniqueBarcode();

        if (request.getBarcode() != null && !request.getBarcode().isBlank()
                && productRepository.findByBarcode(barcode).isPresent()) {
            throw new BadRequestException("Barcode '" + barcode + "' already exists");
        }

        if (request.getCost().compareTo(request.getPrice()) > 0) {
            throw new BadRequestException("Cost cannot be greater than price");
        }

        Category category = requireCategory(request.getCategoryId());

        if (productRepository.existsByNameThAndCategoryAndDeletedAtIsNull(request.getNameTh(), category)) {
            throw new BadRequestException(
                    "Product '" + request.getNameTh() + "' already exists in category '" + category.getNameTh() + "'");
        }

        Product product = productMapper.toEntity(request);
        product.setBarcode(barcode);
        product.setCategory(category);
        product.setBranch(branch);
        if (request.getIsActive() != null) {
            product.setActive(request.getIsActive());
        }
        product = productRepository.save(product);
        auditService.log("Product", product.getId().toString(), "CREATE",
                "Created product " + product.getSku() + " - " + product.getNameTh(), callerUsername);
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse getProduct(UUID branchId, UUID id) {
        Branch branch = requireBranch(branchId);
        Product product = requireProduct(id);
        validateBelongsToBranch(product, branch);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID branchId, UUID id, ProductRequest request, String callerUsername) {
        Branch branch = requireBranch(branchId);
        Product product = requireProduct(id);
        validateBelongsToBranch(product, branch);

        if (!product.getSku().equals(request.getSku())
                && productRepository.findBySku(request.getSku()).isPresent()) {
            throw new BadRequestException("SKU '" + request.getSku() + "' already exists");
        }
        if (request.getBarcode() != null && !request.getBarcode().isBlank()
                && !product.getBarcode().equals(request.getBarcode())
                && productRepository.findByBarcode(request.getBarcode()).isPresent()) {
            throw new BadRequestException("Barcode '" + request.getBarcode() + "' already exists");
        }
        if (request.getCost().compareTo(request.getPrice()) > 0) {
            throw new BadRequestException("Cost cannot be greater than price");
        }

        Category category = requireCategory(request.getCategoryId());

        if (productRepository.existsByNameThAndCategoryAndIdNotAndDeletedAtIsNull(request.getNameTh(), category, id)) {
            throw new BadRequestException(
                    "Product '" + request.getNameTh() + "' already exists in category '" + category.getNameTh() + "'");
        }

        String existingImageUrl = product.getImageUrl();
        productMapper.updateEntity(request, product);
        product.setCategory(category);
        product.setImageUrl(existingImageUrl);
        if (request.getIsActive() != null) {
            product.setActive(request.getIsActive());
        }
        product = productRepository.save(product);
        auditService.log("Product", id.toString(), "UPDATE",
                "Updated product " + product.getSku() + " - " + product.getNameTh(), callerUsername);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID branchId, UUID id, String performedBy) {
        Branch branch = requireBranch(branchId);
        Product product = requireProduct(id);
        validateBelongsToBranch(product, branch);
        product.softDelete(performedBy);
        productRepository.save(product);
        auditService.log("Product", id.toString(), "DELETE",
                "Soft deleted product " + product.getSku() + " - " + product.getNameTh(), performedBy);
    }

    @Override
    public PageResponse<ProductResponse> listProducts(UUID branchId, String query, UUID categoryId, Pageable pageable) {
        Branch branch = requireBranch(branchId);
        Page<Product> page;

        boolean hasQuery = query != null && !query.isBlank();
        boolean hasCategory = categoryId != null;

        if (hasCategory) {
            Category category = requireCategory(categoryId);
            if (hasQuery) {
                page = productRepository.searchByBranchAndCategory(branch, category, query, pageable);
            } else {
                page = productRepository.findByBranchAndCategoryAndDeletedAtIsNull(branch, category, pageable);
            }
        } else if (hasQuery) {
            page = productRepository.searchByBranch(branch, query, pageable);
        } else {
            page = productRepository.findByBranchAndDeletedAtIsNull(branch, pageable);
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
    public ProductResponse getProductByBarcode(UUID branchId, String barcode) {
        Branch branch = requireBranch(branchId);
        return productRepository.findByBarcodeAndBranch(barcode, branch)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with barcode: " + barcode));
    }

    @Override
    public ProductResponse getProductBySku(UUID branchId, String sku) {
        Branch branch = requireBranch(branchId);
        return productRepository.findBySkuAndBranch(sku, branch)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with SKU: " + sku));
    }

    @Override
    @Transactional
    public ProductResponse uploadImage(UUID branchId, UUID id, MultipartFile file) {
        Branch branch = requireBranch(branchId);
        Product product = requireProduct(id);
        validateBelongsToBranch(product, branch);
        String imageUrl = cloudinaryService.uploadProductImage(file);
        product.setImageUrl(imageUrl);
        productRepository.save(product);
        auditService.log("Product", id.toString(), "UPLOAD_IMAGE",
                "Uploaded image for product " + product.getSku(), currentUsername());
        return productMapper.toResponse(product);
    }

    @Override
    public byte[] getBarcodeImage(UUID branchId, UUID id, int width, int height) {
        Branch branch = requireBranch(branchId);
        Product product = requireProduct(id);
        validateBelongsToBranch(product, branch);
        return barcodeService.generatePng(product.getBarcode(), width, height);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Branch requireBranch(UUID branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + branchId));
    }

    private Product requireProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    private Category requireCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }

    private void validateBelongsToBranch(Product product, Branch branch) {
        if (product.getBranch() == null || !product.getBranch().getId().equals(branch.getId())) {
            throw new EntityNotFoundException("Product not found in this branch");
        }
    }

    private String generateUniqueBarcode() {
        String barcode;
        do {
            long suffix = (long) (Math.random() * 1_000_000_000L);
            barcode = String.format("ITW%09d", suffix);
        } while (productRepository.findByBarcode(barcode).isPresent());
        return barcode;
    }

    private String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }
}
