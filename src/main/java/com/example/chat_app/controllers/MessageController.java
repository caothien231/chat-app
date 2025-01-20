package com.example.chat_app.controllers;

import com.example.chat_app.entities.Message;
import com.example.chat_app.entities.User;
import com.example.chat_app.kafka.MessageProducer; // Import the MessageProducer
import com.example.chat_app.services.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;
    private final MessageProducer messageProducer; // Inject MessageProducer

    public MessageController(MessageService messageService, MessageProducer messageProducer) {
        this.messageService = messageService;
        this.messageProducer = messageProducer; // Initialize MessageProducer
    }

    @PostMapping("/sent")
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Integer senderId = currentUser.getId();

        // Send the message using your existing service method (for persistence)
        Message sentMessage = messageService.sendMessage(senderId, message.getRecipientId(), message.getContent());
        return ResponseEntity.ok(sentMessage);
    }

    @GetMapping("/{friendId}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Integer friendId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Integer userId = currentUser.getId();

        List<Message> messages = messageService.getMessages(userId, friendId);
        return ResponseEntity.ok(messages);
    }
}
