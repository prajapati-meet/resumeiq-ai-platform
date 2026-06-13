package com.resumeanalyzer.aiservice.controller;

import com.resumeanalyzer.aiservice.dto.AiSuggestionResponse;
import com.resumeanalyzer.aiservice.service.AiSuggestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiSuggestionService aiSuggestionService;

    public AiController(AiSuggestionService aiSuggestionService) {
        this.aiSuggestionService = aiSuggestionService;
    }

    @GetMapping("/suggestion/{resumeId}")
    public ResponseEntity<AiSuggestionResponse> getSuggestion(
            @PathVariable Long resumeId) {
        return ResponseEntity.ok(
                aiSuggestionService.getSuggestion(resumeId));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Service is running!");
    }
}