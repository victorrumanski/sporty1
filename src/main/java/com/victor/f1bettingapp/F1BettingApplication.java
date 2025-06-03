package com.victor.f1bettingapp;

import com.victor.f1bettingapp.dto.PlaceBetRequest;
import com.victor.f1bettingapp.service.BetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableAsync
public class F1BettingApplication {

    private static final Logger log = LoggerFactory.getLogger(F1BettingApplication.class);

    private final BetService betService;

    public F1BettingApplication(BetService betService) {
        this.betService = betService;
    }

    public static void main(String[] args) {
        SpringApplication.run(F1BettingApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            log.info("Starting database initialization with specific bets...");

            List<Long> userIds = Arrays.asList(1L, 2L, 3L); // Sample user IDs
            String externalEventId = "9987";
            List<String> driverIds = Arrays.asList("1", "4", "6", "12", "14");
            Random random = new Random();

            for (String driverId : driverIds) {
                try {
                    Long randomUserId = userIds.get(random.nextInt(userIds.size()));
                    BigDecimal betAmount = BigDecimal.TEN;

                    PlaceBetRequest betRequest = new PlaceBetRequest();
                    betRequest.setUserId(randomUserId);
                    betRequest.setExternalEventId(externalEventId);
                    betRequest.setDriverId(driverId);
                    betRequest.setAmount(betAmount);
                    // Odds are set within BetService if not found from eventDetails

                    log.info("Placing bet: UserID={}, EventID={}, DriverID={}, Amount={}",
                            randomUserId, externalEventId, driverId, betAmount);
                    betService.placeBet(betRequest);
                    log.info("Successfully placed bet for UserID={}, DriverID={}", randomUserId, driverId);
                } catch (Exception e) {
                    log.error("Error placing bet for DriverID={}: {}", driverId, e.getMessage(), e);
                }
            }
            log.info("Finished database initialization with specific bets.");
        };
    }
} 