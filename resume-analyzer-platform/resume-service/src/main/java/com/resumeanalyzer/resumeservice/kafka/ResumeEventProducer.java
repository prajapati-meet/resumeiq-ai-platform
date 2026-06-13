package com.resumeanalyzer.resumeservice.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ResumeEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.resume-uploaded}")
    private String resumeUploadedTopic;

    public ResumeEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendResumeUploadedEvent(Long resumeId) {
        String message = String.valueOf(resumeId);
        kafkaTemplate.send(resumeUploadedTopic, message);
        System.out.println("Kafka Event Sent → Topic: "
                + resumeUploadedTopic + " | ResumeId: " + resumeId);
    }
}