package com.abhedya.ai.service;

import com.abhedya.ai.client.OpenMeteoClient;
import com.abhedya.ai.dto.*;
import com.abhedya.ai.model.Habitation;
import com.abhedya.ai.repository.HabitationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final OpenMeteoClient openMeteoClient;
    private final HabitationRepository habitationRepository;
    private final int defaultTtlSeconds;

    // In-memory thread-safe telemetry cache
    private final Map<String, CachedWeatherEntry> cache = new ConcurrentHashMap<>();
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);

    public WeatherService(
            OpenMeteoClient openMeteoClient,
            HabitationRepository habitationRepository,
            @Value("${app.weather.cache-ttl-seconds:900}") int defaultTtlSeconds) {
        this.openMeteoClient = openMeteoClient;
        this.habitationRepository = habitationRepository;
        this.defaultTtlSeconds = defaultTtlSeconds;
    }

    /**
     * Retrieves current weather telemetry for a specific habitation.
     */
    public WeatherTelemetryDto getWeatherForHabitation(String habitationId) {
        Habitation hab = habitationRepository.findById(habitationId)
                .orElseThrow(() -> new IllegalArgumentException("Habitation not found with ID: " + habitationId));

        String habKey = "hab:" + hab.getId();
        long now = System.currentTimeMillis();

        CachedWeatherEntry cached = cache.get(habKey);
        if (cached != null && !cached.isExpired(now)) {
            cacheHits.incrementAndGet();
            log.debug("Cache HIT for habitation: {}", hab.getId());
            return enrichTelemetry(cached.response().getCurrentTelemetry(), hab, "CACHED_TELEMETRY",
                    cached.getSecondsRemaining(now));
        }

        // Check coordinate-level cache key (reduces duplicate calls for proximate
        // habitations)
        String geoKey = buildGeoKey(hab.getLatitude(), hab.getLongitude());
        CachedWeatherEntry geoCached = cache.get(geoKey);
        if (geoCached != null && !geoCached.isExpired(now)) {
            cacheHits.incrementAndGet();
            log.debug("Cache HIT (geo-level) for habitation: {}", hab.getId());
            return enrichTelemetry(geoCached.response().getCurrentTelemetry(), hab, "CACHED_TELEMETRY",
                    geoCached.getSecondsRemaining(now));
        }

        cacheMisses.incrementAndGet();
        log.debug("Cache MISS for habitation: {}. Fetching from Open-Meteo client.", hab.getId());

        WeatherForecastResponseDto forecast = openMeteoClient.fetchWeatherForecast(hab.getLatitude(),
                hab.getLongitude());
        WeatherTelemetryDto telemetry = forecast.getCurrentTelemetry();
        enrichTelemetry(telemetry, hab, telemetry.getDataSource(), (long) defaultTtlSeconds);

        // Populate cache
        long expiresAt = now + (defaultTtlSeconds * 1000L);
        CachedWeatherEntry entry = new CachedWeatherEntry(forecast, now, expiresAt);
        cache.put(habKey, entry);
        cache.put(geoKey, entry);

        return telemetry;
    }

    /**
     * Retrieves current weather telemetry for arbitrary geographical coordinates.
     */
    public WeatherTelemetryDto getWeatherForCoordinates(double latitude, double longitude) {
        String geoKey = buildGeoKey(latitude, longitude);
        long now = System.currentTimeMillis();

        CachedWeatherEntry cached = cache.get(geoKey);
        if (cached != null && !cached.isExpired(now)) {
            cacheHits.incrementAndGet();
            WeatherTelemetryDto telemetry = copyTelemetry(cached.response().getCurrentTelemetry());
            telemetry.setDataSource("CACHED_TELEMETRY");
            telemetry.setCacheTtlSecondsRemaining(cached.getSecondsRemaining(now));
            return telemetry;
        }

        cacheMisses.incrementAndGet();
        WeatherForecastResponseDto forecast = openMeteoClient.fetchWeatherForecast(latitude, longitude);
        long expiresAt = now + (defaultTtlSeconds * 1000L);
        cache.put(geoKey, new CachedWeatherEntry(forecast, now, expiresAt));

        WeatherTelemetryDto telemetry = forecast.getCurrentTelemetry();
        telemetry.setCacheTtlSecondsRemaining((long) defaultTtlSeconds);
        return telemetry;
    }

    /**
     * Retrieves multi-day weather forecast and current conditions for a habitation.
     */
    public WeatherForecastResponseDto getForecastForHabitation(String habitationId) {
        Habitation hab = habitationRepository.findById(habitationId)
                .orElseThrow(() -> new IllegalArgumentException("Habitation not found with ID: " + habitationId));

        String habKey = "hab:" + hab.getId();
        long now = System.currentTimeMillis();

        CachedWeatherEntry cached = cache.get(habKey);
        WeatherForecastResponseDto forecast;

        if (cached != null && !cached.isExpired(now)) {
            cacheHits.incrementAndGet();
            forecast = cached.response();
        } else {
            cacheMisses.incrementAndGet();
            forecast = openMeteoClient.fetchWeatherForecast(hab.getLatitude(), hab.getLongitude());
            long expiresAt = now + (defaultTtlSeconds * 1000L);
            cache.put(habKey, new CachedWeatherEntry(forecast, now, expiresAt));
            cache.put(buildGeoKey(hab.getLatitude(), hab.getLongitude()),
                    new CachedWeatherEntry(forecast, now, expiresAt));
        }

        forecast.setHabitationId(hab.getId());
        forecast.setHabitationName(hab.getName());
        forecast.setDistrict(hab.getDistrict());
        forecast.setState(hab.getState());
        if (forecast.getCurrentTelemetry() != null) {
            enrichTelemetry(forecast.getCurrentTelemetry(), hab, forecast.getCurrentTelemetry().getDataSource(),
                    cached != null ? cached.getSecondsRemaining(now) : (long) defaultTtlSeconds);
        }

        return forecast;
    }

    /**
     * Retrieves all active weather alerts across all habitations, prioritized by
     * severity.
     */
    public List<WeatherAlertDto> getActiveWeatherAlerts() {
        List<Habitation> habitations = habitationRepository.findAll();
        List<WeatherAlertDto> alerts = new ArrayList<>();

        for (Habitation hab : habitations) {
            try {
                WeatherTelemetryDto telemetry = getWeatherForHabitation(hab.getId());
                if (telemetry != null && telemetry.getAlertTier() != null
                        && !"NONE".equalsIgnoreCase(telemetry.getAlertTier())) {
                    WeatherAlertDto alert = new WeatherAlertDto();
                    alert.setHabitationId(hab.getId());
                    alert.setHabitationName(hab.getName());
                    alert.setDistrict(hab.getDistrict());
                    alert.setState(hab.getState());
                    alert.setLatitude(hab.getLatitude());
                    alert.setLongitude(hab.getLongitude());
                    alert.setAlertTier(telemetry.getAlertTier());
                    alert.setHeadline(telemetry.getAlertHeadline());
                    alert.setRationale(telemetry.getAlertRationale());
                    alert.setRainfall24hMm(telemetry.getPrecipitationLast24hMm());
                    alert.setWindSpeedKmh(telemetry.getWindSpeedKmh());
                    alert.setWindGustsKmh(telemetry.getWindGustsKmh());
                    alert.setWeatherCode(telemetry.getWeatherCode());
                    alert.setWeatherCondition(telemetry.getWeatherCondition());
                    alert.setInRedZone(Boolean.TRUE.equals(hab.getInRedZone()));
                    alert.setTotalPopulation(hab.getTotalPopulation());
                    alert.setVulnerablePopulation(hab.getVulnerablePopulation());
                    alert.setDataSource(telemetry.getDataSource());
                    alert.setTimestamp(LocalDateTime.now());

                    // Determine Alert Type
                    if (telemetry.getPrecipitationLast24hMm() != null
                            && telemetry.getPrecipitationLast24hMm() >= 115.5) {
                        alert.setAlertType("EXTREME_PRECIPITATION");
                    } else if (telemetry.getWindSpeedKmh() != null && telemetry.getWindSpeedKmh() >= 75.0) {
                        alert.setAlertType("CYCLONIC_WIND");
                    } else if (telemetry.getWeatherCode() != null && (telemetry.getWeatherCode() == 95
                            || telemetry.getWeatherCode() == 96 || telemetry.getWeatherCode() == 99)) {
                        alert.setAlertType("SEVERE_THUNDERSTORM");
                    } else {
                        alert.setAlertType("ELEVATED_WEATHER");
                    }

                    alerts.add(alert);
                }
            } catch (Exception e) {
                log.warn("Failed to check weather alerts for habitation {}: {}", hab.getId(), e.getMessage());
            }
        }

        // Sort by alert severity priority: EMERGENCY > WARNING > WATCH > ADVISORY
        alerts.sort(Comparator.comparingInt(this::getAlertPriorityWeight).reversed());
        return alerts;
    }

    /**
     * Inspects cache status, hit/miss metrics, and active cache keys.
     */
    public WeatherCacheStatusDto getCacheStatus() {
        WeatherCacheStatusDto status = new WeatherCacheStatusDto();
        long now = System.currentTimeMillis();

        // Evict expired entries during inspection
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired(now));

        long hits = cacheHits.get();
        long misses = cacheMisses.get();
        long total = hits + misses;
        double hitRatio = total > 0 ? ((double) hits / total) * 100.0 : 0.0;

        status.setCachedEntriesCount(cache.size());
        status.setCacheHits(hits);
        status.setCacheMisses(misses);
        status.setHitRatioPercent(Math.round(hitRatio * 10.0) / 10.0);
        status.setDefaultTtlSeconds(defaultTtlSeconds);
        status.setCachedKeys(new ArrayList<>(cache.keySet()));
        return status;
    }

    /**
     * Purges all cached weather telemetry entries.
     */
    public void evictCache() {
        int size = cache.size();
        cache.clear();
        log.info("Purged {} weather cache entries.", size);
    }

    private WeatherTelemetryDto enrichTelemetry(WeatherTelemetryDto source, Habitation hab, String dataSource,
            Long remainingTtl) {
        source.setHabitationId(hab.getId());
        source.setHabitationName(hab.getName());
        source.setDistrict(hab.getDistrict());
        source.setState(hab.getState());
        source.setLatitude(hab.getLatitude());
        source.setLongitude(hab.getLongitude());
        if (hab.getElevationMeters() != null
                && (source.getElevationMeters() == null || source.getElevationMeters() == 0.0)) {
            source.setElevationMeters(hab.getElevationMeters().doubleValue());
        }
        if (dataSource != null) {
            source.setDataSource(dataSource);
        }
        source.setCacheTtlSecondsRemaining(remainingTtl);
        return source;
    }

    private WeatherTelemetryDto copyTelemetry(WeatherTelemetryDto src) {
        WeatherTelemetryDto copy = new WeatherTelemetryDto();
        copy.setHabitationId(src.getHabitationId());
        copy.setHabitationName(src.getHabitationName());
        copy.setDistrict(src.getDistrict());
        copy.setState(src.getState());
        copy.setLatitude(src.getLatitude());
        copy.setLongitude(src.getLongitude());
        copy.setElevationMeters(src.getElevationMeters());
        copy.setTimestamp(src.getTimestamp());
        copy.setTemperatureC(src.getTemperatureC());
        copy.setRelativeHumidityPercent(src.getRelativeHumidityPercent());
        copy.setCurrentPrecipitationMm(src.getCurrentPrecipitationMm());
        copy.setRainMm(src.getRainMm());
        copy.setPrecipitationLast24hMm(src.getPrecipitationLast24hMm());
        copy.setPrecipitationNext24hMm(src.getPrecipitationNext24hMm());
        copy.setWindSpeedKmh(src.getWindSpeedKmh());
        copy.setWindGustsKmh(src.getWindGustsKmh());
        copy.setWeatherCode(src.getWeatherCode());
        copy.setWeatherCondition(src.getWeatherCondition());
        copy.setAlertTier(src.getAlertTier());
        copy.setAlertHeadline(src.getAlertHeadline());
        copy.setAlertRationale(src.getAlertRationale());
        copy.setDataSource(src.getDataSource());
        copy.setStale(src.isStale());
        copy.setFallbackReason(src.getFallbackReason());
        return copy;
    }

    private String buildGeoKey(double lat, double lng) {
        return String.format(Locale.US, "geo:%.2f:%.2f", lat, lng);
    }

    private int getAlertPriorityWeight(WeatherAlertDto alert) {
        if (alert == null || alert.getAlertTier() == null)
            return 0;
        return switch (alert.getAlertTier().toUpperCase()) {
            case "EMERGENCY" -> 400;
            case "WARNING" -> 300;
            case "WATCH" -> 200;
            case "ADVISORY" -> 100;
            default -> 0;
        };
    }

    /**
     * Internal cache container holding response and expiration timestamps.
     */
    private record CachedWeatherEntry(WeatherForecastResponseDto response, long cachedAtMs, long expiresAtMs) {
        public boolean isExpired(long now) {
            return now >= expiresAtMs;
        }

        public long getSecondsRemaining(long now) {
            return Math.max(0, (expiresAtMs - now) / 1000L);
        }
    }
}
