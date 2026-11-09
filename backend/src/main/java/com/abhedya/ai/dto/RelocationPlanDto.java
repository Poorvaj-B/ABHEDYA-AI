package com.abhedya.ai.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RelocationPlanDto {

    private String planId;
    private String planCode;
    private String status;
    private Integer totalEvacuees;
    private Integer totalBusesRequired;
    private Integer totalAmbulancesRequired;
    private String notes;
    private String approvedBy;
    private LocalDateTime generatedAt;

    private List<RelocationAssignmentDto> assignments = new ArrayList<>();
    private List<EvacuationRouteDto> routes = new ArrayList<>();

    public RelocationPlanDto() {}

    // Getters and Setters
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

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

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public List<RelocationAssignmentDto> getAssignments() { return assignments; }
    public void setAssignments(List<RelocationAssignmentDto> assignments) { this.assignments = assignments; }

    public List<EvacuationRouteDto> getRoutes() { return routes; }
    public void setRoutes(List<EvacuationRouteDto> routes) { this.routes = routes; }
}

