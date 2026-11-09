package com.abhedya.ai.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "relocation_plans")
public class RelocationPlan {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "plan_code", unique = true, nullable = false, length = 50)
    private String planCode;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "total_evacuees", nullable = false)
    private Integer totalEvacuees;

    @Column(name = "total_buses_required")
    private Integer totalBusesRequired = 0;

    @Column(name = "total_ambulances_required")
    private Integer totalAmbulancesRequired = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RelocationAssignment> assignments = new ArrayList<>();

    public RelocationPlan() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlanCode() { return planCode; }
    public void setPlanCode(String planCode) { this.planCode = planCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getTotalEvacuees() { return totalEvacuees; }
    public void setTotalEvacuees(Integer totalEvacuees) { this.totalEvacuees = totalEvacuees; }

    public Integer getTotalBusesRequired() { return totalBusesRequired; }
    public void setTotalBusesRequired(Integer totalBusesRequired) { this.totalBusesRequired = totalBusesRequired; }

    public Integer getTotalAmbulancesRequired() { return totalAmbulancesRequired; }
    public void setTotalAmbulancesRequired(Integer totalAmbulancesRequired) { this.totalAmbulancesRequired = totalAmbulancesRequired; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public List<RelocationAssignment> getAssignments() { return assignments; }
    public void setAssignments(List<RelocationAssignment> assignments) { this.assignments = assignments; }
}

