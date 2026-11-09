package com.abhedya.ai;

import com.abhedya.ai.client.OpenMeteoClient;
import com.abhedya.ai.dto.*;
import com.abhedya.ai.service.RiskAssessmentService;
import com.abhedya.ai.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class WeatherIntegrationTests {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private OpenMeteoClient openMeteoClient;

    @Autowired
    private RiskAssessmentService riskAssessmentService;

    @BeforeEach
    void setUp() {
        weatherService.evictCache();
    }

    @Test
    @DisplayName("Should retrieve weather telemetry for a known habitation (hab-kl-01 Wayanad)")
    void testWeatherTelemetryForHabitation() {
        WeatherTelemetryDto telemetry = weatherService.getWeatherForHabitation("hab-kl-01");

        assertNotNull(telemetry, "Telemetry should not be null");
        assertEquals("hab-kl-01", telemetry.getHabitationId());
        assertEquals("Wayanad Hill Settlement", telemetry.getHabitationName());
        assertEquals("Wayanad", telemetry.getDistrict());
        assertEquals("Kerala", telemetry.getState());
        assertEquals(11.6854, telemetry.getLatitude(), 0.001);
        assertEquals(76.1320, telemetry.getLongitude(), 0.001);

        assertNotNull(telemetry.getTemperatureC(), "Temperature must be present");
        assertNotNull(telemetry.getRelativeHumidityPercent(), "Relative humidity must be present");
        assertNotNull(telemetry.getWindSpeedKmh(), "Wind speed must be present");
        assertNotNull(telemetry.getAlertTier(), "Alert tier must be computed");
        assertNotNull(telemetry.getAlertHeadline(), "Alert headline must be generated");
        assertNotNull(telemetry.getAlertRationale(), "Alert rationale must be generated");

        // Verify data source is one of the sanctioned types
        assertTrue(List.of("LIVE_OPEN_METEO", "CACHED_TELEMETRY", "FALLBACK_BASELINE").contains(telemetry.getDataSource()),
                "Data source must be LIVE_OPEN_METEO, CACHED_TELEMETRY, or FALLBACK_BASELINE, but got: " + telemetry.getDataSource());
    }

    @Test
    @DisplayName("Should cache weather telemetry and serve subsequent requests from cache")
    void testInMemoryCachingAndHitMetrics() {
        double lat = 12.4244;
        double lng = 75.7382;

        // First call -> Cache MISS
        WeatherTelemetryDto firstCall = weatherService.getWeatherForCoordinates(lat, lng);
        assertNotNull(firstCall);

        // Second call -> Cache HIT
        WeatherTelemetryDto secondCall = weatherService.getWeatherForCoordinates(lat, lng);
        assertNotNull(secondCall);
        assertEquals("CACHED_TELEMETRY", secondCall.getDataSource(), "Second call must be served from cache");
        assertNotNull(secondCall.getCacheTtlSecondsRemaining());
        assertTrue(secondCall.getCacheTtlSecondsRemaining() > 0, "Remaining TTL should be positive");

        // Verify cache status metrics
        WeatherCacheStatusDto status = weatherService.getCacheStatus();
        assertTrue(status.getCachedEntriesCount() >= 1, "Cache should contain at least 1 entry");
        assertTrue(status.getCacheHits() >= 1, "Cache hits should be at least 1");
        assertTrue(status.getHitRatioPercent() > 0.0, "Hit ratio should be greater than 0%");
    }

    @Test
    @DisplayName("Should evaluate alert tiers accurately across IMD meteorological thresholds")
    void testAlertTierEvaluation() {
        // Normal: 5mm rain, 12 km/h wind, code 0 (Clear)
        assertEquals("NONE", openMeteoClient.evaluateAlertTier(5.0, 12.0, 0));

        // Advisory: 30mm rain (>= 25mm)
        assertEquals("ADVISORY", openMeteoClient.evaluateAlertTier(30.0, 15.0, 0));

        // Watch: 70mm rain (>= 64.5mm)
        assertEquals("WATCH", openMeteoClient.evaluateAlertTier(70.0, 20.0, 0));

        // Warning: 130mm rain (>= 115.5mm)
        assertEquals("WARNING", openMeteoClient.evaluateAlertTier(130.0, 20.0, 0));

        // Warning: Gale wind 80 km/h (>= 75 km/h)
        assertEquals("WARNING", openMeteoClient.evaluateAlertTier(10.0, 80.0, 0));

        // Emergency: Extremely heavy rainfall 210mm (>= 204.5mm)
        assertEquals("EMERGENCY", openMeteoClient.evaluateAlertTier(210.0, 30.0, 0));

        // Emergency: Cyclonic wind 95 km/h (>= 90 km/h)
        assertEquals("EMERGENCY", openMeteoClient.evaluateAlertTier(15.0, 95.0, 0));

        // Emergency: Severe hail thunderstorm (code 96) with heavy rain (70mm)
        assertEquals("EMERGENCY", openMeteoClient.evaluateAlertTier(70.0, 35.0, 96));
    }

    @Test
    @DisplayName("Should retrieve multi-day forecast for a habitation with typed daily forecasts")
    void testForecastRetrieval() {
        WeatherForecastResponseDto forecast = weatherService.getForecastForHabitation("hab-ka-01");

        assertNotNull(forecast);
        assertEquals("hab-ka-01", forecast.getHabitationId());
        assertEquals("Kodagu Vulnerable Village", forecast.getHabitationName());
        assertEquals("Kodagu", forecast.getDistrict());

        assertNotNull(forecast.getCurrentTelemetry());
        assertNotNull(forecast.getDailyForecasts());
        assertFalse(forecast.getDailyForecasts().isEmpty(), "Forecasts list should not be empty");

        DailyForecastDto day1 = forecast.getDailyForecasts().get(0);
        assertNotNull(day1.getDate(), "Date must be present");
        assertNotNull(day1.getPrecipitationSumMm(), "Precipitation sum must be present");
        assertNotNull(day1.getWindSpeedMaxKmh(), "Max wind speed must be present");
        assertNotNull(day1.getWeatherCondition(), "Weather condition must be present");
        assertNotNull(day1.getAlertTier(), "Alert tier must be present");
    }

    @Test
    @DisplayName("Should gracefully handle offline / network timeouts with deterministic fallback baseline")
    void testDeterministicFallbackBehavior() {
        WeatherForecastResponseDto fallback = openMeteoClient.buildFallbackForecastResponse(11.5380, 76.1770, "Simulated network timeout");

        assertNotNull(fallback);
        assertEquals("FALLBACK_BASELINE", fallback.getDataSource());
        assertTrue(fallback.isStale(), "Fallback telemetry must be flagged as stale");

        WeatherTelemetryDto current = fallback.getCurrentTelemetry();
        assertNotNull(current);
        assertEquals("FALLBACK_BASELINE", current.getDataSource());
        assertTrue(current.isStale());
        assertEquals("Simulated network timeout", current.getFallbackReason());
        assertEquals("NONE", current.getAlertTier());
        assertNotNull(current.getAlertHeadline());
        assertNotNull(current.getAlertRationale());

        assertEquals(3, fallback.getDailyForecasts().size(), "Fallback should provide 3-day projection");
    }

    @Test
    @DisplayName("Should integrate live weather intelligence into risk assessment engine")
    void testLiveWeatherRiskAssessmentIntegration() {
        RiskAssessmentResponseDto assessment = riskAssessmentService.assessRiskWithLiveWeather("hab-kl-01", null);

        assertNotNull(assessment);
        assertEquals("hab-kl-01", assessment.getHabitationId());
        assertNotNull(assessment.getWeatherDataSource(), "Weather data source must be tracked");
        assertNotNull(assessment.getRainfall24hMm(), "Rainfall must be populated from weather telemetry");
        assertNotNull(assessment.getCompositeScore(), "Composite risk score must be computed");
        assertNotNull(assessment.getVulnerabilityIndex(), "Vulnerability index must be computed");
        assertNotNull(assessment.getClassification(), "Classification must be determined");

        // Rationale must incorporate weather telemetry details
        assertNotNull(assessment.getAssessmentRationale());
        assertTrue(assessment.getAssessmentRationale().contains("weather") ||
                   assessment.getAssessmentRationale().contains("precipitation") ||
                   assessment.getAssessmentRationale().contains("Weather"),
                "Assessment rationale should document weather parameters");
    }

    @Test
    @DisplayName("Should correctly map WMO weather interpretation codes to readable text")
    void testWmoCodeInterpretation() {
        assertEquals("Clear sky", openMeteoClient.interpretWeatherCode(0));
        assertEquals("Mainly clear", openMeteoClient.interpretWeatherCode(1));
        assertEquals("Partly cloudy", openMeteoClient.interpretWeatherCode(2));
        assertEquals("Overcast", openMeteoClient.interpretWeatherCode(3));
        assertEquals("Fog", openMeteoClient.interpretWeatherCode(45));
        assertEquals("Heavy rain", openMeteoClient.interpretWeatherCode(65));
        assertEquals("Violent rain showers", openMeteoClient.interpretWeatherCode(82));
        assertEquals("Thunderstorm", openMeteoClient.interpretWeatherCode(95));
        assertEquals("Thunderstorm with heavy hail", openMeteoClient.interpretWeatherCode(99));
    }

    @Test
    @DisplayName("Should purge cached entries when evictCache is executed")
    void testCacheEviction() {
        weatherService.getWeatherForCoordinates(15.2993, 74.1240); // Goa
        assertTrue(weatherService.getCacheStatus().getCachedEntriesCount() > 0);

        weatherService.evictCache();
        assertEquals(0, weatherService.getCacheStatus().getCachedEntriesCount(), "Cache must be empty after eviction");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when requesting weather for non-existent habitation")
    void testNonExistentHabitationThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                weatherService.getWeatherForHabitation("NON-EXISTENT-HAB-ID")
        );
    }
}
