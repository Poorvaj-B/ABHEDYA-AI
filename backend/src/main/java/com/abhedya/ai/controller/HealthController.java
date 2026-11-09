package com.abhedya.ai.controller;

import com.abhedya.ai.dto.ApiResponse;
import com.abhedya.ai.dto.HealthStatusDto;
import com.abhedya.ai.service.HabitationService;
import com.abhedya.ai.service.HazardZoneService;
import com.abhedya.ai.service.ShelterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @Value("${app.name:ABHEDYA AI Disaster Command Center}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    private final Environment environment;
    private final HabitationService habitationService;
    private final HazardZoneService hazardZoneService;
    private final ShelterService shelterService;

    public HealthController(Environment environment,
                            HabitationService habitationService,
                            HazardZoneService hazardZoneService,
                            ShelterService shelterService) {
        this.environment = environment;
        this.habitationService = habitationService;
        this.hazardZoneService = hazardZoneService;
        this.shelterService = shelterService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<HealthStatusDto>> getHealthStatus() {
        String[] activeProfiles = environment.getActiveProfiles();
        String activeProfile = activeProfiles.length > 0 ? String.join(",", activeProfiles) : "default";

        long habCount = habitationService.countAll();
        long hazardCount = hazardZoneService.countActiveZones();
        long shelterCount = shelterService.countAll();

        HealthStatusDto status = new HealthStatusDto(
                "OPERATIONAL",
                appName,
                appVersion,
                activeProfile,
                "CONNECTED",
                habCount,
                hazardCount,
                shelterCount
        );

        return ResponseEntity.ok(ApiResponse.ok("System Operational & Healthy", status));
    }
}

