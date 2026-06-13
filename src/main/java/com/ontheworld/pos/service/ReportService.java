package com.ontheworld.pos.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public interface ReportService {
    Map<String, Object> dailySales(UUID branchId, LocalDate date);
    Map<String, Object> monthlySales(UUID branchId, int year, int month);
}
