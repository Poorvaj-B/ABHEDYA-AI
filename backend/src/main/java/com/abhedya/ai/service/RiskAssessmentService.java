package com.abhedya.ai.service;

import com.abhedya.ai.dto.HabitationAtRiskDto;
import com.abhedya.ai.dto.RiskAssessmentRequest;
import com.abhedya.ai.dto.RiskAssessmentResponseDto;
import com.abhedya.ai.dto.WeatherTelemetryDto;
import com.abhedya.ai.model.Habitation;
import com.abhedya.ai.model.HazardZone;
import com.abhedya.ai.model.RiskAssessment;
import com.abhedya.ai.repository.HabitationRepository;
import com.abhedya.ai.repository.HazardZoneRepository;
import com.abhedya.ai.repository.RiskAssessmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RiskAssessmentService {

    private final HabitationRepository habitationRepository;
    private final HazardZoneRepository hazardZoneRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final GeospatialService geospatialService;
    private final WeatherService weatherService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public RiskAssessmentService(HabitationRepository habitationRepository,
                                 HazardZoneRepository hazardZoneRepository,
                                 RiskAssessmentRepository riskAssessmentRepository,
                                 GeospatialService geospatialService,
                                 @Autowired(required = false) WeatherService weatherService) {
        this.habitationRepository = habitationRepository;
        this.hazardZoneRepository = hazardZoneRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.geospatialService = geospatialService;
        this.weatherService = weatherService;
    }

    public RiskAssessmentService(HabitationRepository habitationRepository,
                                 HazardZoneRepository hazardZoneRepository,
                                 RiskAssessmentRepository riskAssessmentRepository,
                                 GeospatialService geospatialService) {
        this(habitationRepository, hazardZoneRepository, riskAssessmentRepository, geospatialService, null);
    }

    public RiskAssessmentResponseDto assessRisk(RiskAssessmentRequest request) {
        Habitation hab = habitationRepository.findById(request.getHabitationId())
                .orElseThrow(() -> new IllegalArgumentException("Habitation not found with ID: " + request.getHabitationId()));

        List<HazardZone> activeZones = hazardZoneRepository.findByIsActiveTrue();
        List<HabitationAtRiskDto> spatialRisks = geospatialService.evaluateHabitationRisks(List.of(hab), activeZones);
        HabitationAtRiskDto spatialRisk = spatialRisks.isEmpty() ? null : spatialRisks.get(0);

        // 1. Hazard Severity & Proximity Index (0 to 100)
        double hazardIndex = 20.0; // Base baseline
        if (spatialRisk != null) {
            double baseSeverity = switch (spatialRisk.getSeverityLevel() != null ? spatialRisk.getSeverityLevel() : "LOW") {
                case "VERY_HIGH" -> 100.0;
                case "HIGH" -> 75.0;
                case "MEDIUM" -> 50.0;
                default -> 30.0;
            };

            if ("INSIDE_HAZARD_ZONE".equals(spatialRisk.getExposureStatus())) {
                hazardIndex = baseSeverity;
            } else if ("WITHIN_BUFFER".equals(spatialRisk.getExposureStatus())) {
                hazardIndex = baseSeverity * 0.70;
            } else {
                hazardIndex = baseSeverity * 0.35;
            }
        }

        // 2. Terrain & Environmental Vulnerability Index (0 to 100)
        double terrainBase = 40.0;
        String terrain = hab.getTerrainType() != null ? hab.getTerrainType().toLowerCase() : "";
        if (terrain.contains("steep") || terrain.contains("fault") || terrain.contains("escarpment")) {
            terrainBase = 85.0;
        } else if (terrain.contains("floodplain") || terrain.contains("basin") || terrain.contains("lowland")) {
            terrainBase = 75.0;
        } else if (terrain.contains("mudflat") || terrain.contains("estuary") || terrain.contains("coastal")) {
            terrainBase = 70.0;
        } else if (terrain.contains("valley") || terrain.contains("wetland")) {
            terrainBase = 60.0;
        }

        WeatherTelemetryDto weatherTelemetry = null;
        String weatherDataSource = "USER_SIMULATED";
        double rainfall = request.getRainfall24hMm() != null ? request.getRainfall24hMm() : 0.0;

        if (Boolean.TRUE.equals(request.getUseLiveWeather()) && weatherService != null) {
            try {
                weatherTelemetry = weatherService.getWeatherForHabitation(hab.getId());
                weatherDataSource = weatherTelemetry.getDataSource();
                if (weatherTelemetry.getPrecipitationLast24hMm() != null && weatherTelemetry.getPrecipitationLast24hMm() > 0) {
                    rainfall = weatherTelemetry.getPrecipitationLast24hMm();
                } else if (weatherTelemetry.getCurrentPrecipitationMm() != null) {
                    rainfall = weatherTelemetry.getCurrentPrecipitationMm();
                }
            } catch (Exception ignored) {
                weatherDataSource = "FALLBACK_BASELINE";
            }
        }

        double rainfallFactor = 0.0;
        if (rainfall >= 150.0) {
            rainfallFactor = 35.0;
        } else if (rainfall >= 100.0) {
            rainfallFactor = 25.0;
        } else if (rainfall >= 65.0) {
            rainfallFactor = 15.0;
        } else if (rainfall >= 25.0) {
            rainfallFactor = 8.0;
        }

        double slopeAdjustment = 0.0;
        if (request.getSimulatedSlopeAngle() != null && request.getSimulatedSlopeAngle() >= 30.0) {
            slopeAdjustment = 15.0;
        }

        // Severe weather risk adjustment based on active meteorological alert tier
        double severeWeatherBoost = 0.0;
        if (weatherTelemetry != null) {
            if ("EMERGENCY".equalsIgnoreCase(weatherTelemetry.getAlertTier())) {
                severeWeatherBoost = 15.0;
            } else if ("WARNING".equalsIgnoreCase(weatherTelemetry.getAlertTier())) {
                severeWeatherBoost = 10.0;
            } else if ("WATCH".equalsIgnoreCase(weatherTelemetry.getAlertTier())) {
                severeWeatherBoost = 5.0;
            }
        }

        double vulnerabilityIndex = Math.min(100.0, terrainBase + rainfallFactor + slopeAdjustment + severeWeatherBoost);

        // 3. Vulnerable Population Ratio Index (0 to 100)
        double vulnerableRatioIndex = 0.0;
        if (hab.getTotalPopulation() > 0 && hab.getVulnerablePopulation() != null) {
            double ratio = ((double) hab.getVulnerablePopulation() / hab.getTotalPopulation()) * 100.0;
            vulnerableRatioIndex = Math.min(100.0, ratio * 1.6);
        }

        // 4. Carrying Capacity Pressure Index (0 to 100)
        int excess = Math.max(0, hab.getTotalPopulation() - hab.getSafeCarryingCapacity());
        double capacityPressureIndex = 0.0;
        if (hab.getSafeCarryingCapacity() > 0 && excess > 0) {
            capacityPressureIndex = Math.min(100.0, ((double) excess / hab.getSafeCarryingCapacity()) * 100.0);
        }

        // Normalize Model Weights
        double wH = request.getHazardWeight() != null ? Math.max(0.05, request.getHazardWeight()) : 0.35;
        double wV = request.getVulnerabilityWeight() != null ? Math.max(0.05, request.getVulnerabilityWeight()) : 0.25;
        double wC = request.getCapacityPressureWeight() != null ? Math.max(0.05, request.getCapacityPressureWeight()) : 0.20;
        double wP = request.getVulnerableRatioWeight() != null ? Math.max(0.05, request.getVulnerableRatioWeight()) : 0.20;

        double totalWeight = wH + wV + wC + wP;
        wH /= totalWeight;
        wV /= totalWeight;
        wC /= totalWeight;
        wP /= totalWeight;

        // Multi-Factor Composite Score
        double compositeScore = (wH * hazardIndex) + (wV * vulnerabilityIndex) + (wC * capacityPressureIndex) + (wP * vulnerableRatioIndex);
        compositeScore = Math.round(compositeScore * 10.0) / 10.0;

        // Classification
        String classification;
        if (compositeScore >= 75.0) {
            classification = "VERY_HIGH";
        } else if (compositeScore >= 50.0) {
            classification = "HIGH";
        } else if (compositeScore >= 25.0) {
            classification = "MEDIUM";
        } else {
            classification = "LOW";
        }

        boolean relocationRecommended = excess > 0 && (compositeScore >= 50.0 || "VERY_HIGH".equals(classification));

        // Generate Explainable Rationale
        StringBuilder rationale = new StringBuilder();
        if (spatialRisk != null && "INSIDE_HAZARD_ZONE".equals(spatialRisk.getExposureStatus())) {
            rationale.append("Habitation lies strictly inside active ").append(spatialRisk.getHazardType()).append(" hazard polygon (").append(spatialRisk.getHazardZoneCode()).append("). ");
        } else if (spatialRisk != null && "WITHIN_BUFFER".equals(spatialRisk.getExposureStatus())) {
            rationale.append("Habitation is within ").append(spatialRisk.getDistanceToHazardBoundaryMeters()).append("m of active ").append(spatialRisk.getHazardType()).append(" zone. ");
        } else {
            rationale.append("Habitation is situated outside immediate active hazard perimeters. ");
        }

        if (weatherTelemetry != null) {
            rationale.append(String.format(Locale.US, "Live weather telemetry (%s): %.1f°C, 24h precip %.1fmm, wind %.1f km/h [Alert: %s]. ",
                    weatherTelemetry.getDataSource(),
                    weatherTelemetry.getTemperatureC() != null ? weatherTelemetry.getTemperatureC() : 0.0,
                    rainfall,
                    weatherTelemetry.getWindSpeedKmh() != null ? weatherTelemetry.getWindSpeedKmh() : 0.0,
                    weatherTelemetry.getAlertTier()));
        } else if (rainfall >= 65.0) {
            rationale.append("24-hour precipitation (").append(rainfall).append("mm) exceeds safety thresholds. ");
        }
        if (excess > 0) {
            rationale.append("Population exceeds safe carrying capacity by ").append(excess).append(" persons. ");
        }
        if (relocationRecommended) {
            rationale.append("PRIORITY RELOCATION REQUIRED.");
        } else {
            rationale.append("Situation stable; routine monitoring advised.");
        }

        // Persist Assessment
        RiskAssessment assessment = new RiskAssessment();
        assessment.setId(UUID.randomUUID().toString());
        assessment.setHabitation(hab);
        assessment.setHazardIndex(Math.round(hazardIndex * 10.0) / 10.0);
        assessment.setVulnerabilityIndex(Math.round(vulnerabilityIndex * 10.0) / 10.0);
        assessment.setCapacityPressureIndex(Math.round(capacityPressureIndex * 10.0) / 10.0);
        assessment.setVulnerableRatioIndex(Math.round(vulnerableRatioIndex * 10.0) / 10.0);
        assessment.setCompositeScore(compositeScore);
        assessment.setClassification(classification);
        assessment.setExcessPopulation(excess);
        assessment.setRelocationRecommended(relocationRecommended);
        assessment.setRainfall24hMm(rainfall);

        Map<String, Double> weightsMap = new LinkedHashMap<>();
        weightsMap.put("hazard", Math.round(wH * 100.0) / 100.0);
        weightsMap.put("vulnerability", Math.round(wV * 100.0) / 100.0);
        weightsMap.put("capacity", Math.round(wC * 100.0) / 100.0);
        weightsMap.put("vulnerableRatio", Math.round(wP * 100.0) / 100.0);

        try {
            assessment.setModelWeightsJson(objectMapper.writeValueAsString(weightsMap));
        } catch (Exception ignored) {}

        assessment.setAssessmentRationale(rationale.toString());
        assessment.setAssessedAt(LocalDateTime.now());

        RiskAssessment saved = riskAssessmentRepository.save(assessment);

        RiskAssessmentResponseDto dto = toDto(saved, hab, weightsMap);
        dto.setWeatherDataSource(weatherDataSource);
        dto.setWeatherTelemetry(weatherTelemetry);
        return dto;
    }

    /**
     * Executes risk assessment automatically querying live Open-Meteo weather for the habitation.
     */
    public RiskAssessmentResponseDto assessRiskWithLiveWeather(String habitationId, RiskAssessmentRequest customRequest) {
        RiskAssessmentRequest req = customRequest != null ? customRequest : new RiskAssessmentRequest();
        req.setHabitationId(habitationId);
        req.setUseLiveWeather(true);
        return assessRisk(req);
    }

    public List<RiskAssessmentResponseDto> getAllAssessments(String classification, Boolean relocationOnly) {
        List<RiskAssessment> list = riskAssessmentRepository.findAll();

        if (classification != null && !"ALL".equalsIgnoreCase(classification) && !classification.isBlank()) {
            list = list.stream().filter(a -> classification.equalsIgnoreCase(a.getClassification())).collect(Collectors.toList());
        }

        if (Boolean.TRUE.equals(relocationOnly)) {
            list = list.stream().filter(RiskAssessment::getRelocationRecommended).collect(Collectors.toList());
        }

        return list.stream().map(a -> {
            Map<String, Double> weights = parseWeights(a.getModelWeightsJson());
            return toDto(a, a.getHabitation(), weights);
        }).collect(Collectors.toList());
    }

    public Optional<RiskAssessmentResponseDto> getLatestAssessmentForHabitation(String habitationId) {
        return riskAssessmentRepository.findTopByHabitationIdOrderByAssessedAtDesc(habitationId)
                .map(a -> {
                    Map<String, Double> weights = parseWeights(a.getModelWeightsJson());
                    return toDto(a, a.getHabitation(), weights);
                });
    }

    private RiskAssessmentResponseDto toDto(RiskAssessment assessment, Habitation hab, Map<String, Double> weights) {
        RiskAssessmentResponseDto dto = new RiskAssessmentResponseDto();
        dto.setAssessmentId(assessment.getId());
        dto.setHabitationId(hab.getId());
        dto.setHabitationName(hab.getName());
        dto.setDistrict(hab.getDistrict());
        dto.setState(hab.getState());
        dto.setLatitude(hab.getLatitude());
        dto.setLongitude(hab.getLongitude());
        dto.setTotalPopulation(hab.getTotalPopulation());
        dto.setVulnerablePopulation(hab.getVulnerablePopulation());
        dto.setSafeCarryingCapacity(hab.getSafeCarryingCapacity());
        dto.setExcessPopulation(assessment.getExcessPopulation());
        dto.setHazardIndex(assessment.getHazardIndex());
        dto.setVulnerabilityIndex(assessment.getVulnerabilityIndex());
        dto.setCapacityPressureIndex(assessment.getCapacityPressureIndex());
        dto.setVulnerableRatioIndex(assessment.getVulnerableRatioIndex());
        dto.setCompositeScore(assessment.getCompositeScore());
        dto.setClassification(assessment.getClassification());
        dto.setRelocationRecommended(assessment.getRelocationRecommended());
        dto.setRainfall24hMm(assessment.getRainfall24hMm());
        dto.setModelWeights(weights);
        dto.setAssessmentRationale(assessment.getAssessmentRationale());
        dto.setAssessedAt(assessment.getAssessedAt());
        return dto;
    }

    private Map<String, Double> parseWeights(String json) {
        if (json == null || json.isBlank()) return Collections.emptyMap();
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
