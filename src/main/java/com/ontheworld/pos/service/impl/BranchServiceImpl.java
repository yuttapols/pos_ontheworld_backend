package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.branch.BranchRequest;
import com.ontheworld.pos.dto.branch.BranchResponse;
import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.mapper.BranchMapper;
import com.ontheworld.pos.repository.BranchRepository;
import com.ontheworld.pos.service.AuditService;
import com.ontheworld.pos.service.BranchService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;
    private final AuditService auditService;

    public BranchServiceImpl(BranchRepository branchRepository,
                             BranchMapper branchMapper,
                             AuditService auditService) {
        this.branchRepository = branchRepository;
        this.branchMapper = branchMapper;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public BranchResponse createBranch(BranchRequest request) {
        Branch branch = branchRepository.save(branchMapper.toEntity(request));
        auditService.log("Branch", branch.getId().toString(), "CREATE",
                "Created branch " + branch.getNameTh(), currentUsername());
        return branchMapper.toResponse(branch);
    }

    @Override
    public BranchResponse getBranch(UUID id) {
        return branchRepository.findById(id)
                .map(branchMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + id));
    }

    @Override
    @Transactional
    public BranchResponse updateBranch(UUID id, BranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + id));
        branchMapper.updateEntity(request, branch);
        branch = branchRepository.save(branch);
        auditService.log("Branch", id.toString(), "UPDATE",
                "Updated branch " + branch.getNameTh(), currentUsername());
        return branchMapper.toResponse(branch);
    }

    @Override
    @Transactional
    public void deleteBranch(UUID id, String performedBy) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + id));
        branch.softDelete(performedBy);
        branchRepository.save(branch);
        auditService.log("Branch", id.toString(), "DELETE",
                "Soft deleted branch " + branch.getNameTh(), performedBy);
    }

    @Override
    public List<BranchResponse> findAllBranches() {
        return branchMapper.toResponseList(branchRepository.findAll());
    }

    private String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }
}
