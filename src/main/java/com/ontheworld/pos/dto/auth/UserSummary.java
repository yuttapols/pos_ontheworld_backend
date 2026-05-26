package com.ontheworld.pos.dto.auth;

import com.ontheworld.pos.entity.UserAccount;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserSummary {

    private final UUID id;
    private final String username;
    private final String email;
    private final String role;
    private final UUID branchId;
    private final String branchNameTh;
    private final String branchNameEn;
    private final UUID customerId;

    public UserSummary(UserAccount user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.branchId = user.getBranch() != null ? user.getBranch().getId() : null;
        this.branchNameTh = user.getBranch() != null ? user.getBranch().getNameTh() : null;
        this.branchNameEn = user.getBranch() != null ? user.getBranch().getNameEn() : null;
        this.customerId = user.getCustomer() != null ? user.getCustomer().getId() : null;
    }
}
