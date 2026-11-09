package com.abhedya.ai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hazard_zones")
public class HazardZone {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "zone_code", unique = true, nullable = false, length = 50)
    private String zoneCode;

    @Column(name = "hazard_type", nullable = false, length = 50)
    private String hazardType;

    @Column(name = "severity_level", nullable = false, length = 30)
    private String severityLevel;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(name = "center_lat", nullable = false)
    private Double centerLat;

    @Column(name = "center_lng", nullable = false)
    private Double centerLng;

    @Column(name = "buffer_radius_km", nullable = false)
    private Double bufferRadiusKm;

    @Column(name = "boundary_geojson", nullable = false, columnDefinition = "TEXT")
    private String boundaryGeoJson;

    @Column(name = "is_estimated_buffer")
    private Boolean isEstimatedBuffer = false;

    @Column(name = "perimeter_km")
    private Double perimeterKm = 0.0;

    @Column(name = "area_sq_km")
    private Double areaSqKm = 0.0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public HazardZone() {}

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

    public Boolean getIsEstimatedBuffer() { return isEstimatedBuffer; }
    public void setIsEstimatedBuffer(Boolean isEstimatedBuffer) { this.isEstimatedBuffer = isEstimatedBuffer; }

    public Double getPerimeterKm() { return perimeterKm; }
    public void setPerimeterKm(Double perimeterKm) { this.perimeterKm = perimeterKm; }

    public Double getAreaSqKm() { return areaSqKm; }
    public void setAreaSqKm(Double areaSqKm) { this.areaSqKm = areaSqKm; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

