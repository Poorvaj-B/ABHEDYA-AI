package com.abhedya.ai.controller;

import com.abhedya.ai.dto.*;
import com.abhedya.ai.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Retrieves current weather telemetry for a specific habitation.
     * GET /api/v1/weather/telemetry/{habitationId}
     */
    @GetMapping("/telemetry/{habitationId}")
    public ResponseEntity<ApiResponse<WeatherTelemetryDto>> getWeatherForHabitation(@PathVariable String habitationId) {
        try {
            WeatherTelemetryDto telemetry = weatherService.getWeatherForHabitation(habitationId);
            return ResponseEntity.ok(ApiResponse.ok("Weather telemetry retrieved successfully", telemetry));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Retrieves current weather telemetry for arbitrary geographical coordinates.
     * GET /api/v1/weather/coordinates?lat={lat}&lng={lng}
     */
    @GetMapping("/coordinates")
    public ResponseEntity<ApiResponse<WeatherTelemetryDto>> getWeatherForCoordinates(
            @RequestParam double lat,
            @RequestParam double lng) {
        WeatherTelemetryDto telemetry = weatherService.getWeatherForCoordinates(lat, lng);
        return ResponseEntity.ok(
                ApiResponse.ok("Weather telemetry retrieved for coordinates (" + lat + ", " + lng + ")", telemetry));
    }

    /**
     * Retrieves multi-day weather forecast for a habitation.
     * GET /api/v1/weather/forecast/{habitationId}
     */
    @GetMapping("/forecast/{habitationId}")
    public ResponseEntity<ApiResponse<WeatherForecastResponseDto>> getForecastForHabitation(
            @PathVariable String habitationId) {
        try {
            WeatherForecastResponseDto forecast = weatherService.getForecastForHabitation(habitationId);
            return ResponseEntity.ok(ApiResponse.ok("Weather forecast retrieved successfully", forecast));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Retrieves active severe weather alerts across all habitations.
     * GET /api/v1/weather/alerts
     */
    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<WeatherAlertDto>>> getActiveWeatherAlerts() {
        List<WeatherAlertDto> alerts = weatherService.getActiveWeatherAlerts();
        return ResponseEntity.ok(ApiResponse.ok("Retrieved " + alerts.size() + " active weather alerts", alerts));
    }

    /**
     * Inspects in-memory weather telemetry cache status, hits/misses, and cached
     * entries.
     * GET /api/v1/weather/cache-status
     */
    @GetMapping("/cache-status")
    public ResponseEntity<ApiResponse<WeatherCacheStatusDto>> getCacheStatus() {
        WeatherCacheStatusDto status = weatherService.getCacheStatus();
        return ResponseEntity.ok(ApiResponse.ok("Cache status retrieved", status));
    }

    /**
     * Purges all cached weather telemetry entries.
     * POST /api/v1/weather/cache/evict
     */
    @PostMapping("/cache/evict")
    public ResponseEntity<ApiResponse<String>> evictCache() {
        weatherService.evictCache();
        return ResponseEntity.ok(ApiResponse.ok("Weather cache evicted successfully", "CACHE_CLEARED"));
    }
}
