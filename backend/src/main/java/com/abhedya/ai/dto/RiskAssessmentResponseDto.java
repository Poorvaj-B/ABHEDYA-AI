package com.abhedya.ai.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class RiskAssessmentResponseDto {

    private String assessmentId;
    private String habitationId;
    private String habitationName;
    private String district;
    private String state;
    private Double latitude;
    private Double longitude;
    private Integer totalPopulation;
    private Integer vulnerablePopulation;
    private Integer safeCarryingCapacity;
    private Integer excessPopulation;

    // Component Indices (0 to 100)
    private Double hazardIndex;
    private Double vulnerabilityIndex;
    private Double capacityPressureIndex;
    private Double vulnerableRatioIndex;
    private Double compositeScore;

    private String classification; // VERY_HIGH, HIGH, MEDIUM, LOW
    private Boolean relocationRecommended;
    private Double rainfall24hMm;
    private String weatherDataSource;
    private WeatherTelemetryDto weatherTelemetry;
    private Map<String, Double> modelWeights;
    private String assessmentRationale;
    private LocalDateTime assessedAt;

    public RiskAssessmentResponseDto() {}

    // Getters and Setters
    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }

    public String getHabitationId() { return habitationId; }
    public void setHabitationId(String habitationId) { this.habitationId = habitationId; }

    public String getHabitationName() { return habitationName; }
    public void setHabitationName(String habitationName) { this.habitationName = habitationName; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getTotalPopulation() { return totalPopulation; }
    public void setTotalPopulation(Integer totalPopulation) { this.totalPopulation = totalPopulation; }

    public Integer getVulnerablePopulation() { return vulnerablePopulation; }
    public void setVulnerablePopulation(Integer vulnerablePopulation) { this.vulnerablePopulation = vulnerablePopulation; }

    public Integer getSafeCarryingCapacity() { return safeCarryingCapacity; }
    public void setSafeCarryingCapacity(Integer safeCarryingCapacity) { this.safeCarryingCapacity = safeCarryingCapacity; }

    public Integer getExcessPopulation() { return excessPopulation; }
    public void setExcessPopulation(Integer excessPopulation) { this.excessPopulation = excessPopulation; }

    public Double getHazardIndex() { return hazardIndex; }
    public void setHazardIndex(Double hazardIndex) { this.hazardIndex = hazardIndex; }

    public Double getVulnerabilityIndex() { return vulnerabilityIndex; }
    public void setVulnerabilityIndex(Double vulnerabilityIndex) { this.vulnerabilityIndex = vulnerabilityIndex; }

    public Double getCapacityPressureIndex() { return capacityPressureIndex; }
    public void setCapacityPressureIndex(Double capacityPressureIndex) { this.capacityPressureIndex = capacityPressureIndex; }

    public Double getVulnerableRatioIndex() { return vulnerableRatioIndex; }
    public void setVulnerableRatioIndex(Double vulnerableRatioIndex) { this.vulnerableRatioIndex = vulnerableRatioIndex; }

    public Double getCompositeScore() { return compositeScore; }
    public void setCompositeScore(Double compositeScore) { this.compositeScore = compositeScore; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    public Boolean getRelocationRecommended() { return relocationRecommended; }
    public void setRelocationRecommended(Boolean relocationRecommended) { this.relocationRecommended = relocationRecommended; }

    public Double getRainfall24hMm() { return rainfall24hMm; }
    public void setRainfall24hMm(Double rainfall24hMm) { this.rainfall24hMm = rainfall24hMm; }

    public String getWeatherDataSource() { return weatherDataSource; }
    public void setWeatherDataSource(String weatherDataSource) { this.weatherDataSource = weatherDataSource; }

    public WeatherTelemetryDto getWeatherTelemetry() { return weatherTelemetry; }
    public void setWeatherTelemetry(WeatherTelemetryDto weatherTelemetry) { this.weatherTelemetry = weatherTelemetry; }

    public Map<String, Double> getModelWeights() { return modelWeights; }
    public void setModelWeights(Map<String, Double> modelWeights) { this.modelWeights = modelWeights; }

    public String getAssessmentRationale() { return assessmentRationale; }
    public void setAssessmentRationale(String assessmentRationale) { this.assessmentRationale = assessmentRationale; }

    public LocalDateTime getAssessedAt() { return assessedAt; }
    public void setAssessedAt(LocalDateTime assessedAt) { this.assessedAt = assessedAt; }
}
