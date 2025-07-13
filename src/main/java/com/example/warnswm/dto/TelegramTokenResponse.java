package com.example.warnswm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class TelegramTokenResponse {
    
    private String token;
    private String chatId;
    private boolean isActive;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // Constructors
    public TelegramTokenResponse() {}
    
    public TelegramTokenResponse(String token, String chatId, boolean isActive, LocalDateTime createdAt) {
        this.token = token;
        this.chatId = chatId;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }
    
    // Getters and setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getChatId() {
        return chatId;
    }
    
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
