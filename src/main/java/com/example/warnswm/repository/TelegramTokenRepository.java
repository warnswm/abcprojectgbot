package com.example.warnswm.repository;

import com.example.warnswm.entity.TelegramToken;
import com.example.warnswm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelegramTokenRepository extends JpaRepository<TelegramToken, Long> {
    
    Optional<TelegramToken> findByToken(String token);
    
    Optional<TelegramToken> findByUserAndIsActiveTrue(User user);
    
    Optional<TelegramToken> findByChatIdAndIsActiveTrue(String chatId);
    
    boolean existsByToken(String token);
}
