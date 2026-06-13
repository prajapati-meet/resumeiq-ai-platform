package com.resumeanalyzer.aiservice.repository;

import com.resumeanalyzer.aiservice.entity.AiSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AiSuggestionRepository extends JpaRepository<AiSuggestion, Long> {

    Optional<AiSuggestion> findByResumeId(Long resumeId);
    List<AiSuggestion> findByUserEmail(String userEmail);
}