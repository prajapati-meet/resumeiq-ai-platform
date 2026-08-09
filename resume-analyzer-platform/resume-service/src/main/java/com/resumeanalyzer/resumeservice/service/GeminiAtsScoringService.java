package com.resumeanalyzer.resumeservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiAtsScoringService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GeminiAtsScoringService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public static class GeminiScoringResult {
        public List<String> extractedKeywords = new ArrayList<>();
        public List<String> missingKeywords = new ArrayList<>();
        public int finalAtsScore;
        public int semanticScore;
        public int experienceMatchScore;
    }

    public GeminiScoringResult scoreResume(String resumeText, String targetPosition, String jobDescription) {
        if (resumeText == null) resumeText = "";
        String resumeContent = resumeText.length() > 5000 ? resumeText.substring(0, 5000) : resumeText;

        String prompt = String.format("""
                You are an enterprise ATS (Applicant Tracking System) scoring algorithm.
                Your task is to analyze the Candidate's Resume against the provided Job Description.

                Target Position: %s
                Job Description:
                %s

                Candidate Resume:
                %s

                Perform the following 4 analyses:
                1. Dynamic JD Keyword Extraction: Extract the most important technical and soft skills strictly required by the Job Description. Do not just use a standard list, use the actual JD.
                2. Weighted Keyword Scoring: Evaluate if the resume contains these keywords.
                3. Semantic Similarity: Calculate a 0-100 score indicating how well the candidate's actual accomplishments and tone fit the job description, ignoring exact keywords (e.g. "Frontend Developer" vs "UI Engineer").
                4. Experience & Duration Checking: Mathematically compare the required years of experience in the JD against the calculated years of experience from the resume dates. Output a 0-100 score for experience match.

                Output ONLY valid JSON matching this exact structure, with no markdown formatting or backticks:
                {
                  "extractedKeywords": ["keyword1", "keyword2"],
                  "missingKeywords": ["missing1", "missing2"],
                  "semanticScore": 85,
                  "experienceMatchScore": 90,
                  "finalAtsScore": 88
                }
                """, 
                targetPosition != null ? targetPosition : "N/A", 
                jobDescription != null ? jobDescription : "N/A", 
                resumeContent);

        try {
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

            return parseJsonResult(response);
        } catch (Exception e) {
            System.err.println("Gemini ATS Scoring error: " + e.getMessage());
            return fallbackScore();
        }
    }

    private GeminiScoringResult parseJsonResult(String response) {
        GeminiScoringResult result = new GeminiScoringResult();
        try {
            JsonNode root = objectMapper.readTree(response);
            String textResponse = root.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText();
            
            // Clean up any markdown blocks if the LLM didn't follow instructions perfectly
            textResponse = textResponse.replace("```json", "").replace("```", "").trim();
            
            JsonNode jsonResult = objectMapper.readTree(textResponse);
            
            if (jsonResult.has("extractedKeywords")) {
                jsonResult.get("extractedKeywords").forEach(k -> result.extractedKeywords.add(k.asText()));
            }
            if (jsonResult.has("missingKeywords")) {
                jsonResult.get("missingKeywords").forEach(k -> result.missingKeywords.add(k.asText()));
            }
            result.semanticScore = jsonResult.path("semanticScore").asInt(50);
            result.experienceMatchScore = jsonResult.path("experienceMatchScore").asInt(50);
            result.finalAtsScore = jsonResult.path("finalAtsScore").asInt(50);
            
            return result;
        } catch (Exception e) {
            System.err.println("Failed to parse Gemini ATS score JSON: " + e.getMessage());
            return fallbackScore();
        }
    }

    private GeminiScoringResult fallbackScore() {
        GeminiScoringResult result = new GeminiScoringResult();
        result.finalAtsScore = 40;
        result.semanticScore = 40;
        result.experienceMatchScore = 40;
        result.missingKeywords.add("Gemini API Error");
        return result;
    }
}
