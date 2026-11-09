package com.abhedya.ai.controller;

import com.abhedya.ai.dto.ApiResponse;
import com.abhedya.ai.model.IncidentLog;
import com.abhedya.ai.service.IncidentLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentLogController {

    private final IncidentLogService incidentLogService;

    public IncidentLogController(IncidentLogService incidentLogService) {
        this.incidentLogService = incidentLogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<IncidentLog>>> getRecentIncidents() {
        List<IncidentLog> logs = incidentLogService.getRecentLogs();
        return ResponseEntity.ok(ApiResponse.ok("Retrieved " + logs.size() + " recent incident logs", logs));
    }
}

