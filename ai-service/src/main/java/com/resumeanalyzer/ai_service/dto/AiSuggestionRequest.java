package com.resumeanalyzer.aiservice.dto;

public class AiSuggestionRequest {

    private Long resumeId;
    private String userEmail;
    private Integer atsScore;
    private String extractedSkills;
    private String missingSkills;
    private String extractedText;

    public AiSuggestionRequest() {
        super();
    }

    public AiSuggestionRequest(Long resumeId, String userEmail, Integer atsScore, String extractedSkills, String missingSkills, String extractedText) {
        this.resumeId = resumeId;
        this.userEmail = userEmail;
        this.atsScore = atsScore;
        this.extractedSkills = extractedSkills;
        this.missingSkills = missingSkills;
        this.extractedText = extractedText;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Integer getAtsScore() {
        return atsScore;
    }

    public String getExtractedSkills() {
        return extractedSkills;
    }

    public String getMissingSkills() {
        return missingSkills;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setAtsScore(Integer atsScore) {
        this.atsScore = atsScore;
    }

    public void setExtractedSkills(String extractedSkills) {
        this.extractedSkills = extractedSkills;
    }

    public void setMissingSkills(String missingSkills) {
        this.missingSkills = missingSkills;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }
}