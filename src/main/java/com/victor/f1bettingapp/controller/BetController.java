package com.victor.f1bettingapp.controller;

import com.victor.f1bettingapp.dto.BetDto;
import com.victor.f1bettingapp.dto.PlaceBetRequest;
import com.victor.f1bettingapp.dto.ProcessEventOutcomeRequest;
import com.victor.f1bettingapp.service.BetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bets")
@Tag(name = "Betting Operations", description = "APIs for placing bets, simulating event outcomes, and viewing user bets.")
public class BetController {

    private final BetService betService;

    public BetController(BetService betService) {
        this.betService = betService;
    }

    /**
     * To place a bet.
     * User ID is passed as part of the request body.
     */
    @PostMapping()
    @Operation(summary = "Place a Bet",
            description = "Allows a user to place a single bet on a driver to win a specified F1 event. User is created with 100 EUR balance if not existing.")
    public ResponseEntity<BetDto> placeBet(@Valid @RequestBody PlaceBetRequest betRequestDto) {
        BetDto placedBet = betService.placeBet(betRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(placedBet);
    }

    /**
     * (Bonus) To list bets for a specific user.
     * Example: GET /api/v1/users/123/bets
     */
    @GetMapping("/{userId}")
    @Operation(summary = "List Bets by User",
            description = "Retrieves all bets placed by a specific user, identified by their external user ID.")
    public ResponseEntity<List<BetDto>> getUserBets(
            @Parameter(description = "External ID of the user whose bets are to be retrieved.", required = true, example = "123")
            @PathVariable Long userId) {
        List<BetDto> bets = betService.getBetsByUserId(userId);
        if (bets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bets);
    }
} 