package com.abhedya.ai.dto;

import java.time.LocalDateTime;

public class WeatherTelemetryDto {

    private String habitationId;
    private String habitationName;
    private String district;
    private String state;
    private Double latitude;
    private Double longitude;
    private Double elevationMeters;

    private String timestamp; // Observation ISO timestamp
    private Double temperatureC;
    private Double relativeHumidityPercent;
    private Double currentPrecipitationMm;
    private Double rainMm;
    private Double precipitationLast24hMm;
    private Double precipitationNext24hMm;
    private Double windSpeedKmh;
    private Double windGustsKmh;

    private Integer weatherCode;
    private String weatherCondition;
    private String alertTier; // NONE, ADVISORY, WATCH, WARNING, EMERGENCY
    private String alertHeadline;
    private String alertRationale;

    private String dataSource; // LIVE_OPEN_METEO, CACHED_TELEMETRY, FALLBACK_BASELINE
    private boolean isStale;
    private Long cacheTtlSecondsRemaining;
    private LocalDateTime retrievedAt;
    private String fallbackReason;

    public WeatherTelemetryDto() {
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

    public Double getElevationMeters() { return elevationMeters; }
    public void setElevationMeters(Double elevationMeters) { this.elevationMeters = elevationMeters; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Double getTemperatureC() { return temperatureC; }
    public void setTemperatureC(Double temperatureC) { this.temperatureC = temperatureC; }

    public Double getRelativeHumidityPercent() { return relativeHumidityPercent; }
    public void setRelativeHumidityPercent(Double relativeHumidityPercent) { this.relativeHumidityPercent = relativeHumidityPercent; }

    public Double getCurrentPrecipitationMm() { return currentPrecipitationMm; }
    public void setCurrentPrecipitationMm(Double currentPrecipitationMm) { this.currentPrecipitationMm = currentPrecipitationMm; }

    public Double getRainMm() { return rainMm; }
    public void setRainMm(Double rainMm) { this.rainMm = rainMm; }

    public Double getPrecipitationLast24hMm() { return precipitationLast24hMm; }
    public void setPrecipitationLast24hMm(Double precipitationLast24hMm) { this.precipitationLast24hMm = precipitationLast24hMm; }

    public Double getPrecipitationNext24hMm() { return precipitationNext24hMm; }
    public void setPrecipitationNext24hMm(Double precipitationNext24hMm) { this.precipitationNext24hMm = precipitationNext24hMm; }

    public Double getWindSpeedKmh() { return windSpeedKmh; }
    public void setWindSpeedKmh(Double windSpeedKmh) { this.windSpeedKmh = windSpeedKmh; }

    public Double getWindGustsKmh() { return windGustsKmh; }
    public void setWindGustsKmh(Double windGustsKmh) { this.windGustsKmh = windGustsKmh; }

    public Integer getWeatherCode() { return weatherCode; }
    public void setWeatherCode(Integer weatherCode) { this.weatherCode = weatherCode; }

    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }

    public String getAlertTier() { return alertTier; }
    public void setAlertTier(String alertTier) { this.alertTier = alertTier; }

    public String getAlertHeadline() { return alertHeadline; }
    public void setAlertHeadline(String alertHeadline) { this.alertHeadline = alertHeadline; }

    public String getAlertRationale() { return alertRationale; }
    public void setAlertRationale(String alertRationale) { this.alertRationale = alertRationale; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public boolean isStale() { return isStale; }
    public void setStale(boolean stale) { isStale = stale; }

    public Long getCacheTtlSecondsRemaining() { return cacheTtlSecondsRemaining; }
    public void setCacheTtlSecondsRemaining(Long cacheTtlSecondsRemaining) { this.cacheTtlSecondsRemaining = cacheTtlSecondsRemaining; }

    public LocalDateTime getRetrievedAt() { return retrievedAt; }
    public void setRetrievedAt(LocalDateTime retrievedAt) { this.retrievedAt = retrievedAt; }

    public String getFallbackReason() { return fallbackReason; }
    public void setFallbackReason(String fallbackReason) { this.fallbackReason = fallbackReason; }
}

