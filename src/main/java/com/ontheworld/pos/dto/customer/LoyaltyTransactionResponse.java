package com.ontheworld.pos.dto.customer;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LoyaltyTransactionResponse {
    private UUID id;
    private String type;
    private Integer points;
    private String reason;
    private UUID referenceId;
    private String createdBy;
    private LocalDateTime createdAt;
}
