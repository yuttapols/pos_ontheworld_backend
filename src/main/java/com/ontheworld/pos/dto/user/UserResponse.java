package com.ontheworld.pos.dto.user;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {

    private UUID id;
    private String username;
    private String email;
    private String role;
    private UUID branchId;
    private String branchNameTh;
    private String branchNameEn;
    private boolean enabled;
    private LocalDateTime createdAt;
}
