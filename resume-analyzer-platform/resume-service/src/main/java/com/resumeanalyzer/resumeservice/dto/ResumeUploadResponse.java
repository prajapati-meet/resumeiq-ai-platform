package com.resumeanalyzer.resumeservice.dto;

public class ResumeUploadResponse {

    private Long resumeId;
    private String fileName;
    private String status;
    private String message;

    public ResumeUploadResponse() {
        super();
    }

    public ResumeUploadResponse(Long resumeId, String fileName, String status, String message) {
        this.resumeId = resumeId;
        this.fileName = fileName;
        this.status = status;
        this.message = message;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}