package com.resumeanalyzer.resumeservice.dto;

import java.util.List;

public class AnalysisResponse {

    private Long resumeId;
    private String fileName;
    private Integer atsScore;
    private List<String> extractedSkills;
    private List<String> missingSkills;
    private String educationDetails;
    private String feedback;
    private String status;

    public AnalysisResponse() {
        super();
    }

    public AnalysisResponse(Long resumeId, String fileName, Integer atsScore, List<String> extractedSkills, List<String> missingSkills, String educationDetails, String feedback, String status) {
        this.resumeId = resumeId;
        this.fileName = fileName;
        this.atsScore = atsScore;
        this.extractedSkills = extractedSkills;
        this.missingSkills = missingSkills;
        this.educationDetails = educationDetails;
        this.feedback = feedback;
        this.status = status;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getAtsScore() {
        return atsScore;
    }

    public List<String> getExtractedSkills() {
        return extractedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public String getEducationDetails() {
        return educationDetails;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getStatus() {
        return status;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setAtsScore(Integer atsScore) {
        this.atsScore = atsScore;
    }

    public void setExtractedSkills(List<String> extractedSkills) {
        this.extractedSkills = extractedSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public void setEducationDetails(String educationDetails) {
        this.educationDetails = educationDetails;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}