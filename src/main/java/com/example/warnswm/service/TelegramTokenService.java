package com.example.warnswm.service;

import com.example.warnswm.dto.TelegramTokenResponse;
import com.example.warnswm.entity.TelegramToken;
import com.example.warnswm.entity.User;
import com.example.warnswm.repository.TelegramTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TelegramTokenService {
    
    @Autowired
    private TelegramTokenRepository tokenRepository;
    
    public TelegramTokenResponse generateToken(User user) {
        // Деактивируем предыдущие токены для пользователя
        Optional<TelegramToken> existingToken = tokenRepository.findByUserAndIsActiveTrue(user);
        if (existingToken.isPresent()) {
            TelegramToken token = existingToken.get();
            token.setIsActive(false);
            tokenRepository.save(token);
        }
        
        // Создаем новый токен
        String tokenValue = UUID.randomUUID().toString();
        TelegramToken telegramToken = new TelegramToken(user, tokenValue);
        telegramToken = tokenRepository.save(telegramToken);
        
        return new TelegramTokenResponse(
                telegramToken.getToken(),
                telegramToken.getChatId(),
                telegramToken.getIsActive(),
                telegramToken.getCreatedAt()
        );
    }
    
    public TelegramTokenResponse getCurrentToken(User user) {
        TelegramToken token = tokenRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new RuntimeException("No active token found for user"));
        
        return new TelegramTokenResponse(
                token.getToken(),
                token.getChatId(),
                token.getIsActive(),
                token.getCreatedAt()
        );
    }
    
    public void unlinkToken(User user) {
        Optional<TelegramToken> tokenOpt = tokenRepository.findByUserAndIsActiveTrue(user);
        if (tokenOpt.isPresent()) {
            TelegramToken token = tokenOpt.get();
            token.setIsActive(false);
            token.setChatId(null);
            tokenRepository.save(token);
        } else {
            throw new RuntimeException("No active token found for user");
        }
    }
    
    public void bindChatId(String token, String chatId) {
        TelegramToken telegramToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        telegramToken.setChatId(chatId);
        tokenRepository.save(telegramToken);
    }
    
    public Optional<TelegramToken> findByChatId(String chatId) {
        return tokenRepository.findByChatIdAndIsActiveTrue(chatId);
    }
    
    public Optional<TelegramToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
    
    public TelegramToken saveToken(TelegramToken token) {
        return tokenRepository.save(token);
    }
}
