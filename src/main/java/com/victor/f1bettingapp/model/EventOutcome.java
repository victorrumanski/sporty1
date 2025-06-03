package com.victor.f1bettingapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "event_outcomes")
public class EventOutcome {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String externalEventId;

    @Column(nullable = false)
    private String winningDriverId;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    public EventOutcome() {
    }

    public EventOutcome(String externalEventId, String winningDriverId) {
        this.externalEventId = externalEventId;
        this.winningDriverId = winningDriverId;
        this.processedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "EventOutcome{" +
                "id=" + id +
                ", externalEventId='" + externalEventId + '\'' +
                ", winningDriverId='" + winningDriverId + '\'' +
                ", processedAt=" + processedAt +
                '}';
    }
} 