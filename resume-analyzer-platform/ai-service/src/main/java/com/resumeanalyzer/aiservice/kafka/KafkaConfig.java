package com.resumeanalyzer.aiservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topic.ai-suggestion}")
    private String aiSuggestionTopic;

    @Bean
    public NewTopic aiSuggestionTopic() {
        return TopicBuilder
                .name(aiSuggestionTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}