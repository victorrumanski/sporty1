package com.victor.f1bettingapp.repository;

import com.victor.f1bettingapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByExternalUserId(Long externalUserId);
} 