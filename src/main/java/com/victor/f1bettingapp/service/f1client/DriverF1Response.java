package com.victor.f1bettingapp.service.f1client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DriverF1Response {

    // Getters and Setters
    @JsonProperty("meeting_key")
    private Integer meetingKey;

    @JsonProperty("session_key")
    private Long sessionKey;

    @JsonProperty("driver_number")
    private Integer driverNumber;

    @JsonProperty("broadcast_name")
    private String broadcastName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("name_acronym")
    private String nameAcronym;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("team_colour")
    private String teamColour;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("headshot_url")
    private String headshotUrl;

    @JsonProperty("country_code")
    private String countryCode;

}