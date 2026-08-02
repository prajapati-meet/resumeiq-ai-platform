package com.resumeanalyzer.aiservice.dto;

public class AiSuggestionRequest {

    private Long resumeId;
    private String userEmail;
    private Integer atsScore;
    private String extractedSkills;
    private String missingSkills;
    private String extractedText;
    private String targetPosition;
    private String jobDescription;

    public AiSuggestionRequest() {
        super();
    }

    public AiSuggestionRequest(Long resumeId, String userEmail, Integer atsScore, String extractedSkills, String missingSkills, String extractedText, String targetPosition, String jobDescription) {
        this.resumeId = resumeId;
        this.userEmail = userEmail;
        this.atsScore = atsScore;
        this.extractedSkills = extractedSkills;
        this.missingSkills = missingSkills;
        this.extractedText = extractedText;
        this.targetPosition = targetPosition;
        this.jobDescription = jobDescription;
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

    public String getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(String targetPosition) {
        this.targetPosition = targetPosition;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}