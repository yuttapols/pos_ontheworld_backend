package com.ontheworld.pos.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberRegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String nameTh;

    private String nameEn;

    private String phone;
}
