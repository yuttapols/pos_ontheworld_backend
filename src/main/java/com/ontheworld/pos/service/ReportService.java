package com.ontheworld.pos.service;

import java.time.LocalDate;
import java.util.Map;

public interface ReportService {
    Map<String, Object> dailySales(LocalDate date);
    Map<String, Object> monthlySales(int year, int month);
}
