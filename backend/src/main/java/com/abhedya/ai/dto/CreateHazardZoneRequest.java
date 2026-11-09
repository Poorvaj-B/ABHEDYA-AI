package com.abhedya.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateHazardZoneRequest {

    @NotBlank(message = "Zone code is required")
    private String zoneCode;

    @NotBlank(message = "Hazard type is required")
    private String hazardType;

    @NotBlank(message = "Severity level is required")
    private String severityLevel;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "District is required")
    private String district;

    @NotNull(message = "Center latitude is required")
    private Double centerLat;

    @NotNull(message = "Center longitude is required")
    private Double centerLng;

    @NotNull(message = "Buffer radius in km is required")
    @Positive(message = "Buffer radius must be positive")
    private Double bufferRadiusKm;

    @NotBlank(message = "Boundary GeoJSON is required")
    private String boundaryGeoJson;

    private Boolean isEstimatedBuffer = false;

    public CreateHazardZoneRequest() {}

    // Getters and Setters
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

    public Boolean getIsEstimatedBuffer() { return isEstimatedBuffer; }
    public void setIsEstimatedBuffer(Boolean isEstimatedBuffer) { this.isEstimatedBuffer = isEstimatedBuffer; }
}

