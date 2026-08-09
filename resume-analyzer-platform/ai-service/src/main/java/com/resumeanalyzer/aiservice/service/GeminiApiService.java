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

    private final String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent";

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
                                     String resumeText,
                                     String targetPosition,
                                     String jobDescription) {
        try {
            String prompt = buildPrompt(atsScore, extractedSkills,
                    missingSkills, resumeText, targetPosition, jobDescription);

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

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            System.err.println("Gemini API error: " + e.getMessage() + " - " + errorBody);
            return "Google API Error: " + e.getMessage() + " \nDetails: " + errorBody;
        } catch (Exception e) {
            System.err.println("Gemini API error: " + e.getMessage());
            return generateFallbackSuggestion(atsScore, missingSkills);
        }
    }

    private String buildPrompt(Integer atsScore,
                               String extractedSkills,
                               String missingSkills,
                               String resumeText,
                               String targetPosition,
                               String jobDescription) {
        
        // Increase the resume context for better analysis
        String resumeContent = resumeText.length() > 5000 
                ? resumeText.substring(0, 5000) 
                : resumeText;

        return String.format("""
                Resume vs. Job Description — Brutally Honest Evaluation
                
                You are an experienced technical recruiter and hiring manager. I will provide you with:
                
                1. Job Description (JD) (Target Position: %s)
                2. My Resume
                
                Your job is to evaluate my resume against the JD honestly and critically.
                
                Important instructions
                
                * CRITICAL FORMATTING INSTRUCTION: DO NOT use any Markdown formatting! Do not use asterisks (**), hashes (#), or markdown tables (|). Use completely plain text formatting only, with standard line breaks, numbers (1., 2.), and standard dashes (-) for lists. Keep it highly readable as plain text.
                * Do not flatter me.
                * Do not tell me my resume is "good," "strong," or "impressive" unless the evidence clearly supports it.
                * Do not try to make me feel better.
                * Be direct, realistic, and specific.
                * Assume the goal is to maximize my chances of getting shortlisted, not to protect my feelings.
                * Do not give me generic resume advice.
                * Base your evaluation only on the information present in the JD and resume.
                * Do not assume I have experience that is not explicitly supported by my resume.
                * Distinguish between:
                
                  - skills I clearly demonstrate,
                  - skills I partially demonstrate,
                  - skills I claim but don't substantiate,
                  - skills completely missing from my resume.
                * If something is weak, say that it is weak and explain why.
                * If I am not a strong match, tell me clearly.
                * If there are major gaps that are difficult to fix without actually gaining experience, say so.
                
                1. Overall Match
                
                Give me:
                
                * Overall match score: X/100 (Current ATS Match Score: %d/100)
                * Estimated shortlist likelihood: Low / Medium / High
                * Top 3 reasons I match
                * Top 3 reasons I may be rejected
                
                Be realistic rather than optimistic.
                
                2. JD vs Resume Gap Analysis
                
                Create a plain text list (NOT A TABLE):
                For every important requirement in the JD, list the Requirement, Evidence, Match level, Gap, and How to Improve.
                
                For every important requirement in the JD, classify the match as:
                
                * Strong
                * Partial
                * Weak
                * Missing
                
                Do not skip important requirements.
                
                3. Resume Problems
                
                Identify the biggest problems in my resume that could hurt my chances.
                
                Look specifically for:
                
                * Missing required skills (Missing skills identified by ATS: %s)
                * Weak or vague bullet points
                * Lack of measurable impact
                * Responsibilities instead of achievements
                * Irrelevant information
                * Poor prioritization
                * Missing keywords from the JD
                * Skills listed without evidence
                * Experience that doesn't clearly demonstrate the required competency
                * Seniority mismatch
                * Career progression issues
                * Projects that don't strengthen my candidacy
                * Anything that may confuse or concern a recruiter
                
                Rank the problems from most damaging to least damaging.
                
                4. ATS / Keyword Analysis
                
                Identify:
                
                * Important JD keywords present in my resume (ATS found these skills: %s)
                * Important JD keywords missing from my resume
                * Keywords that appear in my resume but are not supported by experience
                * Important concepts that I demonstrate but don't describe using the terminology used in the JD
                
                Do not recommend keyword stuffing. Only recommend adding a keyword when my actual experience supports it.
                
                5. Bullet-by-Bullet Critique
                
                Review each relevant resume bullet and tell me:
                
                * What is weak about it
                * What is missing
                * Whether it demonstrates impact
                * Whether it aligns with the JD
                * How I should rewrite it
                
                When rewriting, do not invent metrics, technologies, responsibilities, or achievements.
                
                If information is missing, use [ADD METRIC], [ADD TECHNOLOGY], etc., rather than making something up.
                
                6. What I Should Change
                
                Give me a prioritized action plan:
                
                Must Fix
                
                Changes that are likely to materially affect my chances.
                
                Should Fix
                
                Changes that would meaningfully improve the resume.
                
                Nice to Have
                
                Changes that are useful but unlikely to determine the outcome.
                
                Also tell me what NOT to change, so I don't waste time optimizing irrelevant things.
                
                7. Final Verdict
                
                Answer these questions directly:
                
                1. If you were the recruiter, would you shortlist me? Why or why not?
                2. What is the single biggest weakness in my application?
                3. What is the single strongest selling point?
                4. What experience or skill am I missing that matters most for this role?
                5. What are the 3 highest-impact changes I can make to my resume?
                6. If I made those changes, how much could my match realistically improve?
                
                Finally, give me a concise "Do this next" checklist with the exact actions I should take.
                
                JD
                
                %s
                
                RESUME
                
                %s
                """,
                targetPosition != null && !targetPosition.isBlank() ? targetPosition : "Not specified",
                atsScore,
                missingSkills,
                extractedSkills,
                jobDescription != null && !jobDescription.isBlank() ? jobDescription : "Not specified",
                resumeContent);
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
        } else if (atsScore < 80) {
            suggestion.append("Good resume! Fine-tune with these suggestions: ");
        } else {
            suggestion.append("Excellent resume with an 80%+ match! To achieve a perfect match, consider these improvements: ");
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