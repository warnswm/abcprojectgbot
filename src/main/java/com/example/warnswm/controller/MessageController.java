package com.example.warnswm.controller;

import com.example.warnswm.dto.MessageResponse;
import com.example.warnswm.dto.SendMessageRequest;
import com.example.warnswm.entity.User;
import com.example.warnswm.service.MessageService;
import com.example.warnswm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody SendMessageRequest request, 
                                       Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName());
            MessageResponse response = messageService.sendMessage(user, request.getContent());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyMessages(Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName());
            List<MessageResponse> messages = messageService.getUserMessages(user);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
