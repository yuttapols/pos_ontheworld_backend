package com.ontheworld.pos.dto.product;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CategoryResponse implements Serializable {

    private UUID id;
    private String nameTh;
    private String nameEn;
    private String descriptionTh;
    private String descriptionEn;
    private UUID branchId;
    private String branchNameTh;
    private String branchNameEn;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
