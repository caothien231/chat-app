package com.example.chat_app.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    @KafkaListener(topics = "test-topic", groupId = "group_id") // Replace with your topic name
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
        // Add your message handling logic here
    }
}