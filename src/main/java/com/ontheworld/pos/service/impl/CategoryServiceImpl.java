package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.product.CategoryRequest;
import com.ontheworld.pos.dto.product.CategoryResponse;
import com.ontheworld.pos.entity.Category;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.mapper.CategoryMapper;
import com.ontheworld.pos.repository.CategoryRepository;
import com.ontheworld.pos.service.AuditService;
import com.ontheworld.pos.service.CategoryService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuditService auditService;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper,
                               AuditService auditService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = categoryRepository.save(categoryMapper.toEntity(request));
        auditService.log("Category", category.getId().toString(), "CREATE",
                "Created category " + category.getNameTh(), currentUsername());
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
        categoryMapper.updateEntity(request, category);
        category = categoryRepository.save(category);
        auditService.log("Category", id.toString(), "UPDATE",
                "Updated category " + category.getNameTh(), currentUsername());
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id, String performedBy) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
        category.softDelete(performedBy);
        categoryRepository.save(category);
        auditService.log("Category", id.toString(), "DELETE",
                "Soft deleted category " + category.getNameTh(), performedBy);
    }

    @Override
    public List<CategoryResponse> listCategories() {
        return categoryMapper.toResponseList(categoryRepository.findAll());
    }

    private String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }
}
