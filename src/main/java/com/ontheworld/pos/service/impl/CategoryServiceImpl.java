package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.product.CategoryRequest;
import com.ontheworld.pos.dto.product.CategoryResponse;
import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Category;
import com.ontheworld.pos.exception.BadRequestException;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.mapper.CategoryMapper;
import com.ontheworld.pos.repository.BranchRepository;
import com.ontheworld.pos.repository.CategoryRepository;
import com.ontheworld.pos.service.AuditService;
import com.ontheworld.pos.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuditService auditService;
    private final BranchRepository branchRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper,
                               AuditService auditService,
                               BranchRepository branchRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.auditService = auditService;
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(UUID branchId, CategoryRequest request, String callerUsername) {
        Branch branch = requireBranch(branchId);

        if (categoryRepository.existsByNameThAndBranchAndDeletedAtIsNull(request.getNameTh(), branch)) {
            throw new BadRequestException("Category '" + request.getNameTh() + "' already exists in this branch");
        }

        Category category = categoryMapper.toEntity(request);
        category.setBranch(branch);
        if (request.getIsActive() != null) {
            category.setActive(request.getIsActive());
        }
        category = categoryRepository.save(category);
        auditService.log("Category", category.getId().toString(), "CREATE",
                "Created category " + category.getNameTh(), callerUsername);
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse getCategory(UUID branchId, UUID id) {
        Branch branch = requireBranch(branchId);
        Category category = requireCategory(id);
        validateBelongsToBranch(category, branch);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID branchId, UUID id, CategoryRequest request, String callerUsername) {
        Branch branch = requireBranch(branchId);
        Category category = requireCategory(id);
        validateBelongsToBranch(category, branch);

        if (categoryRepository.existsByNameThAndBranchAndIdNotAndDeletedAtIsNull(request.getNameTh(), branch, id)) {
            throw new BadRequestException("Category '" + request.getNameTh() + "' already exists in this branch");
        }

        categoryMapper.updateEntity(request, category);
        if (request.getIsActive() != null) {
            category.setActive(request.getIsActive());
        }
        category = categoryRepository.save(category);
        auditService.log("Category", id.toString(), "UPDATE",
                "Updated category " + category.getNameTh(), callerUsername);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID branchId, UUID id, String performedBy) {
        Branch branch = requireBranch(branchId);
        Category category = requireCategory(id);
        validateBelongsToBranch(category, branch);
        category.softDelete(performedBy);
        categoryRepository.save(category);
        auditService.log("Category", id.toString(), "DELETE",
                "Soft deleted category " + category.getNameTh(), performedBy);
    }

    @Override
    public List<CategoryResponse> listCategories(UUID branchId) {
        Branch branch = requireBranch(branchId);
        return categoryMapper.toResponseList(categoryRepository.findAllByBranch(branch));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Branch requireBranch(UUID branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + branchId));
    }

    private Category requireCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }

    private void validateBelongsToBranch(Category category, Branch branch) {
        if (category.getBranch() == null || !category.getBranch().getId().equals(branch.getId())) {
            throw new EntityNotFoundException("Category not found in this branch");
        }
    }
}
