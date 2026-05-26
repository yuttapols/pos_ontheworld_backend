package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.product.ProductRequest;
import com.ontheworld.pos.dto.product.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(UUID id, ProductRequest request);
    void deleteProduct(UUID id, String performedBy);
    PageResponse<ProductResponse> listProducts(String query, UUID categoryId, Pageable pageable);
    ProductResponse getProduct(UUID id);
    ProductResponse getProductByBarcode(String barcode);
    ProductResponse getProductBySku(String sku);
}
