package com.resumeanalyzer.resumeservice.service;

import com.resumeanalyzer.resumeservice.dto.AnalysisResponse;
import com.resumeanalyzer.resumeservice.dto.ResumeUploadResponse;
import com.resumeanalyzer.resumeservice.entity.Resume;
import com.resumeanalyzer.resumeservice.entity.ResumeAnalysis;
import com.resumeanalyzer.resumeservice.kafka.ResumeEventProducer;
import com.resumeanalyzer.resumeservice.repository.ResumeAnalysisRepository;
import com.resumeanalyzer.resumeservice.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository analysisRepository;
    private final PdfParsingService pdfParsingService;
    private final ResumeEventProducer eventProducer;

    public ResumeService(ResumeRepository resumeRepository, ResumeAnalysisRepository analysisRepository, PdfParsingService pdfParsingService, ResumeEventProducer eventProducer) {
        this.resumeRepository = resumeRepository;
        this.analysisRepository = analysisRepository;
        this.pdfParsingService = pdfParsingService;
        this.eventProducer = eventProducer;
    }

    public ResumeUploadResponse uploadResume(MultipartFile file, String userEmail, String targetPosition, String jobDescription) throws Exception {

        if (file.isEmpty()) {
            throw new RuntimeException("Please upload a file");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("Only PDF files are accepted");
        }

        String extractedText = pdfParsingService.extractTextFromPdf(file);
        if (extractedText == null || extractedText.trim().isEmpty()) {
            throw new RuntimeException(
                    "Could not extract text from PDF. " +
                            "Please ensure the PDF is not scanned/image-only.");
        }

        Resume resume = new Resume();
        resume.setUserEmail(userEmail);
        resume.setFileName(originalFilename);
        resume.setExtractedText(extractedText);
        resume.setTargetPosition(targetPosition);
        resume.setJobDescription(jobDescription);
        Resume savedResume = resumeRepository.save(resume);

        eventProducer.sendResumeUploadedEvent(savedResume.getId());

        return new ResumeUploadResponse(
                savedResume.getId(),
                savedResume.getFileName(),
                savedResume.getStatus(),
                "Resume uploaded successfully. Analysis in progress."
        );
    }

    public AnalysisResponse getAnalysis(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new RuntimeException("Resume not found: " + resumeId));

        ResumeAnalysis analysis = analysisRepository
                .findByResumeId(resumeId)
                .orElseThrow(() -> new RuntimeException("Analysis not ready yet for resumeId: " + resumeId));

        List<String> extractedSkills = (analysis.getExtractedSkills() != null && !analysis.getExtractedSkills().isEmpty())
                ? Arrays.asList(analysis.getExtractedSkills().split(",")) : List.of();

        List<String> missingSkills = (analysis.getMissingSkills() != null && !analysis.getMissingSkills().isEmpty())
                ? Arrays.asList(analysis.getMissingSkills().split(",")) : List.of();

        return new AnalysisResponse(
                resume.getId(),
                resume.getFileName(),
                analysis.getAtsScore(),
                extractedSkills,
                missingSkills,
                analysis.getEducationDetails(),
                analysis.getFeedback(),
                resume.getStatus()
        );
    }
}