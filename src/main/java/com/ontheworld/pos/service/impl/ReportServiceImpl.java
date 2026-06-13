package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.repository.BranchRepository;
import com.ontheworld.pos.repository.SaleRepository;
import com.ontheworld.pos.service.ReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {

    private final SaleRepository saleRepository;
    private final BranchRepository branchRepository;

    public ReportServiceImpl(SaleRepository saleRepository, BranchRepository branchRepository) {
        this.saleRepository = saleRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public Map<String, Object> dailySales(UUID branchId, LocalDate date) {
        Branch branch = requireBranch(branchId);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        long count = saleRepository.countByBranchAndCreatedAtBetween(branch, start, end);
        BigDecimal total = saleRepository.sumTotalByBranchAndCreatedAtBetween(branch, start, end);

        Map<String, Object> result = new HashMap<>();
        result.put("branchId", branchId);
        result.put("date", date);
        result.put("salesCount", count);
        result.put("salesAmount", total);
        return result;
    }

    @Override
    public Map<String, Object> monthlySales(UUID branchId, int year, int month) {
        Branch branch = requireBranch(branchId);
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(year, month, 1).plusMonths(1).atStartOfDay();

        long count = saleRepository.countByBranchAndCreatedAtBetween(branch, start, end);
        BigDecimal total = saleRepository.sumTotalByBranchAndCreatedAtBetween(branch, start, end);

        Map<String, Object> result = new HashMap<>();
        result.put("branchId", branchId);
        result.put("year", year);
        result.put("month", month);
        result.put("salesCount", count);
        result.put("salesAmount", total);
        return result;
    }

    private Branch requireBranch(UUID branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + branchId));
    }
}
