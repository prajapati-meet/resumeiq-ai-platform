package com.resumeanalyzer.resumeservice.service;

import com.resumeanalyzer.resumeservice.entity.Resume;
import com.resumeanalyzer.resumeservice.entity.ResumeAnalysis;
import com.resumeanalyzer.resumeservice.repository.ResumeAnalysisRepository;
import com.resumeanalyzer.resumeservice.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository analysisRepository;
    private final AtsScoringService atsScoringService;

    public ResumeAnalysisService(ResumeRepository resumeRepository, ResumeAnalysisRepository analysisRepository, AtsScoringService atsScoringService) {
        this.resumeRepository = resumeRepository;
        this.analysisRepository = analysisRepository;
        this.atsScoringService = atsScoringService;
    }

    public void analyzeResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new RuntimeException("Resume not found with id: " + resumeId));

        resume.setStatus("PROCESSING");
        resumeRepository.save(resume);

        try {
            String resumeText = resume.getExtractedText();

            List<String> extractedSkills = atsScoringService.extractSkills(resumeText);

            List<String> requiredSkills = Arrays.asList(
                    "java", "spring boot", "mysql", "git", "rest api"
            );

            List<String> missingSkills = atsScoringService.findMissingSkills(extractedSkills, requiredSkills);

            String education = atsScoringService.extractEducation(resumeText);

            Integer atsScore = atsScoringService.calculateAtsScore(resumeText, extractedSkills, requiredSkills);

            String feedback = atsScoringService.generateFeedback(atsScore, missingSkills);

            ResumeAnalysis analysis = new ResumeAnalysis();
            analysis.setResume(resume);
            analysis.setAtsScore(atsScore);
            analysis.setExtractedSkills(String.join(",", extractedSkills));
            analysis.setMissingSkills(String.join(",", missingSkills));
            analysis.setEducationDetails(education);
            analysis.setFeedback(feedback);
            analysisRepository.save(analysis);

            resume.setStatus("COMPLETED");
            resumeRepository.save(resume);

            System.out.println("Analysis completed for ResumeId: "
                    + resumeId + " | ATS Score: " + atsScore);

        } catch (Exception e) {
            resume.setStatus("FAILED");
            resumeRepository.save(resume);
            System.err.println("Analysis failed for ResumeId: "
                    + resumeId + " | Error: " + e.getMessage());
        }
    }
}