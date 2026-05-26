package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.repository.SaleRepository;
import com.ontheworld.pos.service.ReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private final SaleRepository saleRepository;

    public ReportServiceImpl(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Override
    public Map<String, Object> dailySales(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        var sales = saleRepository.findByCreatedAtBetween(start, end, org.springframework.data.domain.Pageable.unpaged());
        BigDecimal total = sales.getContent().stream().map(s -> s.getTotal() == null ? BigDecimal.ZERO : s.getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("salesCount", sales.getTotalElements());
        result.put("salesAmount", total);
        return result;
    }

    @Override
    public Map<String, Object> monthlySales(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atStartOfDay();
        var sales = saleRepository.findByCreatedAtBetween(start, end, org.springframework.data.domain.Pageable.unpaged());
        BigDecimal total = sales.getContent().stream().map(s -> s.getTotal() == null ? BigDecimal.ZERO : s.getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("month", month);
        result.put("salesCount", sales.getTotalElements());
        result.put("salesAmount", total);
        return result;
    }
}
