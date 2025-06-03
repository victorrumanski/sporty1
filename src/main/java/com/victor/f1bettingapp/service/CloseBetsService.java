package com.victor.f1bettingapp.service;

import com.victor.f1bettingapp.event.EventOutcomeProcessedEvent;
import com.victor.f1bettingapp.model.Bet;
import com.victor.f1bettingapp.model.EventOutcome;
import com.victor.f1bettingapp.repository.BetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CloseBetsService {

    private static final Logger logger = LoggerFactory.getLogger(CloseBetsService.class);

    private final BetRepository betRepository;
    private final UserService userService; // Assuming UserService has updateUserBalance

    public CloseBetsService(BetRepository betRepository, UserService userService) {
        this.betRepository = betRepository;
        this.userService = userService;
    }

    @Async
    @EventListener
    @Transactional
    public void handle(EventOutcomeProcessedEvent event) {
        EventOutcome outcome = event.getEventOutcome();
        logger.info("Asynchronously processing event outcome ID: {} for external event ID: {}", outcome.getId(), outcome.getExternalEventId());

        List<Bet> pendingBets = betRepository.findByExternalEventIdAndStatus(outcome.getExternalEventId(), Bet.BetStatus.PENDING);

        if (pendingBets.isEmpty()) {
            logger.info("No pending bets found for event ID: {} during async processing.", outcome.getExternalEventId());
            return;
        }

        logger.info("Async: Processing outcome for event ID: {}. Winning driver ID: {}. Found {} pending bets.",
                outcome.getExternalEventId(), outcome.getWinningDriverId(), pendingBets.size());

        for (Bet bet : pendingBets) {
            if (bet.getDriverId().equals(outcome.getWinningDriverId())) {
                bet.setStatus(Bet.BetStatus.WON);
                BigDecimal prize = bet.getAmount().multiply(BigDecimal.valueOf(bet.getOdds()));
                // Ensure userService.updateUserBalance is robust and potentially transactional itself
                // or consider if this operation needs to be part of the same transaction.
                // For simplicity here, assuming userService.updateUserBalance handles its own transaction or is safe to call.
                userService.updateUserBalance(bet.getUser().getExternalUserId(), prize);
                logger.info("Async: Bet ID {} WON. User {} awarded {} EUR.", bet.getId(), bet.getUser().getExternalUserId(), prize);
            } else {
                bet.setStatus(Bet.BetStatus.LOST);
                logger.info("Async: Bet ID {} LOST. User {}.", bet.getId(), bet.getUser().getExternalUserId());
            }
            betRepository.save(bet); // Save each bet individually within the transaction
        }
        // NON-AI comment
        // this method uses a loop to process all bets
        // ideally each bet would be a message in kafka to be processed on its own transaction for reliability matters
        logger.info("Finished async processing for event outcome ID: {}", outcome.getId());
    }
} 