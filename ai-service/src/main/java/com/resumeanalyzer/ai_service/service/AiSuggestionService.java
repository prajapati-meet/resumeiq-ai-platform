package com.resumeanalyzer.aiservice.service;

import com.resumeanalyzer.aiservice.dto.AiSuggestionRequest;
import com.resumeanalyzer.aiservice.dto.AiSuggestionResponse;
import com.resumeanalyzer.aiservice.entity.AiSuggestion;
import com.resumeanalyzer.aiservice.repository.AiSuggestionRepository;
import org.springframework.stereotype.Service;

@Service
public class AiSuggestionService {

    private final AiSuggestionRepository suggestionRepository;
    private final GeminiApiService geminiApiService;

    public AiSuggestionService(AiSuggestionRepository suggestionRepository,
                               GeminiApiService geminiApiService) {
        this.suggestionRepository = suggestionRepository;
        this.geminiApiService = geminiApiService;
    }

    public void generateSuggestion(AiSuggestionRequest request) {

        AiSuggestion suggestion = new AiSuggestion();
        suggestion.setResumeId(request.getResumeId());
        suggestion.setUserEmail(request.getUserEmail());
        suggestion.setAtsScore(request.getAtsScore());
        suggestion.setExtractedSkills(request.getExtractedSkills());
        suggestion.setMissingSkills(request.getMissingSkills());
        suggestionRepository.save(suggestion);

        try {
            String aiResponse = geminiApiService.generateSuggestion(
                    request.getAtsScore(),
                    request.getExtractedSkills(),
                    request.getMissingSkills(),
                    request.getExtractedText()
            );

            suggestion.setAiSuggestion(aiResponse);
            suggestion.setStatus("COMPLETED");
            suggestionRepository.save(suggestion);

            System.out.println("AI suggestion generated for ResumeId: "
                    + request.getResumeId());

        } catch (Exception e) {
            suggestion.setStatus("FAILED");
            suggestion.setAiSuggestion("Failed to generate suggestion: "
                    + e.getMessage());
            suggestionRepository.save(suggestion);
            System.err.println("AI suggestion failed: " + e.getMessage());
        }
    }

    public AiSuggestionResponse getSuggestion(Long resumeId) {
        AiSuggestion suggestion = suggestionRepository
                .findByResumeId(resumeId)
                .orElseThrow(() -> new RuntimeException(
                        "No AI suggestion found for resumeId: " + resumeId));

        return new AiSuggestionResponse(
                suggestion.getResumeId(),
                suggestion.getAtsScore(),
                suggestion.getAiSuggestion(),
                suggestion.getStatus(),
                "AI suggestion retrieved successfully"
        );
    }
}