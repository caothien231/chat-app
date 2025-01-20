package com.example.chat_app.services;

import com.example.chat_app.entities.UserTopic;
import com.example.chat_app.repositories.UserTopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserTopicService {
    private final UserTopicRepository userTopicRepository;

    public UserTopicService(UserTopicRepository userTopicRepository) {
        this.userTopicRepository = userTopicRepository;
    }

    public List<String> getUserTopics(Integer userId) {
        List<UserTopic> userTopics = userTopicRepository.findByUserId(userId);
        return userTopics.stream().map(UserTopic::getTopicName).collect(Collectors.toList());
    }
}
