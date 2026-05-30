package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.product.CategoryRequest;
import com.ontheworld.pos.dto.product.CategoryResponse;
import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Category;
import com.ontheworld.pos.entity.Role;
import com.ontheworld.pos.entity.UserAccount;
import com.ontheworld.pos.exception.BadRequestException;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.mapper.CategoryMapper;
import com.ontheworld.pos.repository.BranchRepository;
import com.ontheworld.pos.repository.CategoryRepository;
import com.ontheworld.pos.repository.UserAccountRepository;
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
    private final UserAccountRepository userRepository;
    private final BranchRepository branchRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper,
                               AuditService auditService,
                               UserAccountRepository userRepository,
                               BranchRepository branchRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.auditService = auditService;
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, String callerUsername) {
        UserAccount caller = requireUser(callerUsername);
        Branch branch = resolveBranch(caller, request.getBranchId());

        // duplicate name check within the same branch scope
        if (branch != null) {
            if (categoryRepository.existsByNameThAndBranchAndDeletedAtIsNull(request.getNameTh(), branch)) {
                throw new BadRequestException("Category '" + request.getNameTh() + "' already exists in this branch");
            }
        } else {
            if (categoryRepository.existsByNameThAndBranchIsNullAndDeletedAtIsNull(request.getNameTh())) {
                throw new BadRequestException("Global category '" + request.getNameTh() + "' already exists");
            }
        }

        Category category = categoryMapper.toEntity(request);
        category.setBranch(branch);
        category = categoryRepository.save(category);
        auditService.log("Category", category.getId().toString(), "CREATE",
                "Created category " + category.getNameTh(), callerUsername);
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
                "Updated category " + category.getNameTh(), "system");
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
    public List<CategoryResponse> listCategories(String callerUsername) {
        UserAccount caller = requireUser(callerUsername);
        if (caller.getRole() == Role.ADMIN) {
            return categoryMapper.toResponseList(categoryRepository.findAll());
        }
        Branch branch = caller.getBranch();
        if (branch == null) {
            return categoryMapper.toResponseList(categoryRepository.findAll());
        }
        return categoryMapper.toResponseList(categoryRepository.findByBranchScope(branch));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private UserAccount requireUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    /** BRANCH_ADMIN is forced to their own branch; ADMIN can pass branchId or null (global). */
    private Branch resolveBranch(UserAccount caller, UUID requestedBranchId) {
        if (caller.getRole() == Role.BRANCH_ADMIN || caller.getRole() == Role.MANAGER) {
            return caller.getBranch();
        }
        if (caller.getRole() == Role.ADMIN && requestedBranchId != null) {
            return branchRepository.findById(requestedBranchId)
                    .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + requestedBranchId));
        }
        return null; // global category
    }
}
