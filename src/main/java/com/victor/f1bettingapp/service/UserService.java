package com.victor.f1bettingapp.service;

import com.victor.f1bettingapp.exception.InsufficientFundsException;
import com.victor.f1bettingapp.model.User;
import com.victor.f1bettingapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User findOrCreateUser(Long externalUserId) {
        return userRepository.findByExternalUserId(externalUserId)
                .orElseGet(() -> {
                    User newUser = new User(externalUserId);
                    // Balance is set to 100 EUR by default in the User constructor
                    return userRepository.save(newUser);
                });
    }

    @Transactional(readOnly = true)
    public User findUserByExternalId(Long externalUserId) {
        return userRepository.findByExternalUserId(externalUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with external ID: " + externalUserId));
    }

    // This method might be called by BetService internally
    @Transactional
    public void updateUserBalance(Long externalUserId, BigDecimal amountChange) {
        User user = findUserByExternalId(externalUserId);
        BigDecimal newBalance = user.getBalance().add(amountChange);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("User " + externalUserId + " has insufficient funds for this operation.");
        }
        user.setBalance(newBalance);
        userRepository.save(user);
    }
}

// Custom exception for insufficient funds
// class InsufficientFundsException extends RuntimeException {
//     public InsufficientFundsException(String message) {
//         super(message);
//     }
// } 