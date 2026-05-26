package com.ontheworld.pos.dto.customer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoyaltyRequest {

    @NotNull
    @Min(1)
    private Integer points;

    @NotBlank
    private String reason;
}
