package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.product.CategoryRequest;
import com.ontheworld.pos.dto.product.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(UUID branchId, CategoryRequest request, String callerUsername);
    CategoryResponse getCategory(UUID branchId, UUID id);
    CategoryResponse updateCategory(UUID branchId, UUID id, CategoryRequest request, String callerUsername);
    void deleteCategory(UUID branchId, UUID id, String performedBy);
    List<CategoryResponse> listCategories(UUID branchId);
}
