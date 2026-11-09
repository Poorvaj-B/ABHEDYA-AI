package com.abhedya.ai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "habitations")
public class Habitation {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "total_population", nullable = false)
    private Integer totalPopulation;

    @Column(name = "vulnerable_population", nullable = false)
    private Integer vulnerablePopulation;

    @Column(name = "safe_carrying_capacity", nullable = false)
    private Integer safeCarryingCapacity;

    @Column(name = "elevation_meters")
    private Double elevationMeters;

    @Column(name = "terrain_type", length = 100)
    private String terrainType;

    @Column(name = "in_red_zone")
    private Boolean inRedZone = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Habitation() {}

    public Habitation(String id, String name, String district, String state, Double latitude, Double longitude,
                      Integer totalPopulation, Integer vulnerablePopulation, Integer safeCarryingCapacity) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalPopulation = totalPopulation;
        this.vulnerablePopulation = vulnerablePopulation;
        this.safeCarryingCapacity = safeCarryingCapacity;
    }

    public Integer getExcessPopulation() {
        if (totalPopulation != null && safeCarryingCapacity != null) {
            return Math.max(0, totalPopulation - safeCarryingCapacity);
        }
        return 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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

    public Double getElevationMeters() { return elevationMeters; }
    public void setElevationMeters(Double elevationMeters) { this.elevationMeters = elevationMeters; }

    public String getTerrainType() { return terrainType; }
    public void setTerrainType(String terrainType) { this.terrainType = terrainType; }

    public Boolean getInRedZone() { return inRedZone; }
    public void setInRedZone(Boolean inRedZone) { this.inRedZone = inRedZone; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

