package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.branch.BranchRequest;
import com.ontheworld.pos.dto.branch.BranchResponse;
import com.ontheworld.pos.dto.branch.BranchStatsResponse;
import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Role;
import com.ontheworld.pos.entity.UserAccount;
import com.ontheworld.pos.exception.BadRequestException;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.mapper.BranchMapper;
import com.ontheworld.pos.repository.BranchRepository;
import com.ontheworld.pos.repository.ProductStockRepository;
import com.ontheworld.pos.repository.SaleRepository;
import com.ontheworld.pos.repository.UserAccountRepository;
import com.ontheworld.pos.service.AuditService;
import com.ontheworld.pos.service.BranchService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;
    private final AuditService auditService;
    private final UserAccountRepository userRepository;
    private final SaleRepository saleRepository;
    private final ProductStockRepository stockRepository;

    public BranchServiceImpl(BranchRepository branchRepository,
                             BranchMapper branchMapper,
                             AuditService auditService,
                             UserAccountRepository userRepository,
                             SaleRepository saleRepository,
                             ProductStockRepository stockRepository) {
        this.branchRepository = branchRepository;
        this.branchMapper = branchMapper;
        this.auditService = auditService;
        this.userRepository = userRepository;
        this.saleRepository = saleRepository;
        this.stockRepository = stockRepository;
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

    @Override
    public BranchStatsResponse getBranchStats(String callerUsername) {
        UserAccount caller = userRepository.findByUsername(callerUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + callerUsername));
        if (caller.getBranch() == null) {
            throw new BadRequestException("User has no branch assigned");
        }
        return buildStats(caller.getBranch());
    }

    @Override
    public BranchStatsResponse getBranchStatsByIdForAdmin(UUID branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + branchId));
        return buildStats(branch);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private BranchStatsResponse buildStats(Branch branch) {
        LocalDateTime dayStart = LocalDate.now().atStartOfDay();
        LocalDateTime dayEnd = LocalDate.now().atTime(LocalTime.MAX);

        long employeeCount = userRepository.countByBranchAndRoleInAndDeletedAtIsNull(
                branch, Set.of(Role.MANAGER, Role.CASHIER));
        long todaySaleCount = saleRepository.countByBranchAndCreatedAtBetween(branch, dayStart, dayEnd);
        BigDecimal todaySaleTotal = saleRepository.sumTotalByBranchAndCreatedAtBetween(branch, dayStart, dayEnd);
        long outOfStockCount = stockRepository.countByBranchAndQuantityEquals(branch, 0);

        return new BranchStatsResponse(
                branch.getId(),
                branch.getNameTh(),
                branch.getNameEn(),
                employeeCount,
                todaySaleCount,
                todaySaleTotal,
                outOfStockCount
        );
    }

    private String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }
}
