package com.abhedya.ai.dto;

import java.time.LocalDateTime;

public class WeatherAlertDto {

    private String habitationId;
    private String habitationName;
    private String district;
    private String state;
    private Double latitude;
    private Double longitude;

    private String alertTier; // ADVISORY, WATCH, WARNING, EMERGENCY
    private String alertType; // EXTREME_PRECIPITATION, HIGH_WIND, SEVERE_THUNDERSTORM, MULTI_HAZARD
    private String headline;
    private String rationale;

    private Double rainfall24hMm;
    private Double windSpeedKmh;
    private Double windGustsKmh;
    private Integer weatherCode;
    private String weatherCondition;

    private boolean isInRedZone;
    private Integer totalPopulation;
    private Integer vulnerablePopulation;
    private String dataSource;
    private LocalDateTime timestamp;

    public WeatherAlertDto() {
        this.timestamp = LocalDateTime.now();
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

    public String getAlertTier() { return alertTier; }
    public void setAlertTier(String alertTier) { this.alertTier = alertTier; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }

    public Double getRainfall24hMm() { return rainfall24hMm; }
    public void setRainfall24hMm(Double rainfall24hMm) { this.rainfall24hMm = rainfall24hMm; }

    public Double getWindSpeedKmh() { return windSpeedKmh; }
    public void setWindSpeedKmh(Double windSpeedKmh) { this.windSpeedKmh = windSpeedKmh; }

    public Double getWindGustsKmh() { return windGustsKmh; }
    public void setWindGustsKmh(Double windGustsKmh) { this.windGustsKmh = windGustsKmh; }

    public Integer getWeatherCode() { return weatherCode; }
    public void setWeatherCode(Integer weatherCode) { this.weatherCode = weatherCode; }

    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }

    public boolean isInRedZone() { return isInRedZone; }
    public void setInRedZone(boolean inRedZone) { isInRedZone = inRedZone; }

    public Integer getTotalPopulation() { return totalPopulation; }
    public void setTotalPopulation(Integer totalPopulation) { this.totalPopulation = totalPopulation; }

    public Integer getVulnerablePopulation() { return vulnerablePopulation; }
    public void setVulnerablePopulation(Integer vulnerablePopulation) { this.vulnerablePopulation = vulnerablePopulation; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

