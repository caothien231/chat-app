package com.example.chat_app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chat_app.entities.UserTopic;

public interface UserTopicRepository extends JpaRepository<UserTopic, Long> {
    List<UserTopic> findByUserId(Integer userId);
    boolean existsByUserIdAndTopicName(Integer userId, String topicName);
}
