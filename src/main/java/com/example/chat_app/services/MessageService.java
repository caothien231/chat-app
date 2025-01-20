package com.example.chat_app.services;

import com.example.chat_app.entities.Message;
import com.example.chat_app.entities.UserTopic;
import com.example.chat_app.kafka.MessageProducer;
import com.example.chat_app.repositories.MessageRepository;
import com.example.chat_app.repositories.UserTopicRepository;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserTopicRepository userTopicRepository;
    private final MessageProducer messageProducer;
    private final AdminClient adminClient;
    private final WebSocketService webSocketService;

    public MessageService(WebSocketService webSocketService, MessageRepository messageRepository, MessageProducer messageProducer, UserTopicRepository userTopicRepository, AdminClient adminClient) {
        this.webSocketService = webSocketService;
        this.messageRepository = messageRepository;
        this.messageProducer = messageProducer;
        this.userTopicRepository = userTopicRepository;
        this.adminClient = adminClient;
    }

    public Message sendMessage(Integer senderId, Integer recipientId, String content) {
        // Create a new message entity
        Message message = new Message();
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setContent(content);
        message.setTimestamp(new Date());

        // Save the message in the database
        messageRepository.save(message);

        // Create topic name based on user IDs
        String topicName = "chat-" + Math.min(senderId, recipientId) + "-" + Math.max(senderId, recipientId);

        // Check if the topic already exists in Kafka (this can be done using AdminClient)
        if (!topicExists(topicName)) {
            createTopic(topicName);
        }

        // Update user-topic mapping for both sender and recipient
        if (!isUserSubscribedToTopic(senderId, topicName)) {
            updateUserTopicMapping(senderId, topicName);
        }
        if (!isUserSubscribedToTopic(recipientId, topicName)) {
            //use websocket to notify the recepitent subscripted to the topic
            notifyRecipientToSubscribe(recipientId, topicName);
            updateUserTopicMapping(recipientId, topicName);
        }
        // Send the message content to the Kafka topic
        messageProducer.sendMessage(topicName, message.toString()); // Consider serializing the message to JSON
        return message;
    }

    private boolean isUserSubscribedToTopic(Integer userId, String topicName) {
        return userTopicRepository.existsByUserIdAndTopicName(userId, topicName);
    }

    private void notifyRecipientToSubscribe(Integer recipientId, String topicName) {
        // Send a notification message to the recipient via WebSocket
        String notificationMessage = "subscribe:" + topicName;
        webSocketService.sendMessageToUser(recipientId, notificationMessage); // Implement this method in WebSocketService
    }

    private void createTopic(String topicName) {
        NewTopic newTopic = new NewTopic(topicName, 1, (short) 1); // 1 partition, replication factor of 1
        try {
            adminClient.createTopics(Collections.singleton(newTopic));
        } catch (TopicExistsException e) {
            // Topic already exists, no action needed
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
        }
    }

    private boolean topicExists(String topicName) {
        try {
            Map<String, TopicDescription> topics = adminClient.describeTopics(Collections.singletonList(topicName)).all().get();
            return topics.containsKey(topicName);
        } catch (ExecutionException | InterruptedException e) {
            return false; // If an error occurs, assume the topic does not exist
        }
    }

    private void updateUserTopicMapping(Integer userId, String topicName) {
        // Check if mapping exists
        if (!userTopicRepository.existsByUserIdAndTopicName(userId, topicName)) {
            UserTopic userTopic = new UserTopic();
            userTopic.setUserId(userId);
            userTopic.setTopicName(topicName);
            userTopicRepository.save(userTopic);
        }
    }

    public List<Message> getMessages(Integer userId1, Integer userId2) {
        // Fetch messages where userId1 is the sender and userId2 is the recipient
        List<Message> messagesFromUser1 = messageRepository.findBySenderIdAndRecipientId(userId1, userId2);
        // Fetch messages where userId2 is the sender and userId1 is the recipient
        List<Message> messagesFromUser2 = messageRepository.findBySenderIdAndRecipientId(userId2, userId1);

        // Combine both lists
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(messagesFromUser1);
        allMessages.addAll(messagesFromUser2);
        
        return allMessages;
    }
}
