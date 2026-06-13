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

    @PostMapping("/upload")
    public ResponseEntity<ResumeUploadResponse> uploadResume(@RequestParam("file") MultipartFile file, @RequestParam("userEmail") String userEmail) throws Exception {

        ResumeUploadResponse response = resumeService.uploadResume(file, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/analysis/{resumeId}")
    public ResponseEntity<AnalysisResponse> getAnalysis(@PathVariable Long resumeId) {
        return ResponseEntity.ok(resumeService.getAnalysis(resumeId));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Resume Service is running!");
    }
}