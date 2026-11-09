package com.abhedya.ai.dto;

public class ShelterCapacityAuditDto {

    private String shelterId;
    private String name;
    private String district;
    private String state;
    private Double latitude;
    private Double longitude;

    // Physical Footprint Capacity
    private Integer maxCapacity;
    private Integer currentOccupancy;
    private Integer availableBeds;

    // Resource Supported Capacities (Sphere Project Standards)
    private Double potableWaterLitersDay;
    private Integer waterSupportedCapacity; // @ 15 L/person/day
    private Integer sanitationUnits;
    private Integer sanitationSupportedCapacity; // @ 20 persons/unit
    private String medicalSupportLevel;
    private Boolean hasBackupPower;

    // Certified Safe Limits
    private Integer effectiveSafeCapacity;
    private String capacityLimitingFactor; // PHYSICAL_SPACE, WATER_SUPPLY, SANITATION
    private Integer remainingSafeHeadroom;
    private Double occupancyRatePercent;
    private String occupancyStressLevel; // OPTIMAL, CONGESTED, CRITICAL, EXCEEDED_UNSAFE
    private Boolean isVerifiedOperational;
    private Boolean isIntakeAllowed;

    public ShelterCapacityAuditDto() {}

    // Getters and Setters
    public String getShelterId() { return shelterId; }
    public void setShelterId(String shelterId) { this.shelterId = shelterId; }

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

    public Integer getWaterSupportedCapacity() { return waterSupportedCapacity; }
    public void setWaterSupportedCapacity(Integer waterSupportedCapacity) { this.waterSupportedCapacity = waterSupportedCapacity; }

    public Integer getSanitationUnits() { return sanitationUnits; }
    public void setSanitationUnits(Integer sanitationUnits) { this.sanitationUnits = sanitationUnits; }

    public Integer getSanitationSupportedCapacity() { return sanitationSupportedCapacity; }
    public void setSanitationSupportedCapacity(Integer sanitationSupportedCapacity) { this.sanitationSupportedCapacity = sanitationSupportedCapacity; }

    public String getMedicalSupportLevel() { return medicalSupportLevel; }
    public void setMedicalSupportLevel(String medicalSupportLevel) { this.medicalSupportLevel = medicalSupportLevel; }

    public Boolean getHasBackupPower() { return hasBackupPower; }
    public void setHasBackupPower(Boolean hasBackupPower) { this.hasBackupPower = hasBackupPower; }

    public Integer getEffectiveSafeCapacity() { return effectiveSafeCapacity; }
    public void setEffectiveSafeCapacity(Integer effectiveSafeCapacity) { this.effectiveSafeCapacity = effectiveSafeCapacity; }

    public String getCapacityLimitingFactor() { return capacityLimitingFactor; }
    public void setCapacityLimitingFactor(String capacityLimitingFactor) { this.capacityLimitingFactor = capacityLimitingFactor; }

    public Integer getRemainingSafeHeadroom() { return remainingSafeHeadroom; }
    public void setRemainingSafeHeadroom(Integer remainingSafeHeadroom) { this.remainingSafeHeadroom = remainingSafeHeadroom; }

    public Double getOccupancyRatePercent() { return occupancyRatePercent; }
    public void setOccupancyRatePercent(Double occupancyRatePercent) { this.occupancyRatePercent = occupancyRatePercent; }

    public String getOccupancyStressLevel() { return occupancyStressLevel; }
    public void setOccupancyStressLevel(String occupancyStressLevel) { this.occupancyStressLevel = occupancyStressLevel; }

    public Boolean getIsVerifiedOperational() { return isVerifiedOperational; }
    public void setIsVerifiedOperational(Boolean isVerifiedOperational) { this.isVerifiedOperational = isVerifiedOperational; }

    public Boolean getIsIntakeAllowed() { return isIntakeAllowed; }
    public void setIsIntakeAllowed(Boolean isIntakeAllowed) { this.isIntakeAllowed = isIntakeAllowed; }
}
