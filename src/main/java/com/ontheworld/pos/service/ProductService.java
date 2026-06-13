package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.product.ProductRequest;
import com.ontheworld.pos.dto.product.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(UUID branchId, ProductRequest request, String callerUsername);
    ProductResponse getProduct(UUID branchId, UUID id);
    ProductResponse updateProduct(UUID branchId, UUID id, ProductRequest request, String callerUsername);
    void deleteProduct(UUID branchId, UUID id, String performedBy);
    PageResponse<ProductResponse> listProducts(UUID branchId, String query, UUID categoryId, Pageable pageable);
    ProductResponse getProductByBarcode(UUID branchId, String barcode);
    ProductResponse getProductBySku(UUID branchId, String sku);
    ProductResponse uploadImage(UUID branchId, UUID id, MultipartFile file);
    byte[] getBarcodeImage(UUID branchId, UUID id, int width, int height);
}
