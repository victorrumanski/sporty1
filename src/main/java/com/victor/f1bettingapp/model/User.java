package com.victor.f1bettingapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "users") // "user" is often a reserved keyword in SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated primary key
    private Long id;

    @Column(unique = true, nullable = false)
    private Long externalUserId; // The User ID mentioned in requirements

    @Column(nullable = false)
    private BigDecimal balance;

    // Constructor to initialize with starting balance and externalUserId
    public User(Long externalUserId) {
        this.externalUserId = externalUserId;
        this.balance = new BigDecimal("100.00");
    }
} 