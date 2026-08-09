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

    public Integer calculateAtsScore(String resumeText,
                                     List<String> extractedSkills,
                                     List<String> requiredSkills) {
        int score = 0;

        if (!requiredSkills.isEmpty()) {
            long matchedCount = extractedSkills.stream().filter(skill -> requiredSkills.stream().anyMatch(req -> req.toLowerCase().contains(skill.toLowerCase()))).count();
            score += (int) ((matchedCount * 60.0) / requiredSkills.size());
        } else {
            score += Math.min(extractedSkills.size() * 3, 60);
        }

        String lowerText = resumeText.toLowerCase();
        boolean hasEducation = EDUCATION_KEYWORDS.stream()
                .anyMatch(lowerText::contains);
        if (hasEducation) score += 20;

        int wordCount = resumeText.split("\\s+").length;
        if (wordCount > 300) score += 20;
        else if (wordCount > 150) score += 10;
        else score += 5;

        return Math.min(score, 100);
    }

    public String generateFeedback(Integer atsScore,
                                   List<String> missingSkills,
                                   String resumeText) {
        StringBuilder feedback = new StringBuilder();

        if (atsScore >= 80) {
            feedback.append("Excellent resume! Strong ATS compatibility. To further improve, consider the following:\n\n");
        } else if (atsScore >= 60) {
            feedback.append("Good resume with room for improvement.\n\n");
        } else if (atsScore >= 40) {
            feedback.append("Average resume. Consider adding more relevant skills.\n\n");
        } else {
            feedback.append("Resume needs significant improvement for ATS systems.\n\n");
        }

        if (!missingSkills.isEmpty()) {
            feedback.append("Consider adding these skills: ")
                    .append(String.join(", ", missingSkills))
                    .append(".\n\n");
        }

        feedback.append(extractExperience(resumeText));

        return feedback.toString();
    }

    private String extractExperience(String resumeText) {
        StringBuilder exp = new StringBuilder();
        String[] lines = resumeText.split("\n");
        boolean inExperience = false;
        
        for (String line : lines) {
            String lowerLine = line.toLowerCase().trim();
            if (lowerLine.equals("experience") || lowerLine.equals("work experience") 
                || lowerLine.equals("employment history") || lowerLine.equals("professional experience")
                || lowerLine.equals("employment") || lowerLine.equals("internships") 
                || lowerLine.equals("internship experience") || lowerLine.equals("internship")) {
                inExperience = true;
                continue;
            }
            if (inExperience) {
                if (lowerLine.equals("education") || lowerLine.equals("skills") 
                    || lowerLine.equals("projects") || lowerLine.equals("certifications") || lowerLine.equals("languages")) {
                    break;
                }
                if (line.trim().length() > 0) {
                    exp.append(line.trim()).append("\n");
                }
            }
        }
        
        if (exp.length() == 0) {
            return "No distinct Experience section found. Please ensure your resume has a clear 'Experience', 'Work Experience', or 'Internships' heading.";
        }
        
        StringBuilder formattedExp = new StringBuilder("Extracted Experience Summary:\n\n");
        String[] expLines = exp.toString().split("\n");
        for(String line : expLines) {
             String lower = line.toLowerCase();
             if (lower.contains("intern") || lower.contains("internship")) {
                 formattedExp.append("🎓 Internship Role: ").append(line.trim()).append("\n");
             } else if (lower.contains("engineer") || lower.contains("developer") || lower.contains("manager") || lower.contains("analyst") || line.matches(".*(20\\d{2}|19\\d{2}).*")) {
                 formattedExp.append("💼 Full-time / Role: ").append(line.trim()).append("\n");
             } else if (line.trim().startsWith("-") || line.trim().startsWith("•")) {
                 formattedExp.append("   - Summary: ").append(line.substring(1).trim()).append("\n");
             } else {
                 formattedExp.append(line.trim()).append("\n");
             }
        }
        
        return formattedExp.toString();
    }
}