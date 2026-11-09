package com.abhedya.ai.service;

import com.abhedya.ai.model.IncidentLog;
import com.abhedya.ai.repository.IncidentLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class IncidentLogService {

    private final IncidentLogRepository incidentLogRepository;

    public IncidentLogService(IncidentLogRepository incidentLogRepository) {
        this.incidentLogRepository = incidentLogRepository;
    }

    public List<IncidentLog> getRecentLogs() {
        return incidentLogRepository.findTop20ByOrderByLoggedAtDesc();
    }

    @Transactional
    public IncidentLog logEvent(String incidentType, String severity, String title, String description,
                                String state, String district, String source) {
        IncidentLog log = new IncidentLog(
                java.util.UUID.randomUUID().toString(),
                incidentType,
                severity,
                title,
                description,
                state,
                district,
                source
        );
        return incidentLogRepository.save(log);
    }
}

