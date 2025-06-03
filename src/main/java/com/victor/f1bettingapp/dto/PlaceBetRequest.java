package com.victor.f1bettingapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceBetRequest {
    @NotNull(message = "User ID cannot be null")
    private Long userId; // externalUserId

    @NotNull(message = "Event ID cannot be null")
    private String externalEventId;

    @NotNull(message = "Driver ID cannot be null")
    private String driverId; // externalDriverId

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
} 