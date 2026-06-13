package com.resumeanalyzer.resumeservice.kafka;

import com.resumeanalyzer.resumeservice.service.ResumeAnalysisService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResumeEventConsumer {

    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeEventConsumer(ResumeAnalysisService resumeAnalysisService) {
        this.resumeAnalysisService = resumeAnalysisService;
    }

    @KafkaListener(
            topics = "${kafka.topic.resume-uploaded}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeResumeUploadedEvent(String resumeId) {
        System.out.println("Kafka Event Received → ResumeId: " + resumeId);

        resumeAnalysisService.analyzeResume(Long.parseLong(resumeId));
    }
}