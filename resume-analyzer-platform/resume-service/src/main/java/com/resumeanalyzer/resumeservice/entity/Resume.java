package com.resumeanalyzer.resumeservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private String status;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Resume() {
        super();
    }

    public Resume(Long id, String userEmail, String fileName, String extractedText, LocalDateTime uploadedAt, String status) {
        this.id = id;
        this.userEmail = userEmail;
        this.fileName = fileName;
        this.extractedText = extractedText;
        this.uploadedAt = uploadedAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}