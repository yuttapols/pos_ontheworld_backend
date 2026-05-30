package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.product.ProductRequest;
import com.ontheworld.pos.dto.product.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request, String callerUsername);
    ProductResponse updateProduct(UUID id, ProductRequest request);
    void deleteProduct(UUID id, String performedBy);
    PageResponse<ProductResponse> listProducts(String query, UUID categoryId, String callerUsername, Pageable pageable);
    ProductResponse getProduct(UUID id);
    ProductResponse getProductByBarcode(String barcode);
    ProductResponse getProductBySku(String sku);
    ProductResponse uploadImage(UUID id, MultipartFile file);

    /** Returns raw PNG bytes of the product's barcode. width/height in pixels. */
    byte[] getBarcodeImage(UUID id, int width, int height);
}
