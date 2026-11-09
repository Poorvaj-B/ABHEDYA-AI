package com.abhedya.ai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shelters")
public class Shelter {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "current_occupancy")
    private Integer currentOccupancy = 0;

    @Column(name = "available_beds", nullable = false)
    private Integer availableBeds;

    @Column(name = "potable_water_liters_day")
    private Double potableWaterLitersDay = 0.0;

    @Column(name = "has_backup_power")
    private Boolean hasBackupPower = true;

    @Column(name = "sanitation_units")
    private Integer sanitationUnits = 0;

    @Column(name = "medical_support_level", length = 50)
    private String medicalSupportLevel = "BASIC";

    @Column(length = 50)
    private String status = "OPERATIONAL";

    @Column(name = "is_verified_operational")
    private Boolean isVerifiedOperational = false;

    @Column(name = "effective_safe_capacity")
    private Integer effectiveSafeCapacity = 0;

    @Column(name = "capacity_limiting_factor", length = 50)
    private String capacityLimitingFactor = "PHYSICAL_SPACE";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Shelter() {}

    public Integer getRemainingSafeHeadroom() {
        if (maxCapacity != null && currentOccupancy != null) {
            return Math.max(0, maxCapacity - currentOccupancy);
        }
        return 0;
    }

    public Double getOccupancyRatePercent() {
        if (maxCapacity != null && maxCapacity > 0 && currentOccupancy != null) {
            return (currentOccupancy.doubleValue() / maxCapacity.doubleValue()) * 100.0;
        }
        return 0.0;
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

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public Integer getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(Integer currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    public Integer getAvailableBeds() { return availableBeds; }
    public void setAvailableBeds(Integer availableBeds) { this.availableBeds = availableBeds; }

    public Double getPotableWaterLitersDay() { return potableWaterLitersDay; }
    public void setPotableWaterLitersDay(Double potableWaterLitersDay) { this.potableWaterLitersDay = potableWaterLitersDay; }

    public Boolean getHasBackupPower() { return hasBackupPower; }
    public void setHasBackupPower(Boolean hasBackupPower) { this.hasBackupPower = hasBackupPower; }

    public Integer getSanitationUnits() { return sanitationUnits; }
    public void setSanitationUnits(Integer sanitationUnits) { this.sanitationUnits = sanitationUnits; }

    public String getMedicalSupportLevel() { return medicalSupportLevel; }
    public void setMedicalSupportLevel(String medicalSupportLevel) { this.medicalSupportLevel = medicalSupportLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getIsVerifiedOperational() { return isVerifiedOperational; }
    public void setIsVerifiedOperational(Boolean isVerifiedOperational) { this.isVerifiedOperational = isVerifiedOperational; }

    public Integer getEffectiveSafeCapacity() { return effectiveSafeCapacity; }
    public void setEffectiveSafeCapacity(Integer effectiveSafeCapacity) { this.effectiveSafeCapacity = effectiveSafeCapacity; }

    public String getCapacityLimitingFactor() { return capacityLimitingFactor; }
    public void setCapacityLimitingFactor(String capacityLimitingFactor) { this.capacityLimitingFactor = capacityLimitingFactor; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

