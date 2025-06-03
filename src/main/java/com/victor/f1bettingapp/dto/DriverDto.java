package com.victor.f1bettingapp.dto;

import lombok.*;

@Data
@Builder
public class DriverDto {
    private String fullName;
    private String driverId; // externalDriverId
    private Integer odds; // Dynamic odds (2, 3, or 4)
} 