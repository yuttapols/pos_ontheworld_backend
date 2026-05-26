package com.ontheworld.pos.controller.v1;

import com.ontheworld.pos.dto.branch.BranchRequest;
import com.ontheworld.pos.dto.branch.BranchResponse;
import com.ontheworld.pos.service.BranchService;
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
@RequestMapping("/api/v1/branches")
@Tag(name = "Branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new branch")
    public ResponseEntity<BranchResponse> create(@Valid @RequestBody BranchRequest request) {
        return ResponseEntity.ok(branchService.createBranch(request));
    }

    @GetMapping
    @Operation(summary = "List all branches")
    public ResponseEntity<List<BranchResponse>> list() {
        return ResponseEntity.ok(branchService.findAllBranches());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get branch by ID")
    public ResponseEntity<BranchResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(branchService.getBranch(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a branch")
    public ResponseEntity<BranchResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody BranchRequest request) {
        return ResponseEntity.ok(branchService.updateBranch(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a branch")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @AuthenticationPrincipal UserDetails user) {
        branchService.deleteBranch(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
