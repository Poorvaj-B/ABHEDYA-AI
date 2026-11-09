package com.abhedya.ai.client;

import com.abhedya.ai.dto.DailyForecastDto;
import com.abhedya.ai.dto.WeatherForecastResponseDto;
import com.abhedya.ai.dto.WeatherTelemetryDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class OpenMeteoClient {

    private static final Logger log = LoggerFactory.getLogger(OpenMeteoClient.class);

    private final String baseUrl;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;
    private final int maxRetries;
    private final long retryBackoffMs;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Configurable Alert Thresholds
    private final double rainfallAdvisoryMm;
    private final double rainfallWatchMm;
    private final double rainfallWarningMm;
    private final double rainfallEmergencyMm;
    private final double windAdvisoryKmh;
    private final double windWatchKmh;
    private final double windWarningKmh;
    private final double windEmergencyKmh;

    public OpenMeteoClient(
            @Value("${app.weather.open-meteo-base-url:https://api.open-meteo.com/v1/forecast}") String baseUrl,
            @Value("${app.weather.connect-timeout-ms:4000}") int connectTimeoutMs,
            @Value("${app.weather.read-timeout-ms:5000}") int readTimeoutMs,
            @Value("${app.weather.max-retries:2}") int maxRetries,
            @Value("${app.weather.retry-backoff-ms:500}") long retryBackoffMs,
            @Value("${app.weather.thresholds.rainfall-advisory-mm:25.0}") double rainfallAdvisoryMm,
            @Value("${app.weather.thresholds.rainfall-watch-mm:64.5}") double rainfallWatchMm,
            @Value("${app.weather.thresholds.rainfall-warning-mm:115.5}") double rainfallWarningMm,
            @Value("${app.weather.thresholds.rainfall-emergency-mm:204.5}") double rainfallEmergencyMm,
            @Value("${app.weather.thresholds.wind-advisory-kmh:40.0}") double windAdvisoryKmh,
            @Value("${app.weather.thresholds.wind-watch-kmh:50.0}") double windWatchKmh,
            @Value("${app.weather.thresholds.wind-warning-kmh:75.0}") double windWarningKmh,
            @Value("${app.weather.thresholds.wind-emergency-kmh:90.0}") double windEmergencyKmh,
            ObjectMapper objectMapper) {

        this.baseUrl = baseUrl;
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
        this.maxRetries = maxRetries;
        this.retryBackoffMs = retryBackoffMs;

        this.rainfallAdvisoryMm = rainfallAdvisoryMm;
        this.rainfallWatchMm = rainfallWatchMm;
        this.rainfallWarningMm = rainfallWarningMm;
        this.rainfallEmergencyMm = rainfallEmergencyMm;
        this.windAdvisoryKmh = windAdvisoryKmh;
        this.windWatchKmh = windWatchKmh;
        this.windWarningKmh = windWarningKmh;
        this.windEmergencyKmh = windEmergencyKmh;

        this.objectMapper = objectMapper;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Fetches raw weather data from Open-Meteo with retry and graceful fallback.
     */
    public WeatherForecastResponseDto fetchWeatherForecast(double latitude, double longitude) {
        String url = String.format(Locale.US,
                "%s?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,precipitation,rain,weather_code,wind_speed_10m,wind_gusts_10m&daily=precipitation_sum,wind_speed_10m_max,temperature_2m_max,temperature_2m_min,weather_code&past_days=1&forecast_days=3&timezone=auto",
                baseUrl, latitude, longitude);

        int attempts = 0;
        Exception lastException = null;

        while (attempts <= maxRetries) {
            attempts++;
            try {
                log.debug("Querying Open-Meteo API (attempt {}/{}): {}", attempts, maxRetries + 1, url);
                String rawJson = restTemplate.getForObject(url, String.class);
                if (rawJson != null && !rawJson.isBlank()) {
                    JsonNode root = objectMapper.readTree(rawJson);
                    return parseForecastResponse(root, latitude, longitude, "LIVE_OPEN_METEO", false, null);
                }
            } catch (RestClientException e) {
                lastException = e;
                log.warn("Open-Meteo API query attempt {} failed: {}", attempts, e.getMessage());
                if (attempts <= maxRetries) {
                    try {
                        Thread.sleep(retryBackoffMs * attempts);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } catch (Exception e) {
                lastException = e;
                log.error("Unexpected error processing Open-Meteo response: {}", e.getMessage(), e);
                break;
            }
        }

        log.warn("All {} attempts to contact Open-Meteo API failed. Activating deterministic fallback baseline.", attempts);
        String reason = lastException != null ? lastException.getMessage() : "Connection timeout / network unavailable";
        return buildFallbackForecastResponse(latitude, longitude, reason);
    }

    /**
     * Parses the Open-Meteo JSON payload into typed telemetry and daily forecasts.
     */
    private WeatherForecastResponseDto parseForecastResponse(
            JsonNode root, double latitude, double longitude,
            String dataSource, boolean isStale, String fallbackReason) {

        WeatherForecastResponseDto response = new WeatherForecastResponseDto();
        response.setLatitude(latitude);
        response.setLongitude(longitude);
        response.setDataSource(dataSource);
        response.setStale(isStale);
        response.setRetrievedAt(LocalDateTime.now());

        WeatherTelemetryDto current = new WeatherTelemetryDto();
        current.setLatitude(latitude);
        current.setLongitude(longitude);
        current.setDataSource(dataSource);
        current.setStale(isStale);
        current.setRetrievedAt(LocalDateTime.now());
        current.setFallbackReason(fallbackReason);

        if (root.has("elevation")) {
            current.setElevationMeters(root.get("elevation").asDouble());
        }

        JsonNode currentNode = root.path("current");
        if (!currentNode.isMissingNode()) {
            current.setTimestamp(currentNode.path("time").asText(LocalDateTime.now().toString()));
            current.setTemperatureC(round1(currentNode.path("temperature_2m").asDouble(25.0)));
            current.setRelativeHumidityPercent(round1(currentNode.path("relative_humidity_2m").asDouble(65.0)));
            current.setCurrentPrecipitationMm(round1(currentNode.path("precipitation").asDouble(0.0)));
            current.setRainMm(round1(currentNode.path("rain").asDouble(0.0)));
            current.setWindSpeedKmh(round1(currentNode.path("wind_speed_10m").asDouble(10.0)));
            current.setWindGustsKmh(round1(currentNode.path("wind_gusts_10m").asDouble(18.0)));

            int code = currentNode.path("weather_code").asInt(0);
            current.setWeatherCode(code);
            current.setWeatherCondition(interpretWeatherCode(code));
        }

        // Daily arrays: index 0 = yesterday, index 1 = today, index 2 = tomorrow, index 3 = day after tomorrow
        JsonNode daily = root.path("daily");
        List<DailyForecastDto> forecastList = new ArrayList<>();

        double past24hPrecipitation = 0.0;
        double next24hPrecipitation = 0.0;

        if (!daily.isMissingNode() && daily.has("time") && daily.get("time").isArray()) {
            JsonNode times = daily.get("time");
            JsonNode precipSums = daily.path("precipitation_sum");
            JsonNode windMaxs = daily.path("wind_speed_10m_max");
            JsonNode tempMaxs = daily.path("temperature_2m_max");
            JsonNode tempMins = daily.path("temperature_2m_min");
            JsonNode weatherCodes = daily.path("weather_code");

            int size = times.size();

            // Compute past 24h precipitation from yesterday and today
            if (size > 1 && precipSums.isArray()) {
                double todayPrecip = precipSums.get(1).asDouble(0.0);
                double yestPrecip = precipSums.get(0).asDouble(0.0);
                // Combined recent precipitation impact
                past24hPrecipitation = round1(Math.max(todayPrecip, yestPrecip));
            } else if (size > 0 && precipSums.isArray()) {
                past24hPrecipitation = round1(precipSums.get(0).asDouble(0.0));
            }

            // Next 24h precipitation (tomorrow's forecast: index 2 or 1)
            if (size > 2 && precipSums.isArray()) {
                next24hPrecipitation = round1(precipSums.get(2).asDouble(0.0));
            } else if (size > 1 && precipSums.isArray()) {
                next24hPrecipitation = round1(precipSums.get(1).asDouble(0.0));
            }

            // Populate daily forecast items starting from today (index >= 1)
            int startIndex = size > 1 ? 1 : 0;
            for (int i = startIndex; i < size; i++) {
                String date = times.get(i).asText();
                double pSum = precipSums.isArray() && i < precipSums.size() ? round1(precipSums.get(i).asDouble(0.0)) : 0.0;
                double wMax = windMaxs.isArray() && i < windMaxs.size() ? round1(windMaxs.get(i).asDouble(0.0)) : 0.0;
                double tMax = tempMaxs.isArray() && i < tempMaxs.size() ? round1(tempMaxs.get(i).asDouble(28.0)) : 28.0;
                double tMin = tempMins.isArray() && i < tempMins.size() ? round1(tempMins.get(i).asDouble(20.0)) : 20.0;
                int wCode = weatherCodes.isArray() && i < weatherCodes.size() ? weatherCodes.get(i).asInt(0) : 0;
                String condition = interpretWeatherCode(wCode);
                String dayAlert = evaluateAlertTier(pSum, wMax, wCode);

                forecastList.add(new DailyForecastDto(date, pSum, wMax, tMax, tMin, wCode, condition, dayAlert));
            }
        }

        current.setPrecipitationLast24hMm(past24hPrecipitation);
        current.setPrecipitationNext24hMm(next24hPrecipitation);

        // Evaluate Alert Tier and Rationale
        evaluateAlert(current);

        response.setCurrentTelemetry(current);
        response.setDailyForecasts(forecastList);
        return response;
    }

    /**
     * Builds a safe deterministic fallback when external weather network is unreachable.
     */
    public WeatherForecastResponseDto buildFallbackForecastResponse(double latitude, double longitude, String reason) {
        WeatherForecastResponseDto response = new WeatherForecastResponseDto();
        response.setLatitude(latitude);
        response.setLongitude(longitude);
        response.setDataSource("FALLBACK_BASELINE");
        response.setStale(true);
        response.setRetrievedAt(LocalDateTime.now());

        WeatherTelemetryDto current = new WeatherTelemetryDto();
        current.setLatitude(latitude);
        current.setLongitude(longitude);
        current.setDataSource("FALLBACK_BASELINE");
        current.setStale(true);
        current.setRetrievedAt(LocalDateTime.now());
        current.setFallbackReason(reason);

        current.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        current.setTemperatureC(24.5);
        current.setRelativeHumidityPercent(72.0);
        current.setCurrentPrecipitationMm(0.0);
        current.setRainMm(0.0);
        current.setPrecipitationLast24hMm(5.0);
        current.setPrecipitationNext24hMm(3.0);
        current.setWindSpeedKmh(14.0);
        current.setWindGustsKmh(22.0);
        current.setWeatherCode(2);
        current.setWeatherCondition("Partly Cloudy (Baseline Estimation)");

        current.setAlertTier("NONE");
        current.setAlertHeadline("Normal Weather Conditions (Fallback Telemetry)");
        current.setAlertRationale("Deterministic baseline weather applied due to temporary external network isolation: " + reason);

        response.setCurrentTelemetry(current);

        List<DailyForecastDto> forecasts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 3; i++) {
            String date = now.plusDays(i).toLocalDate().toString();
            forecasts.add(new DailyForecastDto(
                    date,
                    4.0 + (i * 2.0),
                    15.0 + (i * 1.5),
                    29.0,
                    21.0,
                    2,
                    "Partly Cloudy",
                    "NONE"
            ));
        }
        response.setDailyForecasts(forecasts);

        return response;
    }

    /**
     * Evaluates alert tier, headline and rationale based on IMD meteorological thresholds.
     */
    public void evaluateAlert(WeatherTelemetryDto telemetry) {
        double precip = Math.max(
                telemetry.getPrecipitationLast24hMm() != null ? telemetry.getPrecipitationLast24hMm() : 0.0,
                telemetry.getCurrentPrecipitationMm() != null ? telemetry.getCurrentPrecipitationMm() : 0.0
        );
        double wind = telemetry.getWindSpeedKmh() != null ? telemetry.getWindSpeedKmh() : 0.0;
        double gusts = telemetry.getWindGustsKmh() != null ? telemetry.getWindGustsKmh() : 0.0;
        int code = telemetry.getWeatherCode() != null ? telemetry.getWeatherCode() : 0;

        String tier = evaluateAlertTier(precip, Math.max(wind, gusts * 0.8), code);
        telemetry.setAlertTier(tier);

        StringBuilder headline = new StringBuilder();
        StringBuilder rationale = new StringBuilder();

        switch (tier) {
            case "EMERGENCY" -> {
                headline.append("RED ALERT — EXTREME WEATHER EMERGENCY");
                if (precip >= rainfallEmergencyMm) {
                    rationale.append(String.format(Locale.US, "Extremely heavy rainfall detected (%.1fmm >= %.1fmm threshold). Imminent flash flood / landslide hazard. ", precip, rainfallEmergencyMm));
                }
                if (wind >= windEmergencyKmh) {
                    rationale.append(String.format(Locale.US, "Cyclonic storm-force wind speeds detected (%.1f km/h >= %.1f km/h threshold). Severe structural danger. ", wind, windEmergencyKmh));
                }
                if (code == 96 || code == 99) {
                    rationale.append("Severe thunderstorm with heavy damaging hail in progress. ");
                }
            }
            case "WARNING" -> {
                headline.append("ORANGE ALERT — SEVERE WEATHER WARNING");
                if (precip >= rainfallWarningMm) {
                    rationale.append(String.format(Locale.US, "Very heavy rainfall detected (%.1fmm >= %.1fmm threshold). Risk of localized inundation and slope instability. ", precip, rainfallWarningMm));
                }
                if (wind >= windWarningKmh) {
                    rationale.append(String.format(Locale.US, "Gale-force wind speeds detected (%.1f km/h >= %.1f km/h threshold). Tree fall and power interruption hazard. ", wind, windWarningKmh));
                }
                if (code == 95) {
                    rationale.append("Active thunderstorm activity detected. ");
                }
            }
            case "WATCH" -> {
                headline.append("YELLOW ALERT — WEATHER WATCH IN EFFECT");
                if (precip >= rainfallWatchMm) {
                    rationale.append(String.format(Locale.US, "Heavy rainfall detected (%.1fmm >= %.1fmm threshold). Habitations in drainage corridors must be monitored. ", precip, rainfallWatchMm));
                }
                if (wind >= windWatchKmh) {
                    rationale.append(String.format(Locale.US, "Strong squally winds (%.1f km/h >= %.1f km/h threshold). High-risk habitations should secure temporary shelters. ", wind, windWatchKmh));
                }
            }
            case "ADVISORY" -> {
                headline.append("BLUE ADVISORY — WEATHER ELEVATION");
                if (precip >= rainfallAdvisoryMm) {
                    rationale.append(String.format(Locale.US, "Moderate precipitation (%.1fmm). Soil moisture saturation increasing. ", precip));
                }
                if (wind >= windAdvisoryKmh) {
                    rationale.append(String.format(Locale.US, "Breezy conditions (%.1f km/h). Routine operations acceptable. ", wind));
                }
            }
            default -> {
                headline.append("NORMAL METEOROLOGICAL CONDITIONS");
                rationale.append("Weather parameters within normal seasonal thresholds. No active alerts.");
            }
        }

        telemetry.setAlertHeadline(headline.toString());
        telemetry.setAlertRationale(rationale.toString());
    }

    /**
     * Determines the alert tier string from parameters according to IMD standards.
     */
    public String evaluateAlertTier(double precipitationMm, double windSpeedKmh, int weatherCode) {
        if (precipitationMm >= rainfallEmergencyMm || windSpeedKmh >= windEmergencyKmh ||
                ((weatherCode == 96 || weatherCode == 99) && precipitationMm >= rainfallWatchMm)) {
            return "EMERGENCY";
        }
        if (precipitationMm >= rainfallWarningMm || windSpeedKmh >= windWarningKmh || weatherCode == 95 || weatherCode == 96 || weatherCode == 99) {
            return "WARNING";
        }
        if (precipitationMm >= rainfallWatchMm || windSpeedKmh >= windWatchKmh || weatherCode == 65 || weatherCode == 82) {
            return "WATCH";
        }
        if (precipitationMm >= rainfallAdvisoryMm || windSpeedKmh >= windAdvisoryKmh || weatherCode == 63 || weatherCode == 81) {
            return "ADVISORY";
        }
        return "NONE";
    }

    /**
     * Maps WMO Weather Interpretation Codes (0-99) to descriptive English text.
     */
    public String interpretWeatherCode(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1 -> "Mainly clear";
            case 2 -> "Partly cloudy";
            case 3 -> "Overcast";
            case 45 -> "Fog";
            case 48 -> "Depositing rime fog";
            case 51 -> "Light drizzle";
            case 53 -> "Moderate drizzle";
            case 55 -> "Dense intensity drizzle";
            case 56, 57 -> "Freezing drizzle";
            case 61 -> "Slight rain";
            case 63 -> "Moderate rain";
            case 65 -> "Heavy rain";
            case 66, 67 -> "Freezing rain";
            case 71 -> "Slight snow fall";
            case 73 -> "Moderate snow fall";
            case 75 -> "Heavy snow fall";
            case 77 -> "Snow grains";
            case 80 -> "Slight rain showers";
            case 81 -> "Moderate rain showers";
            case 82 -> "Violent rain showers";
            case 85 -> "Slight snow showers";
            case 86 -> "Heavy snow showers";
            case 95 -> "Thunderstorm";
            case 96 -> "Thunderstorm with slight hail";
            case 99 -> "Thunderstorm with heavy hail";
            default -> "Unspecified weather (" + code + ")";
        };
    }

    private double round1(double val) {
        return Math.round(val * 10.0) / 10.0;
    }
}
