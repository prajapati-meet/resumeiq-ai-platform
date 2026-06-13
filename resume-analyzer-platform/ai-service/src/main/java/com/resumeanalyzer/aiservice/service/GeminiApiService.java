package com.resumeanalyzer.aiservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class GeminiApiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GeminiApiService(WebClient.Builder webClientBuilder,
                            ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String generateSuggestion(Integer atsScore,
                                     String extractedSkills,
                                     String missingSkills,
                                     String resumeText) {
        try {
            String prompt = buildPrompt(atsScore, extractedSkills,
                    missingSkills, resumeText);

            ObjectNode requestBody = objectMapper.createObjectNode();
            ArrayNode contents = objectMapper.createArrayNode();
            ObjectNode content = objectMapper.createObjectNode();
            ArrayNode parts = objectMapper.createArrayNode();
            ObjectNode part = objectMapper.createObjectNode();

            part.put("text", prompt);
            parts.add(part);
            content.set("parts", parts);
            contents.add(content);
            requestBody.set("contents", contents);

            String response = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractTextFromResponse(response);

        } catch (Exception e) {
            System.err.println("Gemini API error: " + e.getMessage());
            return generateFallbackSuggestion(atsScore, missingSkills);
        }
    }

    private String buildPrompt(Integer atsScore,
                               String extractedSkills,
                               String missingSkills,
                               String resumeText) {
        return String.format("""
                You are an expert resume coach and ATS optimization specialist.
                
                Analyze this resume data and provide detailed improvement suggestions:
                
                ATS Score: %d/100
                Skills Found: %s
                Missing Skills: %s
                
                Resume Content (first 500 chars):
                %s
                
                Please provide:
                1. Overall assessment of the resume
                2. Top 3 specific improvements needed
                3. Skills to add based on missing skills
                4. Formatting recommendations
                5. Keywords to include for better ATS scoring
                
                Keep response concise and actionable.
                """,
                atsScore,
                extractedSkills,
                missingSkills,
                resumeText.length() > 500
                        ? resumeText.substring(0, 500)
                        : resumeText);
    }

    private String extractTextFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText("No suggestion available");
        } catch (Exception e) {
            return "Could not parse Gemini response: " + e.getMessage();
        }
    }

    private String generateFallbackSuggestion(Integer atsScore,
                                              String missingSkills) {
        StringBuilder suggestion = new StringBuilder();

        if (atsScore < 40) {
            suggestion.append("Your resume needs significant improvement. ");
        } else if (atsScore < 70) {
            suggestion.append("Your resume is average. Consider these improvements: ");
        } else {
            suggestion.append("Good resume! Fine-tune with these suggestions: ");
        }

        if (missingSkills != null && !missingSkills.isEmpty()) {
            suggestion.append("Add these missing skills: ")
                    .append(missingSkills)
                    .append(". ");
        }

        suggestion.append("Ensure your resume has clear sections for " +
                "Skills, Experience, and Education.");

        return suggestion.toString();
    }
}