package com.abhedya.ai.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class UpdateShelterOccupancyRequest {

    @NotNull(message = "New occupancy count is required")
    @PositiveOrZero(message = "Occupancy count cannot be negative")
    private Integer newOccupancy;

    public UpdateShelterOccupancyRequest() {}

    public UpdateShelterOccupancyRequest(Integer newOccupancy) {
        this.newOccupancy = newOccupancy;
    }

    public Integer getNewOccupancy() { return newOccupancy; }
    public void setNewOccupancy(Integer newOccupancy) { this.newOccupancy = newOccupancy; }
}
