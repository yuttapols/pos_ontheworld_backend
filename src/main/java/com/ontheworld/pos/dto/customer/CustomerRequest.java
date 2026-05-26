package com.ontheworld.pos.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotBlank
    private String nameTh;

    @NotBlank
    private String nameEn;

    private String phone;

    @Email
    private String email;
}
