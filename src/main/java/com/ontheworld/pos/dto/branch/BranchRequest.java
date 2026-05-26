package com.ontheworld.pos.dto.branch;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BranchRequest {

    @NotBlank
    private String nameTh;

    @NotBlank
    private String nameEn;

    @NotBlank
    private String addressTh;

    @NotBlank
    private String addressEn;
}
