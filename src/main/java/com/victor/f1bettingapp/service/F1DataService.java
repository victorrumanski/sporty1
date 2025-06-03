package com.victor.f1bettingapp.service;

import com.victor.f1bettingapp.dto.EventDto;

import java.util.List;
import java.util.Optional;

public interface F1DataService {

    List<EventDto> searchEvents(String sessionType, Integer year, String countryCode);
    Optional<EventDto> getSingleEvent(String externalEventId, String driverId);

}