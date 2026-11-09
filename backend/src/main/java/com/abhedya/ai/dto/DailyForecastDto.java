package com.abhedya.ai.dto;

public class DailyForecastDto {

    private String date; // YYYY-MM-DD
    private Double precipitationSumMm;
    private Double windSpeedMaxKmh;
    private Double temperatureMaxC;
    private Double temperatureMinC;
    private Integer weatherCode;
    private String weatherCondition;
    private String alertTier;

    public DailyForecastDto() {}

    public DailyForecastDto(String date, Double precipitationSumMm, Double windSpeedMaxKmh,
                            Double temperatureMaxC, Double temperatureMinC, Integer weatherCode,
                            String weatherCondition, String alertTier) {
        this.date = date;
        this.precipitationSumMm = precipitationSumMm;
        this.windSpeedMaxKmh = windSpeedMaxKmh;
        this.temperatureMaxC = temperatureMaxC;
        this.temperatureMinC = temperatureMinC;
        this.weatherCode = weatherCode;
        this.weatherCondition = weatherCondition;
        this.alertTier = alertTier;
    }

    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Double getPrecipitationSumMm() { return precipitationSumMm; }
    public void setPrecipitationSumMm(Double precipitationSumMm) { this.precipitationSumMm = precipitationSumMm; }

    public Double getWindSpeedMaxKmh() { return windSpeedMaxKmh; }
    public void setWindSpeedMaxKmh(Double windSpeedMaxKmh) { this.windSpeedMaxKmh = windSpeedMaxKmh; }

    public Double getTemperatureMaxC() { return temperatureMaxC; }
    public void setTemperatureMaxC(Double temperatureMaxC) { this.temperatureMaxC = temperatureMaxC; }

    public Double getTemperatureMinC() { return temperatureMinC; }
    public void setTemperatureMinC(Double temperatureMinC) { this.temperatureMinC = temperatureMinC; }

    public Integer getWeatherCode() { return weatherCode; }
    public void setWeatherCode(Integer weatherCode) { this.weatherCode = weatherCode; }

    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }

    public String getAlertTier() { return alertTier; }
    public void setAlertTier(String alertTier) { this.alertTier = alertTier; }
}

