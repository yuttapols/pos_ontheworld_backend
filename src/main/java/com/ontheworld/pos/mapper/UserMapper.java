package com.ontheworld.pos.mapper;

import com.ontheworld.pos.dto.user.UserResponse;
import com.ontheworld.pos.entity.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "branch.id",      target = "branchId")
    @Mapping(source = "branch.nameTh",  target = "branchNameTh")
    @Mapping(source = "branch.nameEn",  target = "branchNameEn")
    @Mapping(source = "role",           target = "role", qualifiedByName = "roleToString")
    UserResponse toResponse(UserAccount user);

    List<UserResponse> toResponseList(List<UserAccount> users);

    @org.mapstruct.Named("roleToString")
    default String roleToString(com.ontheworld.pos.entity.Role role) {
        return role == null ? null : role.name();
    }
}
