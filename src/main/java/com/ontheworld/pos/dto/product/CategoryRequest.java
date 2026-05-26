package com.ontheworld.pos.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank
    private String nameTh;

    @NotBlank
    private String nameEn;

    private String descriptionTh;
    private String descriptionEn;
}
