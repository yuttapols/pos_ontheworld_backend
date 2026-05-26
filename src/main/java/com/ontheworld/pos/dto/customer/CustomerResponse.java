package com.ontheworld.pos.dto.customer;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CustomerResponse implements Serializable {

    private UUID id;
    private String nameTh;
    private String nameEn;
    private String phone;
    private String email;
    private Integer loyaltyPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
