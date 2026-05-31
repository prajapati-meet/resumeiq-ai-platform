package com.resumeanalyzer.resumeservice.controller;

import com.resumeanalyzer.resumeservice.dto.AnalysisResponse;
import com.resumeanalyzer.resumeservice.dto.ResumeUploadResponse;
import com.resumeanalyzer.resumeservice.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // POST /api/resume/upload
    // Accepts multipart form data with:
    //   - file: the PDF file
    //   - userEmail: who is uploading
    @PostMapping("/upload")
    public ResponseEntity<ResumeUploadResponse> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userEmail") String userEmail) throws Exception {

        ResumeUploadResponse response =
                resumeService.uploadResume(file, userEmail);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET /api/resume/analysis/{resumeId}
    // Returns full analysis results for a resume
    @GetMapping("/analysis/{resumeId}")
    public ResponseEntity<AnalysisResponse> getAnalysis(
            @PathVariable Long resumeId) {
        return ResponseEntity.ok(resumeService.getAnalysis(resumeId));
    }

    // GET /api/resume/health
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Resume Service is running!");
    }
}