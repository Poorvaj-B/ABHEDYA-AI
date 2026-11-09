package com.abhedya.ai.controller;

import com.abhedya.ai.dto.*;
import com.abhedya.ai.service.RelocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/relocation")
public class RelocationController {

    private final RelocationService relocationService;

    public RelocationController(RelocationService relocationService) {
        this.relocationService = relocationService;
    }

    /**
     * Creates an intelligent relocation plan with OSRM evacuation routing and fleet dispatch.
     * POST /api/v1/relocation/plans
     */
    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<RelocationPlanDto>> createPlan(
            @Valid @RequestBody CreateRelocationPlanRequest request) {
        try {
            RelocationPlanDto plan = relocationService.createRelocationPlan(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Relocation plan generated and dispatched successfully", plan));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to generate relocation plan: " + e.getMessage()));
        }
    }

    /**
     * Retrieves all relocation plans.
     * GET /api/v1/relocation/plans
     */
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<RelocationPlanDto>>> getAllPlans() {
        List<RelocationPlanDto> plans = relocationService.getAllRelocationPlans();
        return ResponseEntity.ok(ApiResponse.ok("Retrieved " + plans.size() + " relocation plans", plans));
    }

    /**
     * Retrieves a relocation plan by ID.
     * GET /api/v1/relocation/plans/{id}
     */
    @GetMapping("/plans/{id}")
    public ResponseEntity<ApiResponse<RelocationPlanDto>> getPlanById(@PathVariable String id) {
        return relocationService.getRelocationPlanById(id)
                .map(p -> ResponseEntity.ok(ApiResponse.ok("Relocation plan found", p)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Relocation plan not found with ID: " + id)));
    }

    /**
     * Updates an evacuation plan status (e.g. EN_ROUTE, COMPLETED, CANCELLED).
     * PATCH /api/v1/relocation/plans/{id}/status?status=COMPLETED
     */
    @PatchMapping("/plans/{id}/status")
    public ResponseEntity<ApiResponse<RelocationPlanDto>> updatePlanStatus(
            @PathVariable String id,
            @RequestParam String status) {
        try {
            RelocationPlanDto updated = relocationService.updatePlanStatus(id, status);
            return ResponseEntity.ok(ApiResponse.ok("Relocation plan status updated to " + status, updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update relocation plan status: " + e.getMessage()));
        }
    }

    /**
     * Retrieves ranked safe shelter recommendations for a vulnerable habitation.
     * GET /api/v1/relocation/recommendations/{habitationId}
     */
    @GetMapping("/recommendations/{habitationId}")
    public ResponseEntity<ApiResponse<List<ShelterRecommendationDto>>> getRecommendations(
            @PathVariable String habitationId) {
        try {
            List<ShelterRecommendationDto> recs = relocationService.getShelterRecommendations(habitationId);
            return ResponseEntity.ok(ApiResponse.ok("Retrieved " + recs.size() + " shelter recommendations", recs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Calculates evacuation route between habitation and shelter on-demand.
     * GET /api/v1/relocation/routes/{habitationId}/{shelterId}
     */
    @GetMapping("/routes/{habitationId}/{shelterId}")
    public ResponseEntity<ApiResponse<EvacuationRouteDto>> calculateRoute(
            @PathVariable String habitationId,
            @PathVariable String shelterId) {
        try {
            EvacuationRouteDto route = relocationService.calculateOnDemandRoute(habitationId, shelterId);
            return ResponseEntity.ok(ApiResponse.ok("Evacuation route calculated successfully", route));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Retrieves all evacuation route geometries as a GeoJSON FeatureCollection for Leaflet map overlay.
     * GET /api/v1/relocation/routes/geojson
     */
    @GetMapping("/routes/geojson")
    public ResponseEntity<ApiResponse<GeoJsonFeatureCollectionDto>> getRoutesGeoJson() {
        GeoJsonFeatureCollectionDto geoJson = relocationService.getEvacuationRoutesGeoJson();
        return ResponseEntity.ok(ApiResponse.ok("Retrieved evacuation route GeoJSON layers", geoJson));
    }
}

