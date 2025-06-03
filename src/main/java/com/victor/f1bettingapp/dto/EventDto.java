package com.victor.f1bettingapp.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    private String externalEventId; // From API, e.g. session_key
    private String name;
    private String sessionType;
    private Integer year;
    private String country;
    private String circuitName;
    private String startDate; // ISO 8601 format String
    private List<DriverDto> driverMarket;
} 