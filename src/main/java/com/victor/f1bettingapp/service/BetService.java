package com.victor.f1bettingapp.service;

import com.victor.f1bettingapp.dto.BetDto;
import com.victor.f1bettingapp.dto.PlaceBetRequest;
import com.victor.f1bettingapp.dto.DriverDto;
import com.victor.f1bettingapp.dto.EventDto;
import com.victor.f1bettingapp.dto.ProcessEventOutcomeRequest;
import com.victor.f1bettingapp.event.EventOutcomeProcessedEvent;
import com.victor.f1bettingapp.exception.BetPlacementException;
import com.victor.f1bettingapp.exception.InsufficientFundsException;
import com.victor.f1bettingapp.model.Bet;
import com.victor.f1bettingapp.model.EventOutcome;
import com.victor.f1bettingapp.model.User;
import com.victor.f1bettingapp.repository.BetRepository;
import com.victor.f1bettingapp.repository.EventOutcomeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class BetService {

    private static final Logger logger = LoggerFactory.getLogger(BetService.class);

    private final BetRepository betRepository;
    private final UserService userService;
    private final EventService eventService;

    public BetService(BetRepository betRepository, UserService userService, EventService f1DataService,
                      EventOutcomeRepository eventOutcomeRepository, ApplicationEventPublisher eventPublisher) {
        this.betRepository = betRepository;
        this.userService = userService;
        this.eventService = f1DataService;
    }

    @Transactional
    public BetDto placeBet(PlaceBetRequest betRequest) {
        //NON-AI comment
        // some pessimistic locking is needed here, so that the user's balance is not updated with wrong values
        User user = userService.findOrCreateUser(betRequest.getUserId());

        if (user.getBalance().compareTo(betRequest.getAmount()) < 0) {
            throw new InsufficientFundsException("User " + user.getExternalUserId() + " has insufficient funds to place a bet of " + betRequest.getAmount());
        }
        
        //NON-AI comment
        // to prevent betting on a closed event, there would need to be a table that holds the Event data with status that is updated by the CloseBetsService
        // also check here if the event is finished or not so placeBet throws an error for closed Events.
        Optional<EventDto> eventOptional = eventService.getSingleEvent(betRequest.getExternalEventId(), betRequest.getDriverId());

        EventDto eventDetails = eventOptional.orElseThrow(() ->
                new BetPlacementException("Event not found: " + betRequest.getExternalEventId()));

        if (eventDetails.getDriverMarket() == null || eventDetails.getDriverMarket().isEmpty()) {
            throw new BetPlacementException("Driver " + betRequest.getDriverId() + " not found in event " + betRequest.getExternalEventId());
        }
        DriverDto driverDetails = eventDetails.getDriverMarket().get(0);

        Integer odds = driverDetails.getOdds();
        if (odds == null) {
            odds = ThreadLocalRandom.current().nextInt(2, 5);
            logger.warn("Odds not found for driver {} in event {}, using random odds: {}", betRequest.getDriverId(), betRequest.getExternalEventId(), odds);
        }


        Bet bet = new Bet();
        bet.setUser(user);
        bet.setExternalEventId(betRequest.getExternalEventId());
        bet.setDriverId(betRequest.getDriverId());
        bet.setAmount(betRequest.getAmount());
        bet.setOdds(odds);
        bet.setStatus(Bet.BetStatus.PENDING);
        bet.setCreatedAt(LocalDateTime.now());

        userService.updateUserBalance(user.getExternalUserId(), betRequest.getAmount().negate());

        Bet savedBet = betRepository.save(bet);
        logger.info("Placed bet ID {} for user {} on event {} for driver {}", savedBet.getId(), user.getExternalUserId(), eventDetails.getName(), driverDetails.getFullName());

        return mapToBetDto(savedBet);
    }


    public List<BetDto> getBetsByUserId(Long externalUserId) {
        User user = userService.findUserByExternalId(externalUserId); // Ensures user exists
        return betRepository.findByUser(user).stream()
                .map(this::mapToBetDto)
                .toList();
    }

    private BetDto mapToBetDto(Bet bet) {
        BigDecimal potentialWinnings = bet.getAmount().multiply(BigDecimal.valueOf(bet.getOdds()));
        return new BetDto(
                bet.getId(),
                bet.getUser().getExternalUserId(),
                bet.getExternalEventId(),
                bet.getDriverId(),
                bet.getAmount(),
                bet.getOdds(),
                bet.getStatus(),
                bet.getCreatedAt(),
                potentialWinnings
        );
    }
} 
