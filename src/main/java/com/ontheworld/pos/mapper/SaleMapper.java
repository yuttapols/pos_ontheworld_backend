package com.ontheworld.pos.mapper;

import com.ontheworld.pos.dto.sale.SaleResponse;
import com.ontheworld.pos.entity.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    @Mapping(source = "branch.id",        target = "branchId")
    @Mapping(source = "branch.nameTh",    target = "branchNameTh")
    @Mapping(source = "branch.nameEn",    target = "branchNameEn")
    @Mapping(source = "cashier.id",       target = "cashierId")
    @Mapping(source = "cashier.username", target = "cashierName")
    @Mapping(source = "customer.id",      target = "customerId")
    SaleResponse toResponse(Sale sale);
}
