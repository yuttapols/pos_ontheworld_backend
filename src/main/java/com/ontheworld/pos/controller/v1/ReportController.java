package com.ontheworld.pos.controller.v1;

import com.ontheworld.pos.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches/{branchId}/reports")
@Tag(name = "Reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily/{date}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Daily sales report for a branch")
    public ResponseEntity<Map<String, Object>> dailySales(
            @PathVariable UUID branchId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.dailySales(branchId, date));
    }

    @GetMapping("/monthly/{year}/{month}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BRANCH_ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Monthly sales report for a branch")
    public ResponseEntity<Map<String, Object>> monthlySales(@PathVariable UUID branchId,
                                                             @PathVariable int year,
                                                             @PathVariable int month) {
        return ResponseEntity.ok(reportService.monthlySales(branchId, year, month));
    }
}
