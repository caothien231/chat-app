package com.example.chat_app.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessageToUser(Integer userId, String message) {
        // Send a message to the specified user (assuming a simple user-specific topic)
        messagingTemplate.convertAndSend("/topic/user/" + userId, message);
    }
}
