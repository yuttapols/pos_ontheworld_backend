package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.product.ProductRequest;
import com.ontheworld.pos.dto.product.ProductResponse;
import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Category;
import com.ontheworld.pos.entity.Product;
import com.ontheworld.pos.entity.Role;
import com.ontheworld.pos.entity.UserAccount;
import com.ontheworld.pos.exception.BadRequestException;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.mapper.ProductMapper;
import com.ontheworld.pos.repository.BranchRepository;
import com.ontheworld.pos.repository.CategoryRepository;
import com.ontheworld.pos.repository.ProductRepository;
import com.ontheworld.pos.repository.UserAccountRepository;
import com.ontheworld.pos.service.AuditService;
import com.ontheworld.pos.service.BarcodeService;
import com.ontheworld.pos.service.CloudinaryService;
import com.ontheworld.pos.service.ProductService;
import org.springframework.web.multipart.MultipartFile;
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
    private final CloudinaryService cloudinaryService;
    private final BarcodeService barcodeService;
    private final UserAccountRepository userRepository;
    private final BranchRepository branchRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                               CategoryRepository categoryRepository,
                               ProductMapper productMapper,
                               AuditService auditService,
                               CloudinaryService cloudinaryService,
                               BarcodeService barcodeService,
                               UserAccountRepository userRepository,
                               BranchRepository branchRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.auditService = auditService;
        this.cloudinaryService = cloudinaryService;
        this.barcodeService = barcodeService;
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request, String callerUsername) {
        UserAccount caller = userRepository.findByUsername(callerUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + callerUsername));
        Branch branch = resolveBranch(caller, request.getBranchId());

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

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.getCategoryId()));

        if (productRepository.existsByNameThAndCategoryAndDeletedAtIsNull(request.getNameTh(), category)) {
            throw new BadRequestException(
                    "Product '" + request.getNameTh() + "' already exists in category '" + category.getNameTh() + "'");
        }

        Product product = productMapper.toEntity(request);
        product.setBarcode(barcode);
        product.setCategory(category);
        product.setBranch(branch);
        product = productRepository.save(product);
        auditService.log("Product", product.getId().toString(), "CREATE",
                "Created product " + product.getSku() + " - " + product.getNameTh(), callerUsername);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        if (!product.getSku().equals(request.getSku())
                && productRepository.findBySku(request.getSku()).isPresent()) {
            throw new BadRequestException("SKU '" + request.getSku() + "' already exists");
        }
        if (!product.getBarcode().equals(request.getBarcode())
                && productRepository.findByBarcode(request.getBarcode()).isPresent()) {
            throw new BadRequestException("Barcode '" + request.getBarcode() + "' already exists");
        }
        if (request.getCost().compareTo(request.getPrice()) > 0) {
            throw new BadRequestException("Cost cannot be greater than price");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.getCategoryId()));

        if (productRepository.existsByNameThAndCategoryAndIdNotAndDeletedAtIsNull(request.getNameTh(), category, id)) {
            throw new BadRequestException(
                    "Product '" + request.getNameTh() + "' already exists in category '" + category.getNameTh() + "'");
        }

        String existingImageUrl = product.getImageUrl();
        productMapper.updateEntity(request, product);
        product.setCategory(category);
        product.setImageUrl(existingImageUrl);   // preserve — imageUrl is managed only via POST /{id}/image
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
    public PageResponse<ProductResponse> listProducts(String query, UUID categoryId, String callerUsername, Pageable pageable) {
        UserAccount caller = userRepository.findByUsername(callerUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + callerUsername));
        Branch branch = caller.getBranch();
        boolean isAdmin = caller.getRole() == Role.ADMIN;

        Page<Product> page;
        if (isAdmin) {
            if (categoryId != null) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));
                page = productRepository.findByCategory(category, pageable);
            } else if (query != null && !query.isBlank()) {
                page = productRepository.searchByQuery(query, pageable);
            } else {
                page = productRepository.findAll(pageable);
            }
        } else {
            if (categoryId != null) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));
                page = productRepository.findByCategoryAndBranchScope(category, branch, pageable);
            } else if (query != null && !query.isBlank()) {
                page = productRepository.searchByBranchScope(branch, query, pageable);
            } else {
                page = productRepository.findByBranchScope(branch, pageable);
            }
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

    @Override
    @Transactional
    public ProductResponse uploadImage(UUID id, MultipartFile file) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        String imageUrl = cloudinaryService.uploadProductImage(file);
        product.setImageUrl(imageUrl);
        productRepository.save(product);
        auditService.log("Product", id.toString(), "UPLOAD_IMAGE",
                "Uploaded image for product " + product.getSku(), currentUsername());
        return productMapper.toResponse(product);
    }

    @Override
    public byte[] getBarcodeImage(UUID id, int width, int height) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        return barcodeService.generatePng(product.getBarcode(), width, height);
    }

    private Branch resolveBranch(UserAccount caller, UUID requestedBranchId) {
        if (caller.getRole() == Role.BRANCH_ADMIN || caller.getRole() == Role.MANAGER) {
            return caller.getBranch();
        }
        if (caller.getRole() == Role.ADMIN && requestedBranchId != null) {
            return branchRepository.findById(requestedBranchId)
                    .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + requestedBranchId));
        }
        return null;
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
