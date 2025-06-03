package com.victor.f1bettingapp.controller;

import com.victor.f1bettingapp.config.openapi.CommonApiResponses;
import com.victor.f1bettingapp.dto.EventDto;
import com.victor.f1bettingapp.dto.ProcessEventOutcomeRequest;
import com.victor.f1bettingapp.dto.SearchEventsResponse;
import com.victor.f1bettingapp.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "F1 Events Management", description = "APIs for listing Formula 1 events and their details.")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * To list F1 events.
     * Can be filtered by sessionType, year, and country.
     * Example: GET /api/v1/events?sessionType=Race&year=2023&country=Monaco
     */
    @GetMapping
    @Operation(summary = "List F1 Events")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of events",
                     content = @Content(mediaType = "application/json",
                             schema = @Schema(implementation = SearchEventsResponse.class))),
        @ApiResponse(responseCode = "204", description = "No events found matching the criteria",
                     content = @Content)
    })
    @CommonApiResponses
    // NON-AI comment
    // This endpoint only proxies the calls to the F1 api
    // this means it is 100% IO bound
    // by default tomcat only uses 200 physical threads maximum, so we can only handle 200 users
    // webflux is an option to achieve much higher throughput, but it has the burden of async/flux programming
    // with boot 3.2 and Java 21, Virtual Threads arrived,
    // now this endpoint can have the same performance that a webflux implementation would achieve,
    // but withtou the burden of async/reactive programming constructs
    public ResponseEntity<SearchEventsResponse> searchEvents(
            @Parameter(description = "Filter by session type (e.g., Race, Qualifying, Practice)")
            @RequestParam(required = false) String sessionType,
            @Parameter(description = "Filter by year (e.g., 2023)")
            @RequestParam(required = false) Integer year,
            @Parameter(description = "Filter by country name or code (e.g., Monaco, AU)")
            @RequestParam(required = false) String country) {

        List<EventDto> events = eventService.searchEvents(sessionType, year, country);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(SearchEventsResponse.builder().events(events).build());
    }

    @PostMapping("/outcomes")
    @Operation(summary = "Simulate Event Outcome",
            description = "Simulates the outcome of a finished F1 event. This will update the status of pending bets and adjust user balances for won bets.")

    public ResponseEntity<Void> processEventOutcome(@Valid @RequestBody ProcessEventOutcomeRequest outcomeRequestDto) {
        eventService.processEventOutcome(outcomeRequestDto);
        return ResponseEntity.ok().build();
    }
} 