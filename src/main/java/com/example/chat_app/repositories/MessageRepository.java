package com.example.chat_app.repositories;

import com.example.chat_app.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByRecipientId(Integer recipientId);
    List<Message> findBySenderIdAndRecipientId(Integer senderId, Integer recipientId);
}
