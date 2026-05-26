package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.common.PageResponse;
import com.ontheworld.pos.dto.sale.SaleRequest;
import com.ontheworld.pos.dto.sale.SaleResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SaleService {
    UUID createSale(SaleRequest request);
    SaleResponse getSale(UUID id);
    PageResponse<SaleResponse> listSales(Pageable pageable);
}
