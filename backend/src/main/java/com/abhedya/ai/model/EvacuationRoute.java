package com.abhedya.ai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evacuation_routes")
public class EvacuationRoute {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private RelocationPlan plan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_habitation_id", nullable = false)
    private Habitation sourceHabitation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_shelter_id", nullable = false)
    private Shelter destinationShelter;

    @Column(name = "route_geojson", nullable = false, columnDefinition = "TEXT")
    private String routeGeoJson;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @Column(name = "duration_minutes", nullable = false)
    private Double durationMinutes;

    @Column(name = "avoids_red_zones")
    private Boolean avoidsRedZones = true;

    @Column(name = "validated_safe")
    private Boolean validatedSafe = false;

    @Column(name = "safety_notes", columnDefinition = "TEXT")
    private String safetyNotes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public EvacuationRoute() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public RelocationPlan getPlan() { return plan; }
    public void setPlan(RelocationPlan plan) { this.plan = plan; }

    public Habitation getSourceHabitation() { return sourceHabitation; }
    public void setSourceHabitation(Habitation sourceHabitation) { this.sourceHabitation = sourceHabitation; }

    public Shelter getDestinationShelter() { return destinationShelter; }
    public void setDestinationShelter(Shelter destinationShelter) { this.destinationShelter = destinationShelter; }

    public String getRouteGeoJson() { return routeGeoJson; }
    public void setRouteGeoJson(String routeGeoJson) { this.routeGeoJson = routeGeoJson; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Double durationMinutes) { this.durationMinutes = durationMinutes; }

    public Boolean getAvoidsRedZones() { return avoidsRedZones; }
    public void setAvoidsRedZones(Boolean avoidsRedZones) { this.avoidsRedZones = avoidsRedZones; }

    public Boolean getValidatedSafe() { return validatedSafe; }
    public void setValidatedSafe(Boolean validatedSafe) { this.validatedSafe = validatedSafe; }

    public String getSafetyNotes() { return safetyNotes; }
    public void setSafetyNotes(String safetyNotes) { this.safetyNotes = safetyNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

