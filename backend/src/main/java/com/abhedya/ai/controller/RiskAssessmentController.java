package com.abhedya.ai.controller;

import com.abhedya.ai.dto.ApiResponse;
import com.abhedya.ai.dto.RiskAssessmentRequest;
import com.abhedya.ai.dto.RiskAssessmentResponseDto;
import com.abhedya.ai.service.RiskAssessmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analysis")
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    public RiskAssessmentController(RiskAssessmentService riskAssessmentService) {
        this.riskAssessmentService = riskAssessmentService;
    }

    /**
     * Executes explainable multi-factor risk assessment for a habitation.
     * POST /api/v1/analysis/risk-assessment
     */
    @PostMapping("/risk-assessment")
    public ResponseEntity<ApiResponse<RiskAssessmentResponseDto>> calculateRisk(
            @Valid @RequestBody RiskAssessmentRequest request) {
        try {
            RiskAssessmentResponseDto result = riskAssessmentService.assessRisk(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Multi-factor risk assessment completed successfully", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Executes risk assessment automatically factoring in live Open-Meteo weather telemetry.
     * POST /api/v1/analysis/risk-assessment/live-weather/{habitationId}
     */
    @PostMapping("/risk-assessment/live-weather/{habitationId}")
    public ResponseEntity<ApiResponse<RiskAssessmentResponseDto>> calculateRiskWithLiveWeather(
            @PathVariable String habitationId,
            @RequestBody(required = false) RiskAssessmentRequest request) {
        try {
            RiskAssessmentResponseDto result = riskAssessmentService.assessRiskWithLiveWeather(habitationId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Risk assessment with live weather completed successfully", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Retrieves historical risk assessment records.
     * GET /api/v1/analysis/assessments
     */
    @GetMapping("/assessments")
    public ResponseEntity<ApiResponse<List<RiskAssessmentResponseDto>>> getAssessments(
            @RequestParam(required = false, defaultValue = "ALL") String classification,
            @RequestParam(required = false, defaultValue = "false") Boolean relocationOnly) {
        List<RiskAssessmentResponseDto> assessments = riskAssessmentService.getAllAssessments(classification, relocationOnly);
        return ResponseEntity.ok(ApiResponse.ok("Retrieved " + assessments.size() + " risk assessment records", assessments));
    }

    /**
     * Retrieves the latest assessment for a habitation.
     * GET /api/v1/analysis/assessments/latest/{habitationId}
     */
    @GetMapping("/assessments/latest/{habitationId}")
    public ResponseEntity<ApiResponse<RiskAssessmentResponseDto>> getLatestAssessment(
            @PathVariable String habitationId) {
        return riskAssessmentService.getLatestAssessmentForHabitation(habitationId)
                .map(a -> ResponseEntity.ok(ApiResponse.ok("Latest assessment retrieved", a)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("No assessment found for habitation ID: " + habitationId)));
    }
}
