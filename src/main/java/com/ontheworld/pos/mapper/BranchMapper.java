package com.ontheworld.pos.mapper;

import com.ontheworld.pos.dto.branch.BranchRequest;
import com.ontheworld.pos.dto.branch.BranchResponse;
import com.ontheworld.pos.entity.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    BranchResponse toResponse(Branch branch);

    List<BranchResponse> toResponseList(List<Branch> branches);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "users", ignore = true)
    Branch toEntity(BranchRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "users", ignore = true)
    void updateEntity(BranchRequest request, @MappingTarget Branch branch);
}
