package com.abhedya.ai;

import com.abhedya.ai.client.OsrmClient;
import com.abhedya.ai.dto.*;
import com.abhedya.ai.service.RelocationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class RelocationAndRoutingTests {

    @Autowired
    private RelocationService relocationService;

    @Autowired
    private OsrmClient osrmClient;

    @Test
    @DisplayName("Should rank safe shelters by multi-factor suitability for a vulnerable habitation")
    void testShelterRecommendationsRanking() {
        List<ShelterRecommendationDto> recs = relocationService.getShelterRecommendations("hab-kl-01");

        assertNotNull(recs, "Recommendations list must not be null");
        assertFalse(recs.isEmpty(), "Should find candidate shelters");

        // Verify sorted in descending order of suitability
        for (int i = 0; i < recs.size() - 1; i++) {
            assertTrue(recs.get(i).getSuitabilityScore() >= recs.get(i + 1).getSuitabilityScore(),
                    "Shelters must be sorted by suitability score descending");
        }

        ShelterRecommendationDto top = recs.get(0);
        assertNotNull(top.getShelterId());
        assertNotNull(top.getShelterName());
        assertNotNull(top.getDistanceKm());
        assertTrue(top.getDistanceKm() > 0, "Distance should be positive");
        assertNotNull(top.getRemainingSafeCapacity());
        assertTrue(top.getSuitabilityScore() > 0, "Suitability score should be positive");
        assertNotNull(top.getRecommendationRationale());
    }

    @Test
    @DisplayName("Should calculate road-network route or fallback via OSRM client")
    void testOsrmRouteCalculation() {
        // Wayanad (11.6854, 76.1320) to Mysuru (12.2958, 76.6394)
        OsrmClient.OsrmRouteResult result = osrmClient.calculateRoute(11.6854, 76.1320, 12.2958, 76.6394);

        assertNotNull(result);
        assertTrue(result.distanceKm() > 0, "Distance should be positive");
        assertTrue(result.durationMinutes() > 0, "Duration should be positive");
        assertNotNull(result.geoJson(), "GeoJSON must be generated");
        assertTrue(result.geoJson().contains("LineString"), "Geometry must be a LineString");
        assertTrue(List.of("OSRM_ROAD_NETWORK", "GEODESIC_FALLBACK").contains(result.source()));
    }

    @Test
    @DisplayName("Should generate deterministic geodesic fallback route when road service is offline")
    void testDeterministicGeodesicFallback() {
        OsrmClient.OsrmRouteResult fallback = osrmClient.generateGeodesicFallback(11.6854, 76.1320, 12.2958, 76.6394);

        assertNotNull(fallback);
        assertEquals("GEODESIC_FALLBACK", fallback.source());
        assertTrue(fallback.distanceKm() > 50.0, "Geodesic road distance should reflect actual span");
        assertTrue(fallback.durationMinutes() > 30.0);
        assertTrue(fallback.geoJson().contains("LineString"));
        assertTrue(fallback.geoJson().contains("coordinates"));
    }

    @Test
    @DisplayName("Should create end-to-end relocation plan with assignments and evacuation route")
    void testCreateRelocationPlanEndToEnd() {
        CreateRelocationPlanRequest request = new CreateRelocationPlanRequest();
        request.setHabitationId("hab-kl-01"); // Wayanad (Population 1800, capacity 900 -> 900 excess)
        request.setNotes("High-priority monsoon evacuation");
        request.setApprovedBy("District Collector, Wayanad");

        RelocationPlanDto plan = relocationService.createRelocationPlan(request);

        assertNotNull(plan, "Plan must be created");
        assertNotNull(plan.getPlanId());
        assertTrue(plan.getPlanCode().startsWith("PLAN-"), "Plan code should have PLAN- prefix");
        assertEquals("DISPATCHED", plan.getStatus());
        assertEquals(900, plan.getTotalEvacuees(), "Should evacuate 900 excess evacuees");

        // Fleet sizing verification
        // 900 evacuees / 40 seats = 23 buses
        assertEquals(23, plan.getTotalBusesRequired());
        // 520 vulnerable / 20 = 26 ambulances
        assertTrue(plan.getTotalAmbulancesRequired() >= 1);

        // Assignments verification
        assertNotNull(plan.getAssignments());
        assertEquals(1, plan.getAssignments().size());
        RelocationAssignmentDto assignment = plan.getAssignments().get(0);
        assertEquals("hab-kl-01", assignment.getSourceHabitationId());
        assertNotNull(assignment.getDestinationShelterId());
        assertEquals(900, assignment.getAllocatedEvacuees());
        assertEquals("IMMEDIATE", assignment.getPriorityTier());

        // Evacuation route verification
        assertNotNull(plan.getRoutes());
        assertEquals(1, plan.getRoutes().size());
        EvacuationRouteDto route = plan.getRoutes().get(0);
        assertTrue(route.getDistanceKm() > 0);
        assertTrue(route.getDurationMinutes() > 0);
        assertTrue(route.getRouteGeoJson().contains("LineString"));
        assertTrue(
                route.getValidatedSafe(),
                "Route safety validation failed: " + route.getSafetyNotes());
    }

    @Test
    @DisplayName("Should calculate on-demand evacuation route between habitation and shelter")
    void testOnDemandEvacuationRoute() {
        EvacuationRouteDto route = relocationService.calculateOnDemandRoute("hab-ka-01", "sh-ka-01");

        assertNotNull(route);
        assertEquals("hab-ka-01", route.getSourceHabitationId());
        assertEquals("sh-ka-01", route.getDestinationShelterId());
        assertTrue(route.getDistanceKm() > 0);
        assertTrue(route.getDurationMinutes() > 0);
        assertNotNull(route.getRouteGeoJson());
        assertTrue(route.getRouteGeoJson().contains("LineString"));
    }

    @Test
    @DisplayName("Should retrieve evacuation routes as GeoJSON FeatureCollection")
    void testEvacuationRoutesGeoJson() {
        // Create a plan first to generate a persistent route
        CreateRelocationPlanRequest request = new CreateRelocationPlanRequest("hab-ka-01");
        relocationService.createRelocationPlan(request);

        GeoJsonFeatureCollectionDto geoJson = relocationService.getEvacuationRoutesGeoJson();
        assertNotNull(geoJson);
        assertEquals("FeatureCollection", geoJson.getType());
        assertNotNull(geoJson.getFeatures());
        assertFalse(geoJson.getFeatures().isEmpty(), "Should contain at least 1 route feature");

        GeoJsonFeatureDto feature = geoJson.getFeatures().get(0);
        assertEquals("Feature", feature.getType());
        assertNotNull(feature.getGeometry());
        assertNotNull(feature.getProperties());
        assertTrue(feature.getProperties().containsKey("distanceKm"));
        assertTrue(feature.getProperties().containsKey("durationMinutes"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when habitation does not exist")
    void testInvalidHabitationThrowsException() {
        CreateRelocationPlanRequest request = new CreateRelocationPlanRequest("INVALID-HAB-ID");
        assertThrows(IllegalArgumentException.class, () -> relocationService.createRelocationPlan(request));
    }

    @Test
    @DisplayName("Should update relocation plan status and synchronize assignment status")
    void testUpdateRelocationPlanStatus() {
        CreateRelocationPlanRequest request = new CreateRelocationPlanRequest("hab-ka-01");
        RelocationPlanDto created = relocationService.createRelocationPlan(request);
        assertEquals("DISPATCHED", created.getStatus());

        RelocationPlanDto updated = relocationService.updatePlanStatus(created.getPlanId(), "COMPLETED");
        assertEquals("COMPLETED", updated.getStatus());
        assertNotNull(updated.getAssignments());
        assertFalse(updated.getAssignments().isEmpty());
        assertEquals("ARRIVED", updated.getAssignments().get(0).getAssignmentStatus());
    }
}
