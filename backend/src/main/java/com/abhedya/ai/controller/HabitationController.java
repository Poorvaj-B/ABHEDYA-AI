package com.abhedya.ai.controller;

import com.abhedya.ai.dto.ApiResponse;
import com.abhedya.ai.dto.HabitationAtRiskDto;
import com.abhedya.ai.model.Habitation;
import com.abhedya.ai.service.HabitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/habitations")
public class HabitationController {

    private final HabitationService habitationService;

    public HabitationController(HabitationService habitationService) {
        this.habitationService = habitationService;
    }

    /**
     * Retrieves habitations with optional spatial and administrative filtering.
     * GET /api/v1/habitations
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Habitation>>> getHabitations(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "ALL") String district,
            @RequestParam(required = false, defaultValue = "false") Boolean inRedZoneOnly,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        List<Habitation> list = habitationService.getHabitations(state, district, inRedZoneOnly);

        // Simple in-memory pagination
        int fromIndex = Math.min(page * size, list.size());
        int toIndex = Math.min(fromIndex + size, list.size());
        List<Habitation> paginated = list.subList(fromIndex, toIndex);

        return ResponseEntity.ok(ApiResponse
                .ok("Retrieved " + paginated.size() + " habitations (total matching: " + list.size() + ")", paginated));
    }

    /**
     * Identifies all habitations at risk using spatial intersection and buffer
     * analysis.
     * GET /api/v1/habitations/at-risk
     */
    @GetMapping("/at-risk")
    public ResponseEntity<ApiResponse<List<HabitationAtRiskDto>>> getHabitationsAtRisk(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "ALL") String hazardType,
            @RequestParam(required = false, defaultValue = "ALL") String severity) {
        List<HabitationAtRiskDto> atRisk = habitationService.getHabitationsAtRisk(state, hazardType, severity);
        return ResponseEntity
                .ok(ApiResponse.ok("Retrieved " + atRisk.size() + " vulnerable habitations at risk", atRisk));
    }

    /**
     * Retrieves a single habitation by ID.
     * GET /api/v1/habitations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Habitation>> getHabitationById(@PathVariable String id) {
        return habitationService.getHabitationById(id)
                .map(hab -> ResponseEntity.ok(ApiResponse.ok("Habitation found", hab)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Habitation not found with ID: " + id)));
    }

    /**
     * Retrieves habitations marked in red zone.
     * GET /api/v1/habitations/red-zone
     */
    @GetMapping("/red-zone")
    public ResponseEntity<ApiResponse<List<Habitation>>> getRedZoneHabitations() {
        List<Habitation> redZoneList = habitationService.getRedZoneHabitations();
        return ResponseEntity
                .ok(ApiResponse.ok("Retrieved " + redZoneList.size() + " red-zone habitations", redZoneList));
    }

    /**
     * Retrieves habitations exceeding safe carrying capacity.
     * GET /api/v1/habitations/overcapacity
     */
    @GetMapping("/overcapacity")
    public ResponseEntity<ApiResponse<List<Habitation>>> getOvercapacityHabitations() {
        List<Habitation> list = habitationService.getHabitationsExceedingCapacity();
        return ResponseEntity
                .ok(ApiResponse.ok("Retrieved " + list.size() + " habitations exceeding safe capacity", list));
    }
}
