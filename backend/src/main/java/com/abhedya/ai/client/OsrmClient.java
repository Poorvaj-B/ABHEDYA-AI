package com.abhedya.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

@Component
public class OsrmClient {

    private static final Logger log = LoggerFactory.getLogger(OsrmClient.class);

    private final String baseUrl;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;
    private final int maxRetries;
    private final long retryBackoffMs;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OsrmClient(
            @Value("${app.routing.osrm-base-url:http://router.project-osrm.org}") String baseUrl,
            @Value("${app.routing.connect-timeout-ms:4000}") int connectTimeoutMs,
            @Value("${app.routing.read-timeout-ms:6000}") int readTimeoutMs,
            @Value("${app.routing.max-retries:2}") int maxRetries,
            @Value("${app.routing.retry-backoff-ms:500}") long retryBackoffMs,
            ObjectMapper objectMapper) {

        this.baseUrl = baseUrl;
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
        this.maxRetries = maxRetries;
        this.retryBackoffMs = retryBackoffMs;
        this.objectMapper = objectMapper;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Queries OSRM road routing engine with automatic retry and graceful geodesic fallback.
     */
    public OsrmRouteResult calculateRoute(double startLat, double startLng, double endLat, double endLng) {
        // OSRM requires {longitude},{latitude} coordinates order
        String url = String.format(Locale.US,
                "%s/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson",
                baseUrl, startLng, startLat, endLng, endLat);

        int attempts = 0;
        Exception lastException = null;

        while (attempts <= maxRetries) {
            attempts++;
            try {
                log.debug("Querying OSRM road network (attempt {}/{}): {}", attempts, maxRetries + 1, url);
                String rawJson = restTemplate.getForObject(url, String.class);
                if (rawJson != null && !rawJson.isBlank()) {
                    JsonNode root = objectMapper.readTree(rawJson);
                    if ("Ok".equalsIgnoreCase(root.path("code").asText())) {
                        JsonNode routeNode = root.path("routes").get(0);
                        double distanceMeters = routeNode.path("distance").asDouble(0.0);
                        double durationSeconds = routeNode.path("duration").asDouble(0.0);
                        JsonNode geometryNode = routeNode.path("geometry");

                        double distanceKm = Math.round((distanceMeters / 1000.0) * 10.0) / 10.0;
                        double durationMinutes = Math.round((durationSeconds / 60.0) * 10.0) / 10.0;
                        String geoJson = objectMapper.writeValueAsString(geometryNode);

                        return new OsrmRouteResult(distanceKm, durationMinutes, geoJson, "OSRM_ROAD_NETWORK");
                    }
                }
            } catch (RestClientException e) {
                lastException = e;
                log.warn("OSRM routing attempt {} failed: {}", attempts, e.getMessage());
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
                log.error("Error processing OSRM routing response: {}", e.getMessage(), e);
                break;
            }
        }

        log.warn("OSRM routing engine unreachable after {} attempts ({}). Activating geodesic route fallback.",
                attempts, lastException != null ? lastException.getMessage() : "Offline");

        return generateGeodesicFallback(startLat, startLng, endLat, endLng);
    }

    /**
     * Generates a multi-point geodesic route fallback when road routing service is offline.
     */
    public OsrmRouteResult generateGeodesicFallback(double startLat, double startLng, double endLat, double endLng) {
        double straightDistanceKm = calculateHaversineDistanceKm(startLat, startLng, endLat, endLng);
        // Multiply by 1.25 road winding factor typical for Indian topography
        double roadDistanceKm = Math.round((straightDistanceKm * 1.25) * 10.0) / 10.0;
        // Assume average convoy speed of 45 km/h for emergency evacuation
        double durationMinutes = Math.round(((roadDistanceKm / 45.0) * 60.0) * 10.0) / 10.0;

        // Generate 5 intermediate waypoints along geodesic path
        StringBuilder coordinatesBuilder = new StringBuilder("[");
        coordinatesBuilder.append(String.format(Locale.US, "[%.6f,%.6f],", startLng, startLat));

        int steps = 5;
        for (int i = 1; i < steps; i++) {
            double fraction = (double) i / steps;
            double lat = startLat + (endLat - startLat) * fraction;
            // Slight natural curvature variation
            double lng = startLng + (endLng - startLng) * fraction + (Math.sin(fraction * Math.PI) * 0.008);
            coordinatesBuilder.append(String.format(Locale.US, "[%.6f,%.6f],", lng, lat));
        }

        coordinatesBuilder.append(String.format(Locale.US, "[%.6f,%.6f]]", endLng, endLat));

        String geoJson = String.format(Locale.US, "{\"type\":\"LineString\",\"coordinates\":%s}", coordinatesBuilder.toString());
        return new OsrmRouteResult(roadDistanceKm, durationMinutes, geoJson, "GEODESIC_FALLBACK");
    }

    private double calculateHaversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public record OsrmRouteResult(double distanceKm, double durationMinutes, String geoJson, String source) {}
}

