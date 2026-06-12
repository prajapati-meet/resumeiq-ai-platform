package com.resumeanalyzer.aiservice.kafka;

import com.resumeanalyzer.aiservice.dto.AiSuggestionRequest;
import com.resumeanalyzer.aiservice.service.AiSuggestionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AiSuggestionConsumer {

    private final AiSuggestionService aiSuggestionService;

    public AiSuggestionConsumer(AiSuggestionService aiSuggestionService) {
        this.aiSuggestionService = aiSuggestionService;
    }

    @KafkaListener(
            topics = "${kafka.topic.ai-suggestion}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeAiSuggestionEvent(String message) {
        System.out.println("AI Kafka Event Received: " + message);

        try {
            String[] parts = message.split("\\|", 6);

            AiSuggestionRequest request = new AiSuggestionRequest();
            request.setResumeId(Long.parseLong(parts[0]));
            request.setUserEmail(parts[1]);
            request.setAtsScore(Integer.parseInt(parts[2]));
            request.setExtractedSkills(parts[3]);
            request.setMissingSkills(parts[4]);
            request.setExtractedText(parts.length > 5 ? parts[5] : "");

            aiSuggestionService.generateSuggestion(request);

        } catch (Exception e) {
            System.err.println("Failed to process AI event: "
                    + e.getMessage());
        }
    }
}