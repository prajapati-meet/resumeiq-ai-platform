package com.resumeanalyzer.aiservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_suggestions")
public class AiSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long resumeId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private Integer atsScore;

    @Column(columnDefinition = "TEXT")
    private String extractedSkills;

    @Column(columnDefinition = "TEXT")
    private String missingSkills;

    @Column(columnDefinition = "LONGTEXT")
    private String aiSuggestion;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public AiSuggestion() {
        super();
    }

    public AiSuggestion(Long id, Long resumeId, String userEmail, Integer atsScore, String extractedSkills, String missingSkills, String aiSuggestion, String status, LocalDateTime createdAt) {
        this.id = id;
        this.resumeId = resumeId;
        this.userEmail = userEmail;
        this.atsScore = atsScore;
        this.extractedSkills = extractedSkills;
        this.missingSkills = missingSkills;
        this.aiSuggestion = aiSuggestion;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
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

    public String getAiSuggestion() {
        return aiSuggestion;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setAiSuggestion(String aiSuggestion) {
        this.aiSuggestion = aiSuggestion;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}