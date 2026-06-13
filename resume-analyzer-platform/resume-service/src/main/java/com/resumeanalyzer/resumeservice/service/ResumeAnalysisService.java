package com.resumeanalyzer.resumeservice.service;

import com.resumeanalyzer.resumeservice.entity.Resume;
import com.resumeanalyzer.resumeservice.entity.ResumeAnalysis;
import com.resumeanalyzer.resumeservice.repository.ResumeAnalysisRepository;
import com.resumeanalyzer.resumeservice.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository analysisRepository;
    private final AtsScoringService atsScoringService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.ai-suggestion}")
    private String aiSuggestionTopic;

    public ResumeAnalysisService(ResumeRepository resumeRepository,
                                 ResumeAnalysisRepository analysisRepository,
                                 AtsScoringService atsScoringService,
                                 KafkaTemplate<String, String> kafkaTemplate) {
        this.resumeRepository = resumeRepository;
        this.analysisRepository = analysisRepository;
        this.atsScoringService = atsScoringService;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void analyzeResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException(
                        "Resume not found: " + resumeId));

        resume.setStatus("PROCESSING");
        resumeRepository.save(resume);

        try {
            String resumeText = resume.getExtractedText();

            List<String> extractedSkills =
                    atsScoringService.extractSkills(resumeText);

            List<String> requiredSkills = Arrays.asList(
                    "java", "spring boot", "mysql", "git", "rest api"
            );

            List<String> missingSkills =
                    atsScoringService.findMissingSkills(
                            extractedSkills, requiredSkills);

            String education =
                    atsScoringService.extractEducation(resumeText);

            Integer atsScore = atsScoringService.calculateAtsScore(
                    resumeText, extractedSkills, requiredSkills);

            String feedback = atsScoringService.generateFeedback(
                    atsScore, missingSkills);

            ResumeAnalysis analysis = new ResumeAnalysis();
            analysis.setResume(resume);
            analysis.setAtsScore(atsScore);
            analysis.setExtractedSkills(String.join(",", extractedSkills));
            analysis.setMissingSkills(String.join(",", missingSkills));
            analysis.setEducationDetails(education);
            analysis.setFeedback(feedback);
            analysisRepository.save(analysis);

            resume.setStatus("COMPLETED");
            resumeRepository.save(resume);

            // Fire Kafka event to AI service
            // Format: resumeId|userEmail|atsScore|extractedSkills|missingSkills
            String aiEvent = resumeId + "|" +
                    resume.getUserEmail() + "|" +
                    atsScore + "|" +
                    String.join(",", extractedSkills) + "|" +
                    String.join(",", missingSkills) + "|" +
                    resumeText.substring(0,
                            Math.min(500, resumeText.length()));

            kafkaTemplate.send(aiSuggestionTopic, aiEvent);
            System.out.println("AI suggestion event sent for ResumeId: "
                    + resumeId);

        } catch (Exception e) {
            resume.setStatus("FAILED");
            resumeRepository.save(resume);
            System.err.println("Analysis failed: " + e.getMessage());
        }
    }
}