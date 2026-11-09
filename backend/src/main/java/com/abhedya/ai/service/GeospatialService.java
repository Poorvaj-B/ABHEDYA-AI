package com.abhedya.ai.service;

import com.abhedya.ai.dto.GeoJsonFeatureCollectionDto;
import com.abhedya.ai.dto.GeoJsonFeatureDto;
import com.abhedya.ai.dto.HabitationAtRiskDto;
import com.abhedya.ai.model.Habitation;
import com.abhedya.ai.model.HazardZone;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GeospatialService {

    private static final Logger log = LoggerFactory.getLogger(GeospatialService.class);
    private static final double EARTH_RADIUS_METERS = 6371000.0;
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parses a GeoJSON string into a JTS Geometry (Polygon or MultiPolygon).
     */
    public Geometry parseGeoJsonToGeometry(String geoJsonString) {
        if (geoJsonString == null || geoJsonString.trim().isEmpty()) {
            throw new IllegalArgumentException("GeoJSON string cannot be null or empty");
        }
        try {
            JsonNode rootNode = objectMapper.readTree(geoJsonString);
            String type = rootNode.has("type") ? rootNode.get("type").asText() : "";
            JsonNode coordinatesNode = rootNode.get("coordinates");

            if (coordinatesNode == null || !coordinatesNode.isArray()) {
                throw new IllegalArgumentException("Invalid GeoJSON: missing coordinates array");
            }

            if ("Polygon".equalsIgnoreCase(type)) {
                return parsePolygon(coordinatesNode);
            } else if ("MultiPolygon".equalsIgnoreCase(type)) {
                return parseMultiPolygon(coordinatesNode);
            } else if ("LineString".equalsIgnoreCase(type)) {
                return parseLineString(coordinatesNode);
            } else {
                throw new IllegalArgumentException("Unsupported GeoJSON geometry type: " + type
                        + ". Only Polygon, MultiPolygon, and LineString are supported.");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse GeoJSON: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid GeoJSON format: " + e.getMessage(), e);
        }
    }

    private Polygon parsePolygon(JsonNode coordinatesNode) {
        if (coordinatesNode.size() == 0) {
            throw new IllegalArgumentException("Polygon coordinates array is empty");
        }
        JsonNode exteriorRingNode = coordinatesNode.get(0);
        LinearRing shell = createLinearRing(exteriorRingNode);

        LinearRing[] holes = null;
        if (coordinatesNode.size() > 1) {
            holes = new LinearRing[coordinatesNode.size() - 1];
            for (int i = 1; i < coordinatesNode.size(); i++) {
                holes[i - 1] = createLinearRing(coordinatesNode.get(i));
            }
        }
        return geometryFactory.createPolygon(shell, holes);
    }

    private MultiPolygon parseMultiPolygon(JsonNode coordinatesNode) {
        Polygon[] polygons = new Polygon[coordinatesNode.size()];
        for (int i = 0; i < coordinatesNode.size(); i++) {
            polygons[i] = parsePolygon(coordinatesNode.get(i));
        }
        return geometryFactory.createMultiPolygon(polygons);
    }

    private LineString parseLineString(JsonNode coordinatesNode) {
        int pointCount = coordinatesNode.size();
        if (pointCount < 2) {
            throw new IllegalArgumentException("LineString must have at least 2 coordinate pairs");
        }
        Coordinate[] coordinates = new Coordinate[pointCount];
        for (int i = 0; i < pointCount; i++) {
            JsonNode pt = coordinatesNode.get(i);
            double lng = pt.get(0).asDouble();
            double lat = pt.get(1).asDouble();
            coordinates[i] = new Coordinate(lng, lat);
        }
        return geometryFactory.createLineString(coordinates);
    }

    private LinearRing createLinearRing(JsonNode ringNode) {
        int pointCount = ringNode.size();
        if (pointCount < 4) {
            throw new IllegalArgumentException(
                    "LinearRing must have at least 4 coordinate pairs (first and last identical)");
        }
        Coordinate[] coordinates = new Coordinate[pointCount];
        for (int i = 0; i < pointCount; i++) {
            JsonNode pt = ringNode.get(i);
            double lng = pt.get(0).asDouble();
            double lat = pt.get(1).asDouble();
            coordinates[i] = new Coordinate(lng, lat);
        }
        // Ensure closed ring
        if (!coordinates[0].equals2D(coordinates[pointCount - 1])) {
            coordinates[pointCount - 1] = new Coordinate(coordinates[0].x, coordinates[0].y);
        }
        return geometryFactory.createLinearRing(coordinates);
    }

    /**
     * Checks if a habitation coordinate is inside a hazard geometry.
     */
    public boolean isPointInPolygon(double lat, double lng, Geometry geometry) {
        if (geometry == null)
            return false;
        Point point = geometryFactory.createPoint(new Coordinate(lng, lat));
        return geometry.contains(point) || geometry.touches(point);
    }

    /**
     * Calculates geodesic Haversine distance between two coordinates in metres.
     */
    public double haversineDistanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    /**
     * Calculates distance in metres from a point to the boundary of a geometry.
     * Uses segment projection for true geodesic distance to the closest polygon
     * edge.
     */
    public double distanceToBoundaryMeters(double lat, double lng, Geometry geometry) {
        if (geometry == null)
            return Double.MAX_VALUE;

        Coordinate[] coords = geometry.getCoordinates();
        if (coords.length < 2)
            return Double.MAX_VALUE;

        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < coords.length - 1; i++) {
            double dist = distancePointToSegmentMeters(lat, lng, coords[i].y, coords[i].x, coords[i + 1].y,
                    coords[i + 1].x);
            if (dist < minDistance) {
                minDistance = dist;
            }
        }
        return minDistance;
    }

    private double distancePointToSegmentMeters(double pLat, double pLon, double aLat, double aLon, double bLat,
            double bLon) {
        double segmentLength = haversineDistanceMeters(aLat, aLon, bLat, bLon);
        if (segmentLength < 1.0) {
            return haversineDistanceMeters(pLat, pLon, aLat, aLon);
        }

        // Projection factor t on segment AB
        double aX = Math.toRadians(aLon) * Math.cos(Math.toRadians((aLat + bLat) / 2));
        double aY = Math.toRadians(aLat);
        double bX = Math.toRadians(bLon) * Math.cos(Math.toRadians((aLat + bLat) / 2));
        double bY = Math.toRadians(bLat);
        double pX = Math.toRadians(pLon) * Math.cos(Math.toRadians((aLat + bLat) / 2));
        double pY = Math.toRadians(pLat);

        double abX = bX - aX;
        double abY = bY - aY;
        double apX = pX - aX;
        double apY = pY - aY;

        double t = (apX * abX + apY * abY) / (abX * abX + abY * abY);
        t = Math.max(0.0, Math.min(1.0, t));

        double closestLat = aLat + t * (bLat - aLat);
        double closestLon = aLon + t * (bLon - aLon);

        return haversineDistanceMeters(pLat, pLon, closestLat, closestLon);
    }

    /**
     * Calculates the true geodetic area of a polygon in square kilometres.
     */
    public double calculateAreaSqKm(Geometry geometry) {
        if (geometry == null || geometry.isEmpty())
            return 0.0;
        Coordinate[] coords = geometry.getCoordinates();
        if (coords.length < 3)
            return 0.0;

        double total = 0.0;
        for (int i = 0; i < coords.length - 1; i++) {
            double p1Lng = Math.toRadians(coords[i].x);
            double p1Lat = Math.toRadians(coords[i].y);
            double p2Lng = Math.toRadians(coords[i + 1].x);
            double p2Lat = Math.toRadians(coords[i + 1].y);
            total += (p2Lng - p1Lng) * (2 + Math.sin(p1Lat) + Math.sin(p2Lat));
        }
        total = Math.abs(total * EARTH_RADIUS_KM * EARTH_RADIUS_KM / 2.0);
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Calculates the perimeter of a polygon in kilometres.
     */
    public double calculatePerimeterKm(Geometry geometry) {
        if (geometry == null || geometry.isEmpty())
            return 0.0;
        Coordinate[] coords = geometry.getCoordinates();
        double perimeter = 0.0;
        for (int i = 0; i < coords.length - 1; i++) {
            perimeter += haversineDistanceMeters(coords[i].y, coords[i].x, coords[i + 1].y, coords[i + 1].x);
        }
        return Math.round((perimeter / 1000.0) * 100.0) / 100.0;
    }

    /**
     * Evaluates habitations against active hazard zones and classifies exposure.
     */
    public List<HabitationAtRiskDto> evaluateHabitationRisks(List<Habitation> habitations,
            List<HazardZone> activeZones) {
        List<HabitationAtRiskDto> results = new ArrayList<>();
        Map<String, Geometry> zoneGeomMap = new HashMap<>();

        for (HazardZone zone : activeZones) {
            try {
                Geometry geom = parseGeoJsonToGeometry(zone.getBoundaryGeoJson());
                zoneGeomMap.put(zone.getId(), geom);
            } catch (Exception e) {
                log.warn("Skipping invalid boundary for hazard zone {}: {}", zone.getZoneCode(), e.getMessage());
            }
        }

        for (Habitation hab : habitations) {
            HazardZone matchedZone = null;
            String highestStatus = "LOW_EXPOSURE";
            double closestBoundaryMeters = Double.MAX_VALUE;
            double distanceToCenterKm = Double.MAX_VALUE;

            for (HazardZone zone : activeZones) {
                Geometry geom = zoneGeomMap.get(zone.getId());
                if (geom == null)
                    continue;

                boolean inside = isPointInPolygon(hab.getLatitude(), hab.getLongitude(), geom);
                double distBoundary = distanceToBoundaryMeters(hab.getLatitude(), hab.getLongitude(), geom);
                double distCenter = haversineDistanceMeters(hab.getLatitude(), hab.getLongitude(), zone.getCenterLat(),
                        zone.getCenterLng()) / 1000.0;

                if (inside) {
                    // Highest priority: inside polygon
                    highestStatus = "INSIDE_HAZARD_ZONE";
                    matchedZone = zone;
                    closestBoundaryMeters = distBoundary;
                    distanceToCenterKm = distCenter;
                    break; // Inside active polygon takes immediate precedence
                } else if (distBoundary <= (zone.getBufferRadiusKm() * 1000.0)) {
                    // Within buffer radius of the polygon
                    if (!"INSIDE_HAZARD_ZONE".equals(highestStatus)) {
                        highestStatus = "WITHIN_BUFFER";
                        matchedZone = zone;
                        closestBoundaryMeters = distBoundary;
                        distanceToCenterKm = distCenter;
                    }
                } else if (distBoundary < closestBoundaryMeters && matchedZone == null) {
                    closestBoundaryMeters = distBoundary;
                    distanceToCenterKm = distCenter;
                }
            }

            if (matchedZone != null || "INSIDE_HAZARD_ZONE".equals(highestStatus)
                    || "WITHIN_BUFFER".equals(highestStatus)) {
                HabitationAtRiskDto dto = new HabitationAtRiskDto();
                dto.setHabitationId(hab.getId());
                dto.setHabitationName(hab.getName());
                dto.setDistrict(hab.getDistrict());
                dto.setState(hab.getState());
                dto.setLatitude(hab.getLatitude());
                dto.setLongitude(hab.getLongitude());
                dto.setTotalPopulation(hab.getTotalPopulation());
                dto.setVulnerablePopulation(hab.getVulnerablePopulation());
                dto.setSafeCarryingCapacity(hab.getSafeCarryingCapacity());
                dto.setExcessPopulation(hab.getExcessPopulation());
                dto.setExposureStatus(highestStatus);
                dto.setDistanceToHazardBoundaryMeters(Math.round(closestBoundaryMeters * 10.0) / 10.0);
                dto.setDistanceToHazardCenterKm(Math.round(distanceToCenterKm * 10.0) / 10.0);

                if (matchedZone != null) {
                    dto.setHazardZoneId(matchedZone.getId());
                    dto.setHazardZoneCode(matchedZone.getZoneCode());
                    dto.setHazardType(matchedZone.getHazardType());
                    dto.setSeverityLevel(matchedZone.getSeverityLevel());
                    dto.setIsEstimatedBuffer(Boolean.TRUE.equals(matchedZone.getIsEstimatedBuffer()));
                }

                results.add(dto);
            }
        }

        return results;
    }

    /**
     * Converts a list of HazardZone entities into a standard RFC 7946 GeoJSON
     * FeatureCollection.
     */
    public GeoJsonFeatureCollectionDto toFeatureCollection(List<HazardZone> zones) {
        GeoJsonFeatureCollectionDto collection = new GeoJsonFeatureCollectionDto();

        for (HazardZone zone : zones) {
            try {
                JsonNode geometryNode = objectMapper.readTree(zone.getBoundaryGeoJson());
                Map<String, Object> properties = new LinkedHashMap<>();
                properties.put("id", zone.getId());
                properties.put("zoneCode", zone.getZoneCode());
                properties.put("hazardType", zone.getHazardType());
                properties.put("severityLevel", zone.getSeverityLevel());
                properties.put("state", zone.getState());
                properties.put("district", zone.getDistrict());
                properties.put("centerLat", zone.getCenterLat());
                properties.put("centerLng", zone.getCenterLng());
                properties.put("bufferRadiusKm", zone.getBufferRadiusKm());
                properties.put("isEstimatedBuffer", Boolean.TRUE.equals(zone.getIsEstimatedBuffer()));
                properties.put("areaSqKm", zone.getAreaSqKm());
                properties.put("perimeterKm", zone.getPerimeterKm());
                properties.put("isActive", zone.getIsActive());

                GeoJsonFeatureDto feature = new GeoJsonFeatureDto(zone.getId(), geometryNode, properties);
                collection.addFeature(feature);
            } catch (Exception e) {
                log.error("Failed to serialize hazard zone {} to GeoJSON: {}", zone.getZoneCode(), e.getMessage());
            }
        }

        return collection;
    }

    public record RouteSafetyAssessment(boolean avoidsRedZones, boolean validatedSafe, String safetyNotes,
            List<String> intersectedZoneCodes) {
    }

    /**
     * Evaluates route geometry against active hazard red zones to determine safety
     * clearance.
     */

    public RouteSafetyAssessment evaluateRouteSafety(
            String routeGeoJson, List<HazardZone> activeZones) {

        // Missing route geometry cannot be considered safe
        if (routeGeoJson == null || routeGeoJson.isBlank()) {
            return new RouteSafetyAssessment(
                    false, false,
                    "Route safety could not be verified: missing route geometry.",
                    List.of());
        }

        // Missing hazard data cannot be considered safe
        if (activeZones == null) {
            return new RouteSafetyAssessment(
                    false, false,
                    "Route safety could not be verified: hazard data unavailable.",
                    List.of());
        }

        try {
            Geometry routeGeom = parseGeoJsonToGeometry(routeGeoJson);

            List<String> intersectedCodes = new ArrayList<>();

            for (HazardZone zone : activeZones) {

                if (zone == null ||
                        zone.getBoundaryGeoJson() == null ||
                        zone.getBoundaryGeoJson().isBlank()) {

                    throw new IllegalStateException(
                            "Hazard zone geometry is missing.");
                }

                Geometry zoneGeom = parseGeoJsonToGeometry(zone.getBoundaryGeoJson());

                if (routeGeom.intersects(zoneGeom)) {
                    String code = zone.getZoneCode() != null
                            ? zone.getZoneCode()
                            : "UNKNOWN_ZONE";

                    intersectedCodes.add(code);
                }
            }

            // No hazard intersection detected
            if (intersectedCodes.isEmpty()) {
                return new RouteSafetyAssessment(
                        true, true,
                        "Route verified: no intersection with active hazard zones.",
                        List.of());
            }

            // Hazard intersection detected
            String zonesList = String.join(", ", intersectedCodes);

            return new RouteSafetyAssessment(
                    false, false,
                    "UNSAFE ROUTE: Evacuation route intersects active hazard zones: "
                            + zonesList,
                    intersectedCodes);

        } catch (Exception e) {

            log.warn(
                    "Could not verify route safety: {}",
                    e.getMessage());

            return new RouteSafetyAssessment(
                    false, false,
                    "Route safety could not be verified due to a geometry validation error.",
                    List.of());
        }
    }
}