package com.victor.f1bettingapp.dto;

import com.victor.f1bettingapp.model.Bet;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BetDto {
    private Long id;
    private Long userId; // externalUserId
    private String externalEventId;
    private String driverId; // externalDriverId
    private BigDecimal amount;
    private Integer odds;
    private Bet.BetStatus status;
    private LocalDateTime createdAt;
    private BigDecimal potentialWinnings; // amount * odds

    public BetDto(Long id, Long userId, String externalEventId, String driverId, BigDecimal amount, Integer odds, Bet.BetStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.externalEventId = externalEventId;
        this.driverId = driverId;
        this.amount = amount;
        this.odds = odds;
        this.status = status;
        this.createdAt = createdAt;
        if (amount != null && odds != null) {
            this.potentialWinnings = amount.multiply(BigDecimal.valueOf(odds));
        }
    }
} 