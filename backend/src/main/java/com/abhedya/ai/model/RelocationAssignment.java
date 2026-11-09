package com.abhedya.ai.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "relocation_assignments")
public class RelocationAssignment {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonBackReference
    private RelocationPlan plan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_habitation_id", nullable = false)
    private Habitation sourceHabitation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_shelter_id", nullable = false)
    private Shelter destinationShelter;

    @Column(name = "allocated_evacuees", nullable = false)
    private Integer allocatedEvacuees;

    @Column(name = "vulnerable_evacuees")
    private Integer vulnerableEvacuees = 0;

    @Column(name = "priority_tier", nullable = false, length = 30)
    private String priorityTier;

    @Column(name = "assignment_status", length = 50)
    private String assignmentStatus = "PENDING";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public RelocationAssignment() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public RelocationPlan getPlan() { return plan; }
    public void setPlan(RelocationPlan plan) { this.plan = plan; }

    public Habitation getSourceHabitation() { return sourceHabitation; }
    public void setSourceHabitation(Habitation sourceHabitation) { this.sourceHabitation = sourceHabitation; }

    public Shelter getDestinationShelter() { return destinationShelter; }
    public void setDestinationShelter(Shelter destinationShelter) { this.destinationShelter = destinationShelter; }

    public Integer getAllocatedEvacuees() { return allocatedEvacuees; }
    public void setAllocatedEvacuees(Integer allocatedEvacuees) { this.allocatedEvacuees = allocatedEvacuees; }

    public Integer getVulnerableEvacuees() { return vulnerableEvacuees; }
    public void setVulnerableEvacuees(Integer vulnerableEvacuees) { this.vulnerableEvacuees = vulnerableEvacuees; }

    public String getPriorityTier() { return priorityTier; }
    public void setPriorityTier(String priorityTier) { this.priorityTier = priorityTier; }

    public String getAssignmentStatus() { return assignmentStatus; }
    public void setAssignmentStatus(String assignmentStatus) { this.assignmentStatus = assignmentStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

