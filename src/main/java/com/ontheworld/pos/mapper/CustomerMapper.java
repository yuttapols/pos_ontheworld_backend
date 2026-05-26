package com.ontheworld.pos.mapper;

import com.ontheworld.pos.dto.customer.CustomerRequest;
import com.ontheworld.pos.dto.customer.CustomerResponse;
import com.ontheworld.pos.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse toResponse(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "loyaltyPoints", ignore = true)
    @Mapping(target = "sales", ignore = true)
    Customer toEntity(CustomerRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "loyaltyPoints", ignore = true)
    @Mapping(target = "sales", ignore = true)
    void updateEntity(CustomerRequest request, @MappingTarget Customer customer);
}
