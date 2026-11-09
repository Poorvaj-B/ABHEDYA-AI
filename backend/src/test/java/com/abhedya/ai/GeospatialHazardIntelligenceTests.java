package com.abhedya.ai;

import com.abhedya.ai.dto.GeoJsonFeatureCollectionDto;
import com.abhedya.ai.dto.HabitationAtRiskDto;
import com.abhedya.ai.dto.HazardZoneDetailDto;
import com.abhedya.ai.model.Habitation;
import com.abhedya.ai.model.HazardZone;
import com.abhedya.ai.repository.HabitationRepository;
import com.abhedya.ai.repository.HazardZoneRepository;
import com.abhedya.ai.service.GeospatialService;
import com.abhedya.ai.service.HabitationService;
import com.abhedya.ai.service.HazardZoneService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
public class GeospatialHazardIntelligenceTests {

    @Autowired
    private GeospatialService geospatialService;

    @Autowired
    private HabitationService habitationService;

    @Autowired
    private HazardZoneService hazardZoneService;

    @Autowired
    private HabitationRepository habitationRepository;

    @Autowired
    private HazardZoneRepository hazardZoneRepository;

    // Benchmark sample polygon centered around Kodagu (lat: 12.42, lng: 75.73)
    private static final String KODAGU_POLYGON_GEOJSON =
            "{\"type\":\"Polygon\",\"coordinates\":[[[75.710,12.400],[75.760,12.410],[75.770,12.445],[75.725,12.450],[75.705,12.420],[75.710,12.400]]]}";

    // 1. Habitation inside a hazard polygon
    @Test
    @DisplayName("1. Habitation inside a hazard polygon is classified as INSIDE_HAZARD_ZONE")
    void testHabitationInsideHazardPolygon() {
        Geometry geom = geospatialService.parseGeoJsonToGeometry(KODAGU_POLYGON_GEOJSON);
        // Coordinate known to be inside the Kodagu polygon
        double insideLat = 12.4244;
        double insideLng = 75.7382;

        boolean isInside = geospatialService.isPointInPolygon(insideLat, insideLng, geom);
        assertTrue(isInside, "Point (12.4244, 75.7382) must be inside the Kodagu polygon");

        double distToBoundary = geospatialService.distanceToBoundaryMeters(insideLat, insideLng, geom);
        assertTrue(distToBoundary > 0.0, "Distance to boundary for interior point must be positive");
    }

    // 2. Habitation outside a hazard polygon
    @Test
    @DisplayName("2. Habitation outside a hazard polygon is classified correctly")
    void testHabitationOutsideHazardPolygon() {
        Geometry geom = geospatialService.parseGeoJsonToGeometry(KODAGU_POLYGON_GEOJSON);
        // Mysuru coordinate far outside Kodagu polygon
        double outsideLat = 12.2958;
        double outsideLng = 76.6394;

        boolean isInside = geospatialService.isPointInPolygon(outsideLat, outsideLng, geom);
        assertFalse(isInside, "Mysuru coordinates must be strictly outside Kodagu hazard polygon");

        double distToBoundary = geospatialService.distanceToBoundaryMeters(outsideLat, outsideLng, geom);
        assertTrue(distToBoundary > 50000.0, "Distance from Mysuru to Kodagu boundary must exceed 50 km");
    }

    // 3. Habitation near a hazard boundary
    @Test
    @DisplayName("3. Habitation near a hazard boundary falls within buffer radius")
    void testHabitationNearHazardBoundary() {
        Geometry geom = geospatialService.parseGeoJsonToGeometry(KODAGU_POLYGON_GEOJSON);
        // Near coordinate just 1.5 km west of the polygon exterior
        double nearLat = 12.4200;
        double nearLng = 75.6950;

        boolean isInside = geospatialService.isPointInPolygon(nearLat, nearLng, geom);
        assertFalse(isInside, "Near point should be outside the polygon perimeter");

        double distMeters = geospatialService.distanceToBoundaryMeters(nearLat, nearLng, geom);
        assertTrue(distMeters < 5000.0, "Point should be within a 5 km buffer radius");
        assertTrue(distMeters > 0.0, "Distance must be strictly positive");
    }

    // 4. Multiple overlapping hazard zones
    @Test
    @DisplayName("4. Multiple overlapping hazard zones prioritize polygon containment and severity")
    void testMultipleOverlappingHazardZones() {
        HazardZone zone1 = new HazardZone();
        zone1.setId("test-z1");
        zone1.setZoneCode("TEST-Z1");
        zone1.setHazardType("FLOOD");
        zone1.setSeverityLevel("HIGH");
        zone1.setCenterLat(12.42);
        zone1.setCenterLng(75.73);
        zone1.setBufferRadiusKm(5.0);
        zone1.setBoundaryGeoJson(KODAGU_POLYGON_GEOJSON);
        zone1.setIsActive(true);

        // Larger overlapping buffer zone with VERY_HIGH severity
        HazardZone zone2 = new HazardZone();
        zone2.setId("test-z2");
        zone2.setZoneCode("TEST-Z2");
        zone2.setHazardType("LANDSLIDE");
        zone2.setSeverityLevel("VERY_HIGH");
        zone2.setCenterLat(12.42);
        zone2.setCenterLng(75.73);
        zone2.setBufferRadiusKm(10.0);
        zone2.setBoundaryGeoJson("{\"type\":\"Polygon\",\"coordinates\":[[[75.65,12.35],[75.80,12.35],[75.80,12.50],[75.65,12.50],[75.65,12.35]]]}");
        zone2.setIsActive(true);

        Habitation hab = new Habitation("hab-test", "Test Habitation", "Kodagu", "Karnataka", 12.4244, 75.7382, 1000, 200, 500);

        List<HabitationAtRiskDto> results = geospatialService.evaluateHabitationRisks(List.of(hab), List.of(zone1, zone2));
        assertEquals(1, results.size(), "Should evaluate exactly one risk profile for the habitation");
        assertEquals("INSIDE_HAZARD_ZONE", results.get(0).getExposureStatus(), "Must be classified as inside hazard zone");
    }

    // 5. Inactive hazard zones
    @Test
    @DisplayName("5. Inactive hazard zones are excluded from active evaluation")
    void testInactiveHazardZones() {
        List<HazardZoneDetailDto> activeZones = hazardZoneService.getRedZoneDetails("ALL", "ALL", true);
        for (HazardZoneDetailDto z : activeZones) {
            assertTrue(z.getIsActive(), "Filtered active zones must all have isActive = true");
        }
    }

    // 6. Invalid GeoJSON
    @Test
    @DisplayName("6. Invalid GeoJSON is rejected with IllegalArgumentException")
    void testInvalidGeoJson() {
        // Missing coordinates
        assertThrows(IllegalArgumentException.class, () -> geospatialService.parseGeoJsonToGeometry("{\"type\":\"Polygon\"}"));

        // Unclosed ring (fewer than 4 coordinates)
        assertThrows(IllegalArgumentException.class, () -> geospatialService.parseGeoJsonToGeometry(
                "{\"type\":\"Polygon\",\"coordinates\":[[[75.71,12.40],[75.76,12.41]]]}"
        ));

        // Malformed JSON string
        assertThrows(IllegalArgumentException.class, () -> geospatialService.parseGeoJsonToGeometry("not valid json at all"));
    }

    // 7. Empty results
    @Test
    @DisplayName("7. Non-existent filter criteria return empty result set without error")
    void testEmptyResults() {
        List<HabitationAtRiskDto> results = habitationService.getHabitationsAtRisk("NonExistentState123", "EARTHQUAKE", "LOW");
        assertNotNull(results, "Result list must not be null");
        assertEquals(0, results.size(), "Result list must be empty for non-existent criteria");
    }

    // 8. Correct distance calculations
    @Test
    @DisplayName("8. Correct distance calculations using Geodesic Haversine formula")
    void testCorrectDistanceCalculations() {
        // Known reference coordinates:
        // New Delhi (28.6139, 77.2090) to Mumbai (19.0760, 72.8777) ~ 1,148 km
        double distanceMeters = geospatialService.haversineDistanceMeters(28.6139, 77.2090, 19.0760, 72.8777);
        double distanceKm = distanceMeters / 1000.0;

        assertTrue(distanceKm > 1140.0 && distanceKm < 1160.0,
                "Distance between Delhi and Mumbai should be approximately 1,148 km. Calculated: " + distanceKm);
    }

    // 9. GeoJSON response validity
    @Test
    @DisplayName("9. GeoJSON response produces valid RFC 7946 FeatureCollection")
    void testGeoJsonResponseValidity() {
        GeoJsonFeatureCollectionDto fc = hazardZoneService.getGeoJson("ALL", "ALL");
        assertNotNull(fc, "FeatureCollection must not be null");
        assertEquals("FeatureCollection", fc.getType(), "GeoJSON type must be FeatureCollection");
        assertNotNull(fc.getFeatures(), "Features list must not be null");
        assertTrue(fc.getFeatures().size() >= 6, "Must contain at least 6 active hazard zones");

        // Verify first feature structure
        var firstFeature = fc.getFeatures().get(0);
        assertEquals("Feature", firstFeature.getType(), "Each feature must have type Feature");
        assertNotNull(firstFeature.getGeometry(), "Each feature must have a geometry");
        assertNotNull(firstFeature.getProperties(), "Each feature must have properties");
        assertTrue(firstFeature.getProperties().containsKey("zoneCode"), "Properties must include zoneCode");
        assertTrue(firstFeature.getProperties().containsKey("severityLevel"), "Properties must include severityLevel");
    }

    // 10. Database spatial queries
    @Test
    @DisplayName("10. Database spatial queries return expected habitations and red zones")
    void testDatabaseSpatialQueries() {
        long totalHabs = habitationRepository.count();
        long redZoneHabs = habitationRepository.countRedZoneHabitations();
        long totalZones = hazardZoneRepository.count();

        assertEquals(20, totalHabs, "Total seeded habitations should be 20");
        assertTrue(redZoneHabs > 0, "Red zone habitations count should be greater than 0");
        assertTrue(totalZones >= 6, "Total hazard zones should be at least 6");

        // Test overcapacity query
        List<Habitation> overcapacity = habitationRepository.findHabitationsExceedingCapacity();
        assertFalse(overcapacity.isEmpty(), "Overcapacity habitations should not be empty in demonstration seed");
    }
}

