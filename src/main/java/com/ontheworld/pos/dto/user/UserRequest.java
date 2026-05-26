package com.ontheworld.pos.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class UserRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private String role;

    private UUID branchId;

    private boolean enabled = true;
}
