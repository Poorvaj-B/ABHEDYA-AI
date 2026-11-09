package com.abhedya.ai.repository;

import com.abhedya.ai.model.IncidentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentLogRepository extends JpaRepository<IncidentLog, String> {

    List<IncidentLog> findTop20ByOrderByLoggedAtDesc();

    List<IncidentLog> findBySeverityIgnoreCase(String severity);
}

