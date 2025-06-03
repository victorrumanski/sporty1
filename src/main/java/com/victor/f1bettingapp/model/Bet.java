package com.victor.f1bettingapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // We'll use externalEventId to link to the event, as the Event entity might be created on-demand from the API
    // or we might decide not to persist all events if they are too numerous.
    // For simplicity, we can also link to a persisted Event entity if available.
    @Column(nullable = false)
    private String externalEventId;

    @Column(nullable = false)
    private String driverId; // externalDriverId of the driver bet on

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer odds; // The odds at the time of placing the bet (2, 3, or 4)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BetStatus status = BetStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum BetStatus {
        PENDING, WON, LOST
    }
} 