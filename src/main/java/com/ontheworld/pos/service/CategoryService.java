package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.product.CategoryRequest;
import com.ontheworld.pos.dto.product.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request, String callerUsername);
    CategoryResponse updateCategory(UUID id, CategoryRequest request);
    void deleteCategory(UUID id, String performedBy);
    List<CategoryResponse> listCategories(String callerUsername);
}
