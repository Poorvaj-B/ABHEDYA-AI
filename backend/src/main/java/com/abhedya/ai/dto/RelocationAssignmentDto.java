package com.abhedya.ai.dto;

import java.time.LocalDateTime;

public class RelocationAssignmentDto {

    private String assignmentId;
    private String planId;
    private String sourceHabitationId;
    private String sourceHabitationName;
    private String destinationShelterId;
    private String destinationShelterName;
    private Integer allocatedEvacuees;
    private Integer vulnerableEvacuees;
    private String priorityTier;
    private String assignmentStatus;
    private Double distanceKm;
    private Double durationMinutes;
    private Integer busesRequired;
    private Integer ambulancesRequired;
    private LocalDateTime createdAt;

    public RelocationAssignmentDto() {}

    // Getters and Setters
    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getSourceHabitationId() { return sourceHabitationId; }
    public void setSourceHabitationId(String sourceHabitationId) { this.sourceHabitationId = sourceHabitationId; }

    public String getSourceHabitationName() { return sourceHabitationName; }
    public void setSourceHabitationName(String sourceHabitationName) { this.sourceHabitationName = sourceHabitationName; }

    public String getDestinationShelterId() { return destinationShelterId; }
    public void setDestinationShelterId(String destinationShelterId) { this.destinationShelterId = destinationShelterId; }

    public String getDestinationShelterName() { return destinationShelterName; }
    public void setDestinationShelterName(String destinationShelterName) { this.destinationShelterName = destinationShelterName; }

    public Integer getAllocatedEvacuees() { return allocatedEvacuees; }
    public void setAllocatedEvacuees(Integer allocatedEvacuees) { this.allocatedEvacuees = allocatedEvacuees; }

    public Integer getVulnerableEvacuees() { return vulnerableEvacuees; }
    public void setVulnerableEvacuees(Integer vulnerableEvacuees) { this.vulnerableEvacuees = vulnerableEvacuees; }

    public String getPriorityTier() { return priorityTier; }
    public void setPriorityTier(String priorityTier) { this.priorityTier = priorityTier; }

    public String getAssignmentStatus() { return assignmentStatus; }
    public void setAssignmentStatus(String assignmentStatus) { this.assignmentStatus = assignmentStatus; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Double durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getBusesRequired() { return busesRequired; }
    public void setBusesRequired(Integer busesRequired) { this.busesRequired = busesRequired; }

    public Integer getAmbulancesRequired() { return ambulancesRequired; }
    public void setAmbulancesRequired(Integer ambulancesRequired) { this.ambulancesRequired = ambulancesRequired; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

