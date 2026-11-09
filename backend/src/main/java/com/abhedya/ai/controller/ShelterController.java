package com.abhedya.ai.controller;

import com.abhedya.ai.dto.ApiResponse;
import com.abhedya.ai.dto.ShelterCapacityAuditDto;
import com.abhedya.ai.dto.UpdateShelterOccupancyRequest;
import com.abhedya.ai.model.Shelter;
import com.abhedya.ai.service.ShelterCapacityService;
import com.abhedya.ai.service.ShelterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shelters")
public class ShelterController {

    private final ShelterService shelterService;
    private final ShelterCapacityService shelterCapacityService;

    public ShelterController(ShelterService shelterService,
                             ShelterCapacityService shelterCapacityService) {
        this.shelterService = shelterService;
        this.shelterCapacityService = shelterCapacityService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Shelter>>> getAllShelters(
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        List<Shelter> shelters = shelterService.getSheltersByState(state);
        return ResponseEntity.ok(ApiResponse.ok("Retrieved " + shelters.size() + " relief shelters", shelters));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Shelter>>> getAvailableShelters() {
        List<Shelter> shelters = shelterService.getAvailableShelters();
        return ResponseEntity.ok(ApiResponse.ok("Retrieved " + shelters.size() + " shelters with safe capacity", shelters));
    }

    /**
     * Multi-resource carrying-capacity assessment across all emergency shelters.
     * Evaluates potable water, sanitation units, medical level, power, and safe headroom.
     * GET /api/v1/shelters/capacity-assessment
     */
    @GetMapping("/capacity-assessment")
    public ResponseEntity<ApiResponse<List<ShelterCapacityAuditDto>>> getShelterCapacityAssessments(
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        List<ShelterCapacityAuditDto> audits = shelterCapacityService.auditAllShelters(state);
        return ResponseEntity.ok(ApiResponse.ok("Retrieved " + audits.size() + " multi-resource shelter capacity assessments", audits));
    }

    /**
     * Detailed capacity and logistics audit for a single shelter.
     * GET /api/v1/shelters/{id}/capacity-audit
     */
    @GetMapping("/{id}/capacity-audit")
    public ResponseEntity<ApiResponse<ShelterCapacityAuditDto>> getShelterCapacityAudit(@PathVariable String id) {
        try {
            ShelterCapacityAuditDto audit = shelterCapacityService.auditShelterById(id);
            return ResponseEntity.ok(ApiResponse.ok("Shelter capacity audit retrieved", audit));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Shelter>> getShelterById(@PathVariable String id) {
        return shelterService.getShelterById(id)
                .map(sh -> ResponseEntity.ok(ApiResponse.ok("Shelter found", sh)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Shelter not found: " + id)));
    }

    /**
     * Updates shelter occupancy with strict safe-capacity enforcement.
     * POST /api/v1/shelters/{id}/occupancy
     */
    @PostMapping("/{id}/occupancy")
    public ResponseEntity<ApiResponse<ShelterCapacityAuditDto>> updateShelterOccupancy(
            @PathVariable String id,
            @Valid @RequestBody UpdateShelterOccupancyRequest request) {
        try {
            ShelterCapacityAuditDto updated = shelterCapacityService.updateOccupancy(id, request.getNewOccupancy());
            return ResponseEntity.ok(ApiResponse.ok("Shelter occupancy updated successfully", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
