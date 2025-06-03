package com.victor.f1bettingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SearchEventsResponse {
    List<EventDto> events;
}
