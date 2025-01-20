package com.example.chat_app.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KafkaTopicManager {

    private final KafkaAdmin kafkaAdmin;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public KafkaTopicManager(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    public void createTopicIfNotExists(String topicName) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            boolean topicExists = adminClient.listTopics().names().get().contains(topicName);
            if (!topicExists) {
                NewTopic topic = new NewTopic(topicName, 1, (short) 1); // 1 partition, replication factor of 1
                adminClient.createTopics(Collections.singleton(topic));
                System.out.println("Topic created: " + topicName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
