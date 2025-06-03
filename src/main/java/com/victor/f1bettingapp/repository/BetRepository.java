package com.victor.f1bettingapp.repository;

import com.victor.f1bettingapp.model.Bet;
import com.victor.f1bettingapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByUser(User user);
    List<Bet> findByExternalEventIdAndStatus(String externalEventId, Bet.BetStatus status);
} 