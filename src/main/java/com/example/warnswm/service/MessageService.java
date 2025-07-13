package com.example.warnswm.service;

import com.example.warnswm.dto.MessageResponse;
import com.example.warnswm.dto.TelegramTokenResponse;
import com.example.warnswm.entity.Message;
import com.example.warnswm.entity.User;
import com.example.warnswm.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private TelegramTokenService tokenService;
    
    @Autowired
    private TelegramBotService botService;
    
    public MessageResponse sendMessage(User user, String content) {
        // Сохраняем сообщение в БД
        Message message = new Message(user, content);
        message = messageRepository.save(message);
        
        // Отправляем в Telegram если есть активный токен с привязанным чатом
        try {
            TelegramTokenResponse tokenResponse = tokenService.getCurrentToken(user);
            if (tokenResponse.getChatId() != null) {
                String formattedMessage = String.format("%s, я получил от тебя сообщение:\n%s", 
                        user.getName(), content);
                botService.sendMessage(tokenResponse.getChatId(), formattedMessage);
            }
        } catch (RuntimeException e) {
            // Нет активного токена - ничего не делаем
        }
        
        return new MessageResponse(message.getId(), message.getContent(), message.getSentAt());
    }
    
    public List<MessageResponse> getUserMessages(User user) {
        List<Message> messages = messageRepository.findByUserOrderBySentAtDesc(user);
        return messages.stream()
                .map(msg -> new MessageResponse(msg.getId(), msg.getContent(), msg.getSentAt()))
                .collect(Collectors.toList());
    }
}
