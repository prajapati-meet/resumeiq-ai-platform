package com.resumeanalyzer.resumeservice.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AtsScoringService {

    private static final List<String> TECH_SKILLS = Arrays.asList(
            "java", "python", "javascript", "typescript", "c++", "c#", "golang", "rust",
            "spring", "spring boot", "react", "angular", "vue", "node.js", "django", "flask",
            "mysql", "postgresql", "mongodb", "redis", "elasticsearch",
            "aws", "azure", "gcp", "docker", "kubernetes", "jenkins", "git",
            "microservices", "rest api", "graphql", "kafka", "rabbitmq",
            "hibernate", "jpa", "maven", "gradle", "junit", "agile", "scrum"
    );

    private static final List<String> EDUCATION_KEYWORDS = Arrays.asList(
            "bachelor", "master", "phd", "b.tech", "m.tech", "b.e", "m.e",
            "computer science", "information technology", "engineering",
            "university", "college", "institute", "degree"
    );

    public List<String> extractSkills(String resumeText) {
        List<String> foundSkills = new ArrayList<>();
        String lowerText = resumeText.toLowerCase();

        for (String skill : TECH_SKILLS) {
            if (lowerText.contains(skill.toLowerCase())) {
                foundSkills.add(skill);
            }
        }
        return foundSkills;
    }

    // ─────────────────────────────────────────────────────
    // FIND MISSING SKILLS
    // Compares resume skills against a job description
    // ─────────────────────────────────────────────────────
    public List<String> findMissingSkills(List<String> extractedSkills,
                                          List<String> requiredSkills) {
        List<String> missingSkills = new ArrayList<>();

        for (String required : requiredSkills) {
            boolean found = false;
            for (String extracted : extractedSkills) {
                if (extracted.toLowerCase().contains(required.toLowerCase())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                missingSkills.add(required);
            }
        }
        return missingSkills;
    }

    // ─────────────────────────────────────────────────────
    // EXTRACT EDUCATION
    // Looks for education section keywords
    // ─────────────────────────────────────────────────────
    public String extractEducation(String resumeText) {
        StringBuilder education = new StringBuilder();
        String lowerText = resumeText.toLowerCase();
        String[] lines = resumeText.split("\n");

        for (String line : lines) {
            String lowerLine = line.toLowerCase();
            for (String keyword : EDUCATION_KEYWORDS) {
                if (lowerLine.contains(keyword)) {
                    education.append(line.trim()).append(" | ");
                    break;
                }
            }
        }

        return education.length() > 0
                ? education.toString()
                : "No education details found";
    }

    // ─────────────────────────────────────────────────────
    // CALCULATE ATS SCORE
    // Score is based on:
    // - Skills match (60%)
    // - Education presence (20%)
    // - Resume length/completeness (20%)
    // ─────────────────────────────────────────────────────
    public Integer calculateAtsScore(String resumeText,
                                     List<String> extractedSkills,
                                     List<String> requiredSkills) {
        int score = 0;

        // Skills score — 60 points max
        if (!requiredSkills.isEmpty()) {
            long matchedCount = extractedSkills.stream()
                    .filter(skill -> requiredSkills.stream()
                            .anyMatch(req -> req.toLowerCase()
                                    .contains(skill.toLowerCase())))
                    .count();
            score += (int) ((matchedCount * 60.0) / requiredSkills.size());
        } else {
            // No job description provided — score based on total skills found
            score += Math.min(extractedSkills.size() * 3, 60);
        }

        // Education score — 20 points
        String lowerText = resumeText.toLowerCase();
        boolean hasEducation = EDUCATION_KEYWORDS.stream()
                .anyMatch(lowerText::contains);
        if (hasEducation) score += 20;

        // Completeness score — 20 points
        // Based on resume length (longer = more detailed = better)
        int wordCount = resumeText.split("\\s+").length;
        if (wordCount > 300) score += 20;
        else if (wordCount > 150) score += 10;
        else score += 5;

        // Cap at 100
        return Math.min(score, 100);
    }

    // ─────────────────────────────────────────────────────
    // GENERATE FEEDBACK
    // Human readable feedback based on score
    // ─────────────────────────────────────────────────────
    public String generateFeedback(Integer atsScore,
                                   List<String> missingSkills) {
        StringBuilder feedback = new StringBuilder();

        if (atsScore >= 80) {
            feedback.append("Excellent resume! Strong ATS compatibility. ");
        } else if (atsScore >= 60) {
            feedback.append("Good resume with room for improvement. ");
        } else if (atsScore >= 40) {
            feedback.append("Average resume. Consider adding more relevant skills. ");
        } else {
            feedback.append("Resume needs significant improvement for ATS systems. ");
        }

        if (!missingSkills.isEmpty()) {
            feedback.append("Consider adding these skills: ")
                    .append(String.join(", ", missingSkills))
                    .append(".");
        }

        return feedback.toString();
    }
}