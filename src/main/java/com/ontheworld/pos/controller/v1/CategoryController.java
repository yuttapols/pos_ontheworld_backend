package com.ontheworld.pos.controller.v1;

import com.ontheworld.pos.dto.product.CategoryRequest;
import com.ontheworld.pos.dto.product.CategoryResponse;
import com.ontheworld.pos.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches/{branchId}/categories")
@Tag(name = "Categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Create a new category for a branch")
    public ResponseEntity<CategoryResponse> create(@PathVariable UUID branchId,
                                                    @Valid @RequestBody CategoryRequest request,
                                                    @AuthenticationPrincipal UserDetails caller) {
        return ResponseEntity.ok(categoryService.createCategory(branchId, request, caller.getUsername()));
    }

    @GetMapping
    @Operation(summary = "List all categories for a branch")
    public ResponseEntity<List<CategoryResponse>> list(@PathVariable UUID branchId) {
        return ResponseEntity.ok(categoryService.listCategories(branchId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryResponse> get(@PathVariable UUID branchId,
                                                 @PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategory(branchId, id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update a category")
    public ResponseEntity<CategoryResponse> update(@PathVariable UUID branchId,
                                                    @PathVariable UUID id,
                                                    @Valid @RequestBody CategoryRequest request,
                                                    @AuthenticationPrincipal UserDetails caller) {
        return ResponseEntity.ok(categoryService.updateCategory(branchId, id, request, caller.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Soft delete a category")
    public ResponseEntity<Void> delete(@PathVariable UUID branchId,
                                       @PathVariable UUID id,
                                       @AuthenticationPrincipal UserDetails caller) {
        categoryService.deleteCategory(branchId, id, caller.getUsername());
        return ResponseEntity.noContent().build();
    }
}
