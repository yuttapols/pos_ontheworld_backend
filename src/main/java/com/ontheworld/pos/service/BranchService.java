package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.branch.BranchRequest;
import com.ontheworld.pos.dto.branch.BranchResponse;
import com.ontheworld.pos.dto.branch.BranchStatsResponse;

import java.util.List;
import java.util.UUID;

public interface BranchService {
    BranchResponse createBranch(BranchRequest request);
    BranchResponse getBranch(UUID id);
    BranchResponse updateBranch(UUID id, BranchRequest request);
    void deleteBranch(UUID id, String performedBy);
    List<BranchResponse> findAllBranches();

    /** Dashboard stats for the caller's branch (BRANCH_ADMIN) */
    BranchStatsResponse getBranchStats(String callerUsername);

    /** Dashboard stats for any branch by ID (ADMIN only) */
    BranchStatsResponse getBranchStatsByIdForAdmin(UUID branchId);
}
