package com.abhedya.ai.repository;

import com.abhedya.ai.model.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, String> {

    List<RiskAssessment> findByClassificationIgnoreCase(String classification);

    List<RiskAssessment> findByRelocationRecommendedTrue();

    Optional<RiskAssessment> findTopByHabitationIdOrderByAssessedAtDesc(String habitationId);
}

