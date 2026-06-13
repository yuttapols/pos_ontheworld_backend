package com.ontheworld.pos.controller.v1;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.product.ProductRequest;
import com.ontheworld.pos.dto.product.ProductResponse;
import com.ontheworld.pos.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches/{branchId}/products")
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Create a new product for a branch")
    public ResponseEntity<ProductResponse> create(@PathVariable UUID branchId,
                                                   @Valid @RequestBody ProductRequest request,
                                                   @AuthenticationPrincipal UserDetails caller) {
        return ResponseEntity.ok(productService.createProduct(branchId, request, caller.getUsername()));
    }

    @GetMapping
    @Operation(summary = "List products for a branch (paginated, filterable by query and categoryId)")
    public ResponseEntity<PageResponse<ProductResponse>> list(
            @PathVariable UUID branchId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.listProducts(branchId, q, categoryId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductResponse> get(@PathVariable UUID branchId,
                                                @PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProduct(branchId, id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update a product")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID branchId,
                                                   @PathVariable UUID id,
                                                   @Valid @RequestBody ProductRequest request,
                                                   @AuthenticationPrincipal UserDetails caller) {
        return ResponseEntity.ok(productService.updateProduct(branchId, id, request, caller.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Soft delete a product")
    public ResponseEntity<Void> delete(@PathVariable UUID branchId,
                                       @PathVariable UUID id,
                                       @AuthenticationPrincipal UserDetails caller) {
        productService.deleteProduct(branchId, id, caller.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/barcode/{barcode}")
    @Operation(summary = "Get product by barcode within a branch")
    public ResponseEntity<ProductResponse> getByBarcode(@PathVariable UUID branchId,
                                                         @PathVariable String barcode) {
        return ResponseEntity.ok(productService.getProductByBarcode(branchId, barcode));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU within a branch")
    public ResponseEntity<ProductResponse> getBySku(@PathVariable UUID branchId,
                                                     @PathVariable String sku) {
        return ResponseEntity.ok(productService.getProductBySku(branchId, sku));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Upload product image to Cloudinary (JPEG/PNG/WebP, max 5 MB)")
    public ResponseEntity<ProductResponse> uploadImage(@PathVariable UUID branchId,
                                                       @PathVariable UUID id,
                                                       @RequestParam MultipartFile file) {
        return ResponseEntity.ok(productService.uploadImage(branchId, id, file));
    }

    @GetMapping(value = "/{id}/barcode-image", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Get product barcode as PNG image (width/height in pixels, default 300x100)")
    public ResponseEntity<byte[]> barcodeImage(@PathVariable UUID branchId,
                                               @PathVariable UUID id,
                                               @RequestParam(defaultValue = "300") int width,
                                               @RequestParam(defaultValue = "100") int height) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(productService.getBarcodeImage(branchId, id, width, height));
    }
}
