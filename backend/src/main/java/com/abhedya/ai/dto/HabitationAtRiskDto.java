package com.abhedya.ai.dto;

public class HabitationAtRiskDto {

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
    private String exposureStatus; // "INSIDE_HAZARD_ZONE", "WITHIN_BUFFER", "LOW_EXPOSURE"
    private Double distanceToHazardBoundaryMeters;
    private Double distanceToHazardCenterKm;
    private String hazardZoneId;
    private String hazardZoneCode;
    private String hazardType;
    private String severityLevel;
    private Boolean isEstimatedBuffer;

    public HabitationAtRiskDto() {}

    // Getters and Setters
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

    public String getExposureStatus() { return exposureStatus; }
    public void setExposureStatus(String exposureStatus) { this.exposureStatus = exposureStatus; }

    public Double getDistanceToHazardBoundaryMeters() { return distanceToHazardBoundaryMeters; }
    public void setDistanceToHazardBoundaryMeters(Double distanceToHazardBoundaryMeters) { this.distanceToHazardBoundaryMeters = distanceToHazardBoundaryMeters; }

    public Double getDistanceToHazardCenterKm() { return distanceToHazardCenterKm; }
    public void setDistanceToHazardCenterKm(Double distanceToHazardCenterKm) { this.distanceToHazardCenterKm = distanceToHazardCenterKm; }

    public String getHazardZoneId() { return hazardZoneId; }
    public void setHazardZoneId(String hazardZoneId) { this.hazardZoneId = hazardZoneId; }

    public String getHazardZoneCode() { return hazardZoneCode; }
    public void setHazardZoneCode(String hazardZoneCode) { this.hazardZoneCode = hazardZoneCode; }

    public String getHazardType() { return hazardType; }
    public void setHazardType(String hazardType) { this.hazardType = hazardType; }

    public String getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(String severityLevel) { this.severityLevel = severityLevel; }

    public Boolean getIsEstimatedBuffer() { return isEstimatedBuffer; }
    public void setIsEstimatedBuffer(Boolean isEstimatedBuffer) { this.isEstimatedBuffer = isEstimatedBuffer; }
}

