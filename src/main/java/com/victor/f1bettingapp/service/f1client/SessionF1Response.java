package com.victor.f1bettingapp.service.f1client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SessionF1Response {

    // Getters and Setters
    @JsonProperty("meeting_key")
    private Integer meetingKey;

    @JsonProperty("session_key")
    private Long sessionKey; // session_key can be large

    @JsonProperty("location")
    private String location;

    @JsonProperty("date_start")
    private String dateStart; // Keep as String, parsing handled in service

    @JsonProperty("date_end")
    private String dateEnd; // Keep as String

    @JsonProperty("session_type")
    private String sessionType;

    @JsonProperty("session_name")
    private String sessionName;

    @JsonProperty("country_key")
    private Integer countryKey;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("circuit_key")
    private Integer circuitKey;

    @JsonProperty("circuit_short_name")
    private String circuitShortName;

    @JsonProperty("gmt_offset")
    private String gmtOffset;

    @JsonProperty("year")
    private Integer year;

}