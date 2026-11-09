package com.abhedya.ai.dto;

import java.time.LocalDateTime;

public class EvacuationRouteDto {

    private String routeId;
    private String planId;
    private String planCode;

    private String sourceHabitationId;
    private String sourceHabitationName;
    private Double sourceLat;
    private Double sourceLng;

    private String destinationShelterId;
    private String destinationShelterName;
    private Double destinationLat;
    private Double destinationLng;

    private Double distanceKm;
    private Double durationMinutes;
    private String routeGeoJson; // GeoJSON LineString
    private String routeSource; // OSRM_ROAD_NETWORK, GEODESIC_FALLBACK
    private Boolean avoidsRedZones;
    private Boolean validatedSafe;
    private String safetyNotes;
    private LocalDateTime createdAt;

    public EvacuationRouteDto() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getPlanCode() { return planCode; }
    public void setPlanCode(String planCode) { this.planCode = planCode; }

    public String getSourceHabitationId() { return sourceHabitationId; }
    public void setSourceHabitationId(String sourceHabitationId) { this.sourceHabitationId = sourceHabitationId; }

    public String getSourceHabitationName() { return sourceHabitationName; }
    public void setSourceHabitationName(String sourceHabitationName) { this.sourceHabitationName = sourceHabitationName; }

    public Double getSourceLat() { return sourceLat; }
    public void setSourceLat(Double sourceLat) { this.sourceLat = sourceLat; }

    public Double getSourceLng() { return sourceLng; }
    public void setSourceLng(Double sourceLng) { this.sourceLng = sourceLng; }

    public String getDestinationShelterId() { return destinationShelterId; }
    public void setDestinationShelterId(String destinationShelterId) { this.destinationShelterId = destinationShelterId; }

    public String getDestinationShelterName() { return destinationShelterName; }
    public void setDestinationShelterName(String destinationShelterName) { this.destinationShelterName = destinationShelterName; }

    public Double getDestinationLat() { return destinationLat; }
    public void setDestinationLat(Double destinationLat) { this.destinationLat = destinationLat; }

    public Double getDestinationLng() { return destinationLng; }
    public void setDestinationLng(Double destinationLng) { this.destinationLng = destinationLng; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Double durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getRouteGeoJson() { return routeGeoJson; }
    public void setRouteGeoJson(String routeGeoJson) { this.routeGeoJson = routeGeoJson; }

    public String getRouteSource() { return routeSource; }
    public void setRouteSource(String routeSource) { this.routeSource = routeSource; }

    public Boolean getAvoidsRedZones() { return avoidsRedZones; }
    public void setAvoidsRedZones(Boolean avoidsRedZones) { this.avoidsRedZones = avoidsRedZones; }

    public Boolean getValidatedSafe() { return validatedSafe; }
    public void setValidatedSafe(Boolean validatedSafe) { this.validatedSafe = validatedSafe; }

    public String getSafetyNotes() { return safetyNotes; }
    public void setSafetyNotes(String safetyNotes) { this.safetyNotes = safetyNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

