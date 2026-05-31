package com.resumeanalyzer.resumeservice.service;

import com.resumeanalyzer.resumeservice.entity.Resume;
import com.resumeanalyzer.resumeservice.entity.ResumeAnalysis;
import com.resumeanalyzer.resumeservice.repository.ResumeAnalysisRepository;
import com.resumeanalyzer.resumeservice.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

// Orchestrates the full analysis workflow:
// 1. Load resume from DB
// 2. Extract skills using AtsScoringService
// 3. Calculate ATS score
// 4. Save results to ResumeAnalysis table
// 5. Update resume status to COMPLETED
@Service
public class ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository analysisRepository;
    private final AtsScoringService atsScoringService;

    public ResumeAnalysisService(ResumeRepository resumeRepository,
                                 ResumeAnalysisRepository analysisRepository,
                                 AtsScoringService atsScoringService) {
        this.resumeRepository = resumeRepository;
        this.analysisRepository = analysisRepository;
        this.atsScoringService = atsScoringService;
    }

    // Called by Kafka consumer when resume-uploaded event arrives
    public void analyzeResume(Long resumeId) {
        // Step 1: Load resume from DB
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException(
                        "Resume not found with id: " + resumeId));

        // Update status to PROCESSING
        resume.setStatus("PROCESSING");
        resumeRepository.save(resume);

        try {
            String resumeText = resume.getExtractedText();

            // Step 2: Extract skills from text
            List<String> extractedSkills =
                    atsScoringService.extractSkills(resumeText);

            // Step 3: Default required skills for scoring
            // (In future this will come from job description)
            List<String> requiredSkills = Arrays.asList(
                    "java", "spring boot", "mysql", "git", "rest api"
            );

            // Step 4: Find missing skills
            List<String> missingSkills =
                    atsScoringService.findMissingSkills(
                            extractedSkills, requiredSkills);

            // Step 5: Extract education details
            String education =
                    atsScoringService.extractEducation(resumeText);

            // Step 6: Calculate ATS score
            Integer atsScore = atsScoringService.calculateAtsScore(
                    resumeText, extractedSkills, requiredSkills);

            // Step 7: Generate feedback
            String feedback = atsScoringService.generateFeedback(
                    atsScore, missingSkills);

            // Step 8: Save analysis results
            ResumeAnalysis analysis = new ResumeAnalysis();
            analysis.setResume(resume);
            analysis.setAtsScore(atsScore);
            analysis.setExtractedSkills(String.join(",", extractedSkills));
            analysis.setMissingSkills(String.join(",", missingSkills));
            analysis.setEducationDetails(education);
            analysis.setFeedback(feedback);
            analysisRepository.save(analysis);

            // Step 9: Update resume status to COMPLETED
            resume.setStatus("COMPLETED");
            resumeRepository.save(resume);

            System.out.println("Analysis completed for ResumeId: "
                    + resumeId + " | ATS Score: " + atsScore);

        } catch (Exception e) {
            // If analysis fails mark as FAILED
            resume.setStatus("FAILED");
            resumeRepository.save(resume);
            System.err.println("Analysis failed for ResumeId: "
                    + resumeId + " | Error: " + e.getMessage());
        }
    }
}