package com.abhedya.ai.dto;

import java.time.LocalDateTime;

public class HazardZoneDetailDto {

    private String id;
    private String zoneCode;
    private String hazardType;
    private String severityLevel;
    private String state;
    private String district;
    private Double centerLat;
    private Double centerLng;
    private Double bufferRadiusKm;
    private String boundaryGeoJson;
    private Boolean isActive;
    private Boolean isEstimatedBuffer;
    private Double areaSqKm;
    private Double perimeterKm;
    private long affectedHabitationsCount;
    private long affectedTotalPopulation;
    private long affectedVulnerablePopulation;
    private LocalDateTime createdAt;

    public HazardZoneDetailDto() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getZoneCode() { return zoneCode; }
    public void setZoneCode(String zoneCode) { this.zoneCode = zoneCode; }

    public String getHazardType() { return hazardType; }
    public void setHazardType(String hazardType) { this.hazardType = hazardType; }

    public String getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(String severityLevel) { this.severityLevel = severityLevel; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public Double getCenterLat() { return centerLat; }
    public void setCenterLat(Double centerLat) { this.centerLat = centerLat; }

    public Double getCenterLng() { return centerLng; }
    public void setCenterLng(Double centerLng) { this.centerLng = centerLng; }

    public Double getBufferRadiusKm() { return bufferRadiusKm; }
    public void setBufferRadiusKm(Double bufferRadiusKm) { this.bufferRadiusKm = bufferRadiusKm; }

    public String getBoundaryGeoJson() { return boundaryGeoJson; }
    public void setBoundaryGeoJson(String boundaryGeoJson) { this.boundaryGeoJson = boundaryGeoJson; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsEstimatedBuffer() { return isEstimatedBuffer; }
    public void setIsEstimatedBuffer(Boolean isEstimatedBuffer) { this.isEstimatedBuffer = isEstimatedBuffer; }

    public Double getAreaSqKm() { return areaSqKm; }
    public void setAreaSqKm(Double areaSqKm) { this.areaSqKm = areaSqKm; }

    public Double getPerimeterKm() { return perimeterKm; }
    public void setPerimeterKm(Double perimeterKm) { this.perimeterKm = perimeterKm; }

    public long getAffectedHabitationsCount() { return affectedHabitationsCount; }
    public void setAffectedHabitationsCount(long affectedHabitationsCount) { this.affectedHabitationsCount = affectedHabitationsCount; }

    public long getAffectedTotalPopulation() { return affectedTotalPopulation; }
    public void setAffectedTotalPopulation(long affectedTotalPopulation) { this.affectedTotalPopulation = affectedTotalPopulation; }

    public long getAffectedVulnerablePopulation() { return affectedVulnerablePopulation; }
    public void setAffectedVulnerablePopulation(long affectedVulnerablePopulation) { this.affectedVulnerablePopulation = affectedVulnerablePopulation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

