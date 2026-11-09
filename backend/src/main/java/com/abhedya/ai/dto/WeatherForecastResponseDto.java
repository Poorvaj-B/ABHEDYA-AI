package com.abhedya.ai.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WeatherForecastResponseDto {

    private String habitationId;
    private String habitationName;
    private String district;
    private String state;
    private Double latitude;
    private Double longitude;

    private String dataSource;
    private boolean isStale;
    private LocalDateTime retrievedAt;

    private WeatherTelemetryDto currentTelemetry;
    private List<DailyForecastDto> dailyForecasts = new ArrayList<>();

    public WeatherForecastResponseDto() {
        this.retrievedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getHabitationId() { return habitationId; }
    public void setHabitationId(String habitationId) { this.habitationId = habitationId; }

    public String getHabitationName() { return habitationName; }
    public void setHabitationName(String habitationName) { this.habitationName = habitationName; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public boolean isStale() { return isStale; }
    public void setStale(boolean stale) { isStale = stale; }

    public LocalDateTime getRetrievedAt() { return retrievedAt; }
    public void setRetrievedAt(LocalDateTime retrievedAt) { this.retrievedAt = retrievedAt; }

    public WeatherTelemetryDto getCurrentTelemetry() { return currentTelemetry; }
    public void setCurrentTelemetry(WeatherTelemetryDto currentTelemetry) { this.currentTelemetry = currentTelemetry; }

    public List<DailyForecastDto> getDailyForecasts() { return dailyForecasts; }
    public void setDailyForecasts(List<DailyForecastDto> dailyForecasts) { this.dailyForecasts = dailyForecasts; }
}

