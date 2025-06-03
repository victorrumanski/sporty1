package com.victor.f1bettingapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessEventOutcomeRequest {
    @NotNull(message = "Event ID cannot be null")
    private String externalEventId;

    @NotNull(message = "Winning driver ID cannot be null")
    private String winningDriverId; // externalDriverId
} 