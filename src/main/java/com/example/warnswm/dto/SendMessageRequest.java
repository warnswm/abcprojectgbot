package com.example.warnswm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SendMessageRequest {
    
    @NotBlank(message = "Message content is required")
    @Size(max = 4000, message = "Message must not exceed 4000 characters")
    private String content;
    
    // Constructors
    public SendMessageRequest() {}
    
    public SendMessageRequest(String content) {
        this.content = content;
    }
    
    // Getters and setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}
