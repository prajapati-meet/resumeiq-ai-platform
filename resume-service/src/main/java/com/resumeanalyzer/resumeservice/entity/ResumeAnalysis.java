package com.resumeanalyzer.resumeservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_analyses")
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false)
    private Integer atsScore;

    @Column(columnDefinition = "TEXT")
    private String extractedSkills;

    @Column(columnDefinition = "TEXT")
    private String missingSkills;

    @Column(columnDefinition = "TEXT")
    private String educationDetails;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;

    @PrePersist
    protected void onCreate() {
        this.analyzedAt = LocalDateTime.now();
    }

    public ResumeAnalysis() {
        super();
    }

    public ResumeAnalysis(Long id, Resume resume, Integer atsScore, String extractedSkills, String missingSkills, String educationDetails, String feedback, LocalDateTime analyzedAt) {
        this.id = id;
        this.resume = resume;
        this.atsScore = atsScore;
        this.extractedSkills = extractedSkills;
        this.missingSkills = missingSkills;
        this.educationDetails = educationDetails;
        this.feedback = feedback;
        this.analyzedAt = analyzedAt;
    }

    public Long getId() {
        return id;
    }

    public Resume getResume() {
        return resume;
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

    public String getEducationDetails() {
        return educationDetails;
    }

    public String getFeedback() {
        return feedback;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
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

    public void setEducationDetails(String educationDetails) {
        this.educationDetails = educationDetails;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}