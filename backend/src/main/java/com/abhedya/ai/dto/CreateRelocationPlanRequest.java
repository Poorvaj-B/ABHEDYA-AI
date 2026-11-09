package com.abhedya.ai.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateRelocationPlanRequest {

    @NotBlank(message = "Source Habitation ID is required")
    private String habitationId;

    // Optional override for destination shelter; if null, algorithm selects optimal safe shelter
    private String targetShelterId;

    // Optional override for evacuee count; if null, uses excess population or vulnerable population
    private Integer evacueeCount;

    private String notes;
    private String approvedBy;

    public CreateRelocationPlanRequest() {}

    public CreateRelocationPlanRequest(String habitationId) {
        this.habitationId = habitationId;
    }

    // Getters and Setters
    public String getHabitationId() { return habitationId; }
    public void setHabitationId(String habitationId) { this.habitationId = habitationId; }

    public String getTargetShelterId() { return targetShelterId; }
    public void setTargetShelterId(String targetShelterId) { this.targetShelterId = targetShelterId; }

    public Integer getEvacueeCount() { return evacueeCount; }
    public void setEvacueeCount(Integer evacueeCount) { this.evacueeCount = evacueeCount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
}

