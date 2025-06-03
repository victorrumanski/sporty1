package com.victor.f1bettingapp.service;

import com.victor.f1bettingapp.dto.EventDto;
import com.victor.f1bettingapp.dto.ProcessEventOutcomeRequest;
import com.victor.f1bettingapp.event.EventOutcomeProcessedEvent;
import com.victor.f1bettingapp.model.EventOutcome;
import com.victor.f1bettingapp.repository.EventOutcomeRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
// import java.util.stream.Collectors; // If mapping from entity to DTO was needed here

@Service
@AllArgsConstructor
public class EventService {

    private final F1DataService f1DataService;
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private final EventOutcomeRepository eventOutcomeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void processEventOutcome(ProcessEventOutcomeRequest outcomeRequest) {
        logger.info("Received request to process outcome for event ID: {}. Winning driver ID: {}",
                outcomeRequest.getExternalEventId(), outcomeRequest.getWinningDriverId());

        EventOutcome eventOutcome = new EventOutcome(outcomeRequest.getExternalEventId(), outcomeRequest.getWinningDriverId());
        EventOutcome savedOutcome = eventOutcomeRepository.save(eventOutcome);
        logger.info("Saved EventOutcome ID: {} for external event ID: {}", savedOutcome.getId(), savedOutcome.getExternalEventId());

        eventPublisher.publishEvent(new EventOutcomeProcessedEvent(this, savedOutcome));
        logger.info("Published EventOutcomeProcessedEvent for event outcome ID: {}", savedOutcome.getId());
    }

    public Optional<EventDto> getSingleEvent(String externalEventId, String driverId) {
        Optional<EventDto> eventDtoOptional = f1DataService.getSingleEvent(externalEventId, driverId);

        return eventDtoOptional.map(event -> {
            setOddsForEventDrivers(event);
            return event;
        });
    }

    public List<EventDto> searchEvents(String sessionType, Integer year, String country) {
        List<EventDto> eventDtos = f1DataService.searchEvents(sessionType, year, country);

        eventDtos.forEach(this::setOddsForEventDrivers);

        return eventDtos;
    }

    private void setOddsForEventDrivers(EventDto event) {
        if (event.getDriverMarket() != null) {
            event.getDriverMarket().forEach(driver -> {
                driver.setOdds(getRandomOdd());
            });
        }
    }

    private int getRandomOdd() {
        return ThreadLocalRandom.current().nextInt(2, 5); // Generates a random int between 2 (inclusive) and 5 (exclusive)
    }

}