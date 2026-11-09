package com.abhedya.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class RiskAssessmentRequest {

    @NotBlank(message = "Habitation ID is required")
    private String habitationId;

    @PositiveOrZero(message = "Rainfall must be zero or positive")
    private Double rainfall24hMm = 0.0;

    private Boolean useLiveWeather = false;

    private Double simulatedSlopeAngle;

    // Configurable model weights (must sum to 1.0)
    private Double hazardWeight = 0.35;
    private Double vulnerabilityWeight = 0.25;
    private Double capacityPressureWeight = 0.20;
    private Double vulnerableRatioWeight = 0.20;

    public RiskAssessmentRequest() {}

    // Getters and Setters
    public String getHabitationId() { return habitationId; }
    public void setHabitationId(String habitationId) { this.habitationId = habitationId; }

    public Double getRainfall24hMm() { return rainfall24hMm; }
    public void setRainfall24hMm(Double rainfall24hMm) { this.rainfall24hMm = rainfall24hMm; }

    public Boolean getUseLiveWeather() { return useLiveWeather; }
    public void setUseLiveWeather(Boolean useLiveWeather) { this.useLiveWeather = useLiveWeather; }

    public Double getSimulatedSlopeAngle() { return simulatedSlopeAngle; }
    public void setSimulatedSlopeAngle(Double simulatedSlopeAngle) { this.simulatedSlopeAngle = simulatedSlopeAngle; }

    public Double getHazardWeight() { return hazardWeight; }
    public void setHazardWeight(Double hazardWeight) { this.hazardWeight = hazardWeight; }

    public Double getVulnerabilityWeight() { return vulnerabilityWeight; }
    public void setVulnerabilityWeight(Double vulnerabilityWeight) { this.vulnerabilityWeight = vulnerabilityWeight; }

    public Double getCapacityPressureWeight() { return capacityPressureWeight; }
    public void setCapacityPressureWeight(Double capacityPressureWeight) { this.capacityPressureWeight = capacityPressureWeight; }

    public Double getVulnerableRatioWeight() { return vulnerableRatioWeight; }
    public void setVulnerableRatioWeight(Double vulnerableRatioWeight) { this.vulnerableRatioWeight = vulnerableRatioWeight; }
}
