package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.branch.BranchRequest;
import com.ontheworld.pos.dto.branch.BranchResponse;

import java.util.List;
import java.util.UUID;

public interface BranchService {
    BranchResponse createBranch(BranchRequest request);
    BranchResponse getBranch(UUID id);
    BranchResponse updateBranch(UUID id, BranchRequest request);
    void deleteBranch(UUID id, String performedBy);
    List<BranchResponse> findAllBranches();
}
