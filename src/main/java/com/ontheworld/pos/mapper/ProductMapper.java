package com.ontheworld.pos.mapper;

import com.ontheworld.pos.dto.product.ProductRequest;
import com.ontheworld.pos.dto.product.ProductResponse;
import com.ontheworld.pos.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.id",     target = "categoryId")
    @Mapping(source = "category.nameTh", target = "categoryNameTh")
    @Mapping(source = "category.nameEn", target = "categoryNameEn")
    @Mapping(source = "branch.id",       target = "branchId")
    @Mapping(source = "branch.nameTh",   target = "branchNameTh")
    @Mapping(source = "branch.nameEn",   target = "branchNameEn")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "stocks", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "stocks", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    void updateEntity(ProductRequest request, @MappingTarget Product product);
}
