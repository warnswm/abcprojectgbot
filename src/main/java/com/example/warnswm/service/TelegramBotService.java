package com.example.warnswm.service;

import com.example.warnswm.entity.TelegramToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service
public class TelegramBotService extends TelegramLongPollingBot {
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    @Value("${telegram.bot.username}")
    private String botUsername;
    
    @Autowired
    private TelegramTokenService tokenService;
    
    @Override
    public String getBotToken() {
        return botToken;
    }
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            String messageText = message.getText().trim();
            
            // Обработка команды /unlink
            if (messageText.equals("/unlink")) {
                handleUnlinkCommand(chatId);
                return;
            }
            
            // Обработка команды /start
            if (messageText.equals("/start")) {
                sendMessage(chatId, "Привет! Отправьте мне ваш токен для привязки к этому чату.\n\nДоступные команды:\n/unlink - отвязать токен");
                return;
            }
            
            // Проверяем, есть ли уже привязанный чат для этого chatId
            Optional<TelegramToken> existingToken = tokenService.findByChatId(chatId);
            
            if (existingToken.isPresent()) {
                // Чат уже привязан, отправляем уведомление
                sendMessage(chatId, "Ваш чат уже привязан к токену: " + existingToken.get().getToken() + "\n\nИспользуйте /unlink для отвязки");
            } else {
                // Пытаемся найти токен по тексту сообщения
                Optional<TelegramToken> tokenOpt = tokenService.findByToken(messageText);
                
                if (tokenOpt.isPresent()) {
                    TelegramToken token = tokenOpt.get();
                    if (token.getChatId() == null) {
                        // Привязываем токен к чату
                        tokenService.bindChatId(messageText, chatId);
                        sendMessage(chatId, "Токен успешно привязан! Теперь вы будете получать сообщения от API.\n\nИспользуйте /unlink для отвязки");
                    } else {
                        sendMessage(chatId, "Этот токен уже привязан к другому чату.");
                    }
                } else {
                    sendMessage(chatId, "Неизвестная команда. Отправьте мне ваш токен для привязки к этому чату.\n\nДоступные команды:\n/unlink - отвязать токен");
                }
            }
        }
    }
    
    private void handleUnlinkCommand(String chatId) {
        try {
            Optional<TelegramToken> tokenOpt = tokenService.findByChatId(chatId);
            
            if (tokenOpt.isPresent()) {
                TelegramToken token = tokenOpt.get();
                // Отвязываем токен
                token.setIsActive(false);
                token.setChatId(null);
                tokenService.saveToken(token);
                
                sendMessage(chatId, "Токен успешно отвязан! Сообщения больше не будут приходить в этот чат.\n\nДля повторной привязки отправьте новый токен.");
            } else {
                sendMessage(chatId, "У вас нет привязанного токена. Отправьте токен для привязки к этому чату.");
            }
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при отвязке токена. Попробуйте позже.");
            e.printStackTrace();
        }
    }
    
    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
