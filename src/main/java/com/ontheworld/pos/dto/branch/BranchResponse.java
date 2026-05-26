package com.ontheworld.pos.dto.branch;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BranchResponse implements Serializable {

    private UUID id;
    private String nameTh;
    private String nameEn;
    private String addressTh;
    private String addressEn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
