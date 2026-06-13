package com.resumeanalyzer.aiservice.dto;

// Sent back to client with AI suggestions
public class AiSuggestionResponse {

    private Long resumeId;
    private Integer atsScore;
    private String aiSuggestion;
    private String status;
    private String message;

    public AiSuggestionResponse() {
        super();
    }

    public AiSuggestionResponse(Long resumeId, Integer atsScore, String aiSuggestion, String status, String message) {
        this.resumeId = resumeId;
        this.atsScore = atsScore;
        this.aiSuggestion = aiSuggestion;
        this.status = status;
        this.message = message;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public Integer getAtsScore() {
        return atsScore;
    }

    public String getAiSuggestion() {
        return aiSuggestion;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setAtsScore(Integer atsScore) {
        this.atsScore = atsScore;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public void setAiSuggestion(String aiSuggestion) {
        this.aiSuggestion = aiSuggestion;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}