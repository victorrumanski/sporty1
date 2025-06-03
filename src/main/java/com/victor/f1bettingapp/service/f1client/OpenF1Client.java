package com.victor.f1bettingapp.service.f1client;

import com.victor.f1bettingapp.dto.DriverDto;
import com.victor.f1bettingapp.dto.EventDto;
import com.victor.f1bettingapp.service.F1DataService;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class OpenF1Client implements F1DataService {

    private static final Logger logger = LoggerFactory.getLogger(OpenF1Client.class);
    private final RestTemplate restTemplate;
    private final String apiBaseUrl;
    private final ExecutorService executorService; // For parallel driver fetching
    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public OpenF1Client(RestTemplate restTemplate, @Value("${f1api.base-url:https://api.openf1.org/v1}") String apiBaseUrl) {
        this.restTemplate = restTemplate;
        this.apiBaseUrl = apiBaseUrl;
        // Using a cached thread pool, good for many short-lived asynchronous tasks.
        // Consider a fixed thread pool if a more bounded resource usage is needed.
        this.executorService = Executors.newCachedThreadPool();
    }

    public Optional<EventDto> getSingleEvent(String externalEventId, String driverId) {
        List<EventDto> results = fetchEvents(externalEventId, driverId, null, null, null);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }

    public List<EventDto> searchEvents(String sessionType, Integer year, String countryCode) {
        return fetchEvents(null, null, sessionType, year, countryCode);
    }

    private List<EventDto> fetchEvents(String externalEventId, String driverId, String sessionType, Integer year, String countryCode) {
        List<SessionF1Response> sessionResponses = fetchSessionF1ResponsesFromApi(externalEventId, sessionType, year, countryCode);

        if (sessionResponses == null || sessionResponses.isEmpty()) {
            return Collections.emptyList();
        }

        // Prepare for parallel fetching of driver data
        Map<String, CompletableFuture<List<DriverF1Response>>> driverFuturesMap =
                sessionResponses.stream()
                        .map(sr -> String.valueOf(sr.getSessionKey()))
                        .distinct() // Fetch drivers only once per unique session key
                        .collect(Collectors.toMap(
                                sessionKey -> sessionKey,
                                sessionKey -> CompletableFuture.supplyAsync(
                                        () -> fetchDriverF1ResponsesFromApi(sessionKey, driverId), // Pass driverId for filtering
                                        executorService
                                )
                        ));

        // Wait for all driver data fetches to complete
        CompletableFuture.allOf(driverFuturesMap.values().toArray(new CompletableFuture[0])).join();

        // Now, map sessions to EventDto, attaching the fetched driver data
        List<EventDto> eventDtos = new ArrayList<>();
        for (SessionF1Response sessionResponse : sessionResponses) {
            String sessionKey = String.valueOf(sessionResponse.getSessionKey());
            try {
                List<DriverF1Response> driverF1Responses = driverFuturesMap.get(sessionKey).join(); // .join() is safe here
                eventDtos.add(mapToEventDto(sessionResponse, driverF1Responses));
            } catch (Exception e) {
                logger.error("Error processing drivers for session key {}: {}", sessionKey, e.getMessage(), e);
                // Optionally add eventDto with empty driver market or skip
                 eventDtos.add(mapToEventDto(sessionResponse, Collections.emptyList()));
            }
        }
        return eventDtos;
    }

    private List<SessionF1Response> fetchSessionF1ResponsesFromApi(String externalEventId, String sessionType, Integer year, String countryCode) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + "/sessions");

        if (externalEventId != null && !externalEventId.isEmpty()) {
            builder.queryParam("session_key", externalEventId);
        } else {
            if (sessionType != null && !sessionType.isEmpty()) {
                builder.queryParam("session_name", sessionType);
            }
            if (year != null) {
                builder.queryParam("year", year);
            }
            if (countryCode != null && !countryCode.isEmpty()) {
                builder.queryParam("country_code", countryCode);
            }
        }

        String url = builder.toUriString();
        logger.info("Fetching session responses from OpenF1 API: {}", url);

        try {
            ResponseEntity<List<SessionF1Response>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<SessionF1Response>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching session responses from OpenF1 API: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Updated to accept pre-fetched driver responses
    private EventDto mapToEventDto(SessionF1Response sessionData, List<DriverF1Response> driverResponses) {
        String startDate = null;
        String dateStr = sessionData.getDateStart();
        if (dateStr != null) {
            try {
                startDate = API_DATE_FORMATTER.parse(dateStr, Instant::from).toString();
            } catch (Exception e) {
                logger.warn("Could not parse date_start: {} due to {}", dateStr, e.getMessage());
            }
        }

        List<DriverDto> driverMarket = driverResponses.stream()
                .map(this::mapToDriverDto)
                .toList();

        return EventDto.builder()
                .externalEventId(String.valueOf(sessionData.getSessionKey()))
                .name(sessionData.getSessionName())
                .sessionType(sessionData.getSessionType())
                .year(sessionData.getYear())
                .country(sessionData.getCountryName())
                .circuitName(sessionData.getCircuitShortName())
                .startDate(startDate)
                .driverMarket(driverMarket)
                .build();
    }

    // This method now primarily serves the asynchronous calls
    private List<DriverF1Response> fetchDriverF1ResponsesFromApi(String sessionKey, String driverId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + "/drivers")
                .queryParam("session_key", sessionKey);

        if (driverId != null) { // If driverId is provided, add it as a filter
            builder.queryParam("driver_number", driverId);
        }

        String url = builder.toUriString();
        logger.info("Fetching driver responses for session_key {} (driver_id filter: {}) from OpenF1 API: {}", sessionKey, driverId, url);

        try {
            ResponseEntity<List<DriverF1Response>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DriverF1Response>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching driver responses from OpenF1 API for session_key {}: {}", sessionKey, e.getMessage(), e);
            return Collections.emptyList(); // Return empty list on error to not break the entire flow
        }
    }

    private DriverDto mapToDriverDto(DriverF1Response driverData) {
        String name = driverData.getFullName();
        Long driverNumber = driverData.getDriverNumber() != null ? driverData.getDriverNumber().longValue() : null;
        return DriverDto.builder()
                .fullName(name)
                .driverId(driverNumber != null ? String.valueOf(driverNumber) : null)
                .build();
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (executorService != null && !executorService.isShutdown()) {
            logger.info("Shutting down OpenF1Client executor service");
            executorService.shutdown();
        }
    }
}