package com.abhedya.ai.dto;

import java.time.LocalDateTime;

public class HealthStatusDto {

    private String status;
    private String applicationName;
    private String version;
    private String activeProfile;
    private String databaseStatus;
    private long totalHabitations;
    private long totalHazardZones;
    private long totalShelters;
    private LocalDateTime timestamp;

    public HealthStatusDto() {
        this.timestamp = LocalDateTime.now();
    }

    public HealthStatusDto(String status, String applicationName, String version, String activeProfile,
                           String databaseStatus, long totalHabitations, long totalHazardZones, long totalShelters) {
        this.status = status;
        this.applicationName = applicationName;
        this.version = version;
        this.activeProfile = activeProfile;
        this.databaseStatus = databaseStatus;
        this.totalHabitations = totalHabitations;
        this.totalHazardZones = totalHazardZones;
        this.totalShelters = totalShelters;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getActiveProfile() { return activeProfile; }
    public void setActiveProfile(String activeProfile) { this.activeProfile = activeProfile; }

    public String getDatabaseStatus() { return databaseStatus; }
    public void setDatabaseStatus(String databaseStatus) { this.databaseStatus = databaseStatus; }

    public long getTotalHabitations() { return totalHabitations; }
    public void setTotalHabitations(long totalHabitations) { this.totalHabitations = totalHabitations; }

    public long getTotalHazardZones() { return totalHazardZones; }
    public void setTotalHazardZones(long totalHazardZones) { this.totalHazardZones = totalHazardZones; }

    public long getTotalShelters() { return totalShelters; }
    public void setTotalShelters(long totalShelters) { this.totalShelters = totalShelters; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

