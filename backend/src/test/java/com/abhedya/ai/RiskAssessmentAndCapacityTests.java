package com.abhedya.ai;

import com.abhedya.ai.dto.RiskAssessmentRequest;
import com.abhedya.ai.dto.RiskAssessmentResponseDto;
import com.abhedya.ai.dto.ShelterCapacityAuditDto;
import com.abhedya.ai.model.Shelter;
import com.abhedya.ai.repository.ShelterRepository;
import com.abhedya.ai.service.RiskAssessmentService;
import com.abhedya.ai.service.ShelterCapacityService;
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
public class RiskAssessmentAndCapacityTests {

    @Autowired
    private RiskAssessmentService riskAssessmentService;

    @Autowired
    private ShelterCapacityService shelterCapacityService;

    @Autowired
    private ShelterRepository shelterRepository;

    @Test
    @DisplayName("1. Multi-factor risk calculation generates explainable composite score and classification")
    void testMultiFactorRiskCalculation() {
        RiskAssessmentRequest request = new RiskAssessmentRequest();
        request.setHabitationId("hab-ka-01"); // Kodagu Vulnerable Village
        request.setRainfall24hMm(100.0);

        RiskAssessmentResponseDto response = riskAssessmentService.assessRisk(request);

        assertNotNull(response);
        assertEquals("hab-ka-01", response.getHabitationId());
        assertTrue(response.getHazardIndex() >= 75.0, "Kodagu inside landslide zone should have high hazard index");
        assertTrue(response.getVulnerabilityIndex() >= 70.0, "Steep terrain + 100mm rain should produce high vulnerability index");
        assertTrue(response.getCapacityPressureIndex() > 0.0, "Population exceeds carrying capacity, pressure index must be positive");
        assertTrue(response.getCompositeScore() >= 50.0, "Composite score should indicate high risk");
        assertTrue(response.getRelocationRecommended(), "Relocation must be recommended for high risk overcapacity habitation");
        assertNotNull(response.getAssessmentRationale(), "Rationale must be populated");
        assertFalse(response.getAssessmentRationale().isBlank());
    }

    @Test
    @DisplayName("2. Rainfall escalation increases vulnerability index and composite risk score")
    void testRainfallEscalationIncreasesRisk() {
        RiskAssessmentRequest dryRequest = new RiskAssessmentRequest();
        dryRequest.setHabitationId("hab-ka-01");
        dryRequest.setRainfall24hMm(0.0);

        RiskAssessmentRequest wetRequest = new RiskAssessmentRequest();
        wetRequest.setHabitationId("hab-ka-01");
        wetRequest.setRainfall24hMm(180.0); // Severe rainfall event

        RiskAssessmentResponseDto dryResponse = riskAssessmentService.assessRisk(dryRequest);
        RiskAssessmentResponseDto wetResponse = riskAssessmentService.assessRisk(wetRequest);

        assertTrue(wetResponse.getVulnerabilityIndex() > dryResponse.getVulnerabilityIndex(),
                "Vulnerability index under 180mm rain must exceed dry vulnerability index");
        assertTrue(wetResponse.getCompositeScore() > dryResponse.getCompositeScore(),
                "Composite score under severe rainfall must exceed dry score");
    }

    @Test
    @DisplayName("3. Configurable weights properly rebalance multi-factor scoring")
    void testConfigurableModelWeights() {
        RiskAssessmentRequest request = new RiskAssessmentRequest();
        request.setHabitationId("hab-ka-01");
        request.setHazardWeight(0.60);
        request.setVulnerabilityWeight(0.20);
        request.setCapacityPressureWeight(0.10);
        request.setVulnerableRatioWeight(0.10);

        RiskAssessmentResponseDto response = riskAssessmentService.assessRisk(request);
        assertNotNull(response.getModelWeights());
        assertEquals(0.60, response.getModelWeights().get("hazard"), 0.01);
    }

    @Test
    @DisplayName("4. Multi-resource shelter capacity evaluates water and sanitation constraints (Sphere Standards)")
    void testShelterResourceConstraints() {
        Shelter shelter = new Shelter();
        shelter.setId("test-sh-01");
        shelter.setName("Resource Bottleneck Shelter");
        shelter.setDistrict("Kodagu");
        shelter.setState("Karnataka");
        shelter.setLatitude(12.42);
        shelter.setLongitude(75.74);
        shelter.setMaxCapacity(1000); // 1000 physical beds
        shelter.setCurrentOccupancy(100);
        shelter.setAvailableBeds(900);
        shelter.setPotableWaterLitersDay(6000.0); // 6000 L / 15 L/day = 400 person capacity
        shelter.setSanitationUnits(15); // 15 units * 20 persons = 300 person capacity
        shelter.setHasBackupPower(true);
        shelter.setStatus("OPERATIONAL");
        shelter.setIsVerifiedOperational(true);

        ShelterCapacityAuditDto audit = shelterCapacityService.auditShelter(shelter);

        assertEquals(1000, audit.getMaxCapacity(), "Physical footprint capacity is 1000");
        assertEquals(400, audit.getWaterSupportedCapacity(), "Water supports 400 persons at 15L/day");
        assertEquals(300, audit.getSanitationSupportedCapacity(), "Sanitation supports 300 persons at 20 persons/unit");
        assertEquals(300, audit.getEffectiveSafeCapacity(), "Effective safe capacity must be bottlenecked at 300");
        assertEquals("SANITATION", audit.getCapacityLimitingFactor(), "Limiting factor must be SANITATION");
        assertEquals(200, audit.getRemainingSafeHeadroom(), "Headroom must be 300 - 100 = 200");
    }

    @Test
    @DisplayName("5. Strict safe capacity enforcement prevents dangerous overloading")
    void testStrictSafeCapacityEnforcement() {
        // Retrieve seeded shelter
        Shelter shelter = shelterRepository.findById("sh-ka-01").orElseThrow();
        int safeCapacity = shelter.getMaxCapacity();

        // Legal update within safe capacity
        assertDoesNotThrow(() -> {
            shelterCapacityService.updateOccupancy(shelter.getId(), 500);
        });

        // Illegal update exceeding safe capacity must be rejected
        int dangerousOccupancy = safeCapacity + 500;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            shelterCapacityService.updateOccupancy(shelter.getId(), dangerousOccupancy);
        });
        assertTrue(ex.getMessage().contains("Cannot exceed shelter safe capacity"));
    }

    @Test
    @DisplayName("6. Historical assessment retrieval and filtering")
    void testAssessmentRetrieval() {
        List<RiskAssessmentResponseDto> assessments = riskAssessmentService.getAllAssessments("ALL", false);
        assertNotNull(assessments);
        assertFalse(assessments.isEmpty(), "Seeded baseline assessments must be retrievable");

        List<RiskAssessmentResponseDto> veryHighOnly = riskAssessmentService.getAllAssessments("VERY_HIGH", false);
        for (RiskAssessmentResponseDto a : veryHighOnly) {
            assertEquals("VERY_HIGH", a.getClassification());
        }
    }
}
