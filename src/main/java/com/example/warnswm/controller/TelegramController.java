package com.example.warnswm.controller;

import com.example.warnswm.dto.TelegramTokenResponse;
import com.example.warnswm.entity.User;
import com.example.warnswm.service.TelegramTokenService;
import com.example.warnswm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/telegram")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TelegramController {
    
    @Autowired
    private TelegramTokenService tokenService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/generate-token")
    public ResponseEntity<?> generateToken(Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName());
            TelegramTokenResponse response = tokenService.generateToken(user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/current-token")
    public ResponseEntity<?> getCurrentToken(Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName());
            TelegramTokenResponse response = tokenService.getCurrentToken(user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/unlink")
    public ResponseEntity<?> unlinkToken(Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName());
            tokenService.unlinkToken(user);
            return ResponseEntity.ok("Token successfully unlinked");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
