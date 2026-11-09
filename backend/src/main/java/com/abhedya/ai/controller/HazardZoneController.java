package com.abhedya.ai.controller;

import com.abhedya.ai.dto.ApiResponse;
import com.abhedya.ai.dto.CreateHazardZoneRequest;
import com.abhedya.ai.dto.GeoJsonFeatureCollectionDto;
import com.abhedya.ai.dto.HabitationAtRiskDto;
import com.abhedya.ai.dto.HazardZoneDetailDto;
import com.abhedya.ai.service.HazardZoneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hazards")
public class HazardZoneController {

    private final HazardZoneService hazardZoneService;

    public HazardZoneController(HazardZoneService hazardZoneService) {
        this.hazardZoneService = hazardZoneService;
    }

    /**
     * Retrieves active red zones with calculated spatial metrics and impacted demographics.
     * GET /api/v1/hazards/red-zones
     */
    @GetMapping("/red-zones")
    public ResponseEntity<ApiResponse<List<HazardZoneDetailDto>>> getRedZones(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "ALL") String type,
            @RequestParam(required = false, defaultValue = "true") Boolean activeOnly) {
        List<HazardZoneDetailDto> redZones = hazardZoneService.getRedZoneDetails(state, type, activeOnly);
        return ResponseEntity.ok(ApiResponse.ok("Retrieved " + redZones.size() + " hazard red zones", redZones));
    }

    /**
     * Standard RFC 7946 GeoJSON FeatureCollection for Leaflet map overlay.
     * GET /api/v1/hazards/geojson
     */
    @GetMapping(value = "/geojson", produces = "application/geo+json")
    public ResponseEntity<GeoJsonFeatureCollectionDto> getHazardGeoJson(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "ALL") String type) {
        GeoJsonFeatureCollectionDto featureCollection = hazardZoneService.getGeoJson(state, type);
        return ResponseEntity.ok(featureCollection);
    }

    /**
     * Fallback JSON representation of GeoJSON.
     */
    @GetMapping(value = "/geojson", produces = "application/json")
    public ResponseEntity<GeoJsonFeatureCollectionDto> getHazardGeoJsonAsJson(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "ALL") String type) {
        return ResponseEntity.ok(hazardZoneService.getGeoJson(state, type));
    }

    /**
     * Retrieves a single hazard zone by ID.
     * GET /api/v1/hazards/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HazardZoneDetailDto>> getHazardZoneById(@PathVariable String id) {
        return hazardZoneService.getZoneDetailById(id)
                .map(zone -> ResponseEntity.ok(ApiResponse.ok("Hazard zone found", zone)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Hazard zone not found with ID: " + id)));
    }

    /**
     * Retrieves all habitations affected by a specific hazard zone.
     * GET /api/v1/hazards/{id}/affected-habitations
     */
    @GetMapping("/{id}/affected-habitations")
    public ResponseEntity<ApiResponse<List<HabitationAtRiskDto>>> getAffectedHabitations(@PathVariable String id) {
        try {
            List<HabitationAtRiskDto> affected = hazardZoneService.getAffectedHabitationsForZone(id);
            return ResponseEntity.ok(ApiResponse.ok("Retrieved " + affected.size() + " affected habitations for hazard zone: " + id, affected));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Creates a new hazard zone with verified GeoJSON boundary validation and spatial metric calculation.
     * POST /api/v1/hazards
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HazardZoneDetailDto>> createHazardZone(
            @Valid @RequestBody CreateHazardZoneRequest request) {
        try {
            HazardZoneDetailDto created = hazardZoneService.createHazardZone(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Hazard zone created successfully with calculated spatial metrics", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
