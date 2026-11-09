package com.abhedya.ai.dto;

public class ShelterRecommendationDto {

    private String shelterId;
    private String shelterName;
    private String district;
    private String state;
    private Double latitude;
    private Double longitude;

    private Integer maxCapacity;
    private Integer currentOccupancy;
    private Integer availableBeds;
    private Integer effectiveSafeCapacity;
    private Integer remainingSafeCapacity;

    private Double distanceKm;
    private Double estimatedTravelMinutes;
    private Double suitabilityScore; // 0 to 100
    private String medicalSupportLevel;
    private Boolean hasBackupPower;
    private Boolean isInsideHazardZone;
    private String recommendationRationale;

    public ShelterRecommendationDto() {}

    // Getters and Setters
    public String getShelterId() { return shelterId; }
    public void setShelterId(String shelterId) { this.shelterId = shelterId; }

    public String getShelterName() { return shelterName; }
    public void setShelterName(String shelterName) { this.shelterName = shelterName; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public Integer getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(Integer currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    public Integer getAvailableBeds() { return availableBeds; }
    public void setAvailableBeds(Integer availableBeds) { this.availableBeds = availableBeds; }

    public Integer getEffectiveSafeCapacity() { return effectiveSafeCapacity; }
    public void setEffectiveSafeCapacity(Integer effectiveSafeCapacity) { this.effectiveSafeCapacity = effectiveSafeCapacity; }

    public Integer getRemainingSafeCapacity() { return remainingSafeCapacity; }
    public void setRemainingSafeCapacity(Integer remainingSafeCapacity) { this.remainingSafeCapacity = remainingSafeCapacity; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getEstimatedTravelMinutes() { return estimatedTravelMinutes; }
    public void setEstimatedTravelMinutes(Double estimatedTravelMinutes) { this.estimatedTravelMinutes = estimatedTravelMinutes; }

    public Double getSuitabilityScore() { return suitabilityScore; }
    public void setSuitabilityScore(Double suitabilityScore) { this.suitabilityScore = suitabilityScore; }

    public String getMedicalSupportLevel() { return medicalSupportLevel; }
    public void setMedicalSupportLevel(String medicalSupportLevel) { this.medicalSupportLevel = medicalSupportLevel; }

    public Boolean getHasBackupPower() { return hasBackupPower; }
    public void setHasBackupPower(Boolean hasBackupPower) { this.hasBackupPower = hasBackupPower; }

    public Boolean getInsideHazardZone() { return isInsideHazardZone; }
    public void setInsideHazardZone(Boolean insideHazardZone) { isInsideHazardZone = insideHazardZone; }

    public String getRecommendationRationale() { return recommendationRationale; }
    public void setRecommendationRationale(String recommendationRationale) { this.recommendationRationale = recommendationRationale; }
}

