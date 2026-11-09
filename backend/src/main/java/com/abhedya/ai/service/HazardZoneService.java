package com.abhedya.ai.service;

import com.abhedya.ai.dto.CreateHazardZoneRequest;
import com.abhedya.ai.dto.GeoJsonFeatureCollectionDto;
import com.abhedya.ai.dto.HabitationAtRiskDto;
import com.abhedya.ai.dto.HazardZoneDetailDto;
import com.abhedya.ai.model.Habitation;
import com.abhedya.ai.model.HazardZone;
import com.abhedya.ai.repository.HabitationRepository;
import com.abhedya.ai.repository.HazardZoneRepository;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HazardZoneService {

    private final HazardZoneRepository hazardZoneRepository;
    private final HabitationRepository habitationRepository;
    private final GeospatialService geospatialService;

    public HazardZoneService(HazardZoneRepository hazardZoneRepository,
                             HabitationRepository habitationRepository,
                             GeospatialService geospatialService) {
        this.hazardZoneRepository = hazardZoneRepository;
        this.habitationRepository = habitationRepository;
        this.geospatialService = geospatialService;
    }

    public List<HazardZone> getAllActiveZones() {
        return hazardZoneRepository.findByIsActiveTrue();
    }

    public Optional<HazardZone> getZoneById(String id) {
        return hazardZoneRepository.findById(id);
    }

    public List<HazardZone> getZones(String state, String type, Boolean activeOnly) {
        List<HazardZone> zones = Boolean.TRUE.equals(activeOnly)
                ? hazardZoneRepository.findByIsActiveTrue()
                : hazardZoneRepository.findAll();

        if (state != null && !"ALL".equalsIgnoreCase(state) && !state.isBlank()) {
            zones = zones.stream()
                    .filter(z -> state.equalsIgnoreCase(z.getState()))
                    .collect(Collectors.toList());
        }

        if (type != null && !"ALL".equalsIgnoreCase(type) && !type.isBlank()) {
            zones = zones.stream()
                    .filter(z -> type.equalsIgnoreCase(z.getHazardType()))
                    .collect(Collectors.toList());
        }

        return zones;
    }

    public List<HazardZoneDetailDto> getRedZoneDetails(String state, String type, Boolean activeOnly) {
        List<HazardZone> zones = getZones(state, type, activeOnly);
        List<Habitation> allHabitations = habitationRepository.findAll();

        return zones.stream().map(zone -> {
            HazardZoneDetailDto dto = toDetailDto(zone);
            try {
                Geometry geom = geospatialService.parseGeoJsonToGeometry(zone.getBoundaryGeoJson());
                List<Habitation> affected = allHabitations.stream()
                        .filter(h -> geospatialService.isPointInPolygon(h.getLatitude(), h.getLongitude(), geom)
                                || geospatialService.distanceToBoundaryMeters(h.getLatitude(), h.getLongitude(), geom) <= (zone.getBufferRadiusKm() * 1000.0))
                        .collect(Collectors.toList());

                dto.setAffectedHabitationsCount(affected.size());
                dto.setAffectedTotalPopulation(affected.stream().mapToLong(Habitation::getTotalPopulation).sum());
                dto.setAffectedVulnerablePopulation(affected.stream().mapToLong(Habitation::getVulnerablePopulation).sum());
            } catch (Exception e) {
                dto.setAffectedHabitationsCount(0);
            }
            return dto;
        }).collect(Collectors.toList());
    }

    public Optional<HazardZoneDetailDto> getZoneDetailById(String id) {
        return hazardZoneRepository.findById(id).map(zone -> {
            HazardZoneDetailDto dto = toDetailDto(zone);
            List<Habitation> allHabitations = habitationRepository.findAll();
            try {
                Geometry geom = geospatialService.parseGeoJsonToGeometry(zone.getBoundaryGeoJson());
                List<Habitation> affected = allHabitations.stream()
                        .filter(h -> geospatialService.isPointInPolygon(h.getLatitude(), h.getLongitude(), geom)
                                || geospatialService.distanceToBoundaryMeters(h.getLatitude(), h.getLongitude(), geom) <= (zone.getBufferRadiusKm() * 1000.0))
                        .collect(Collectors.toList());

                dto.setAffectedHabitationsCount(affected.size());
                dto.setAffectedTotalPopulation(affected.stream().mapToLong(Habitation::getTotalPopulation).sum());
                dto.setAffectedVulnerablePopulation(affected.stream().mapToLong(Habitation::getVulnerablePopulation).sum());
            } catch (Exception e) {
                dto.setAffectedHabitationsCount(0);
            }
            return dto;
        });
    }

    public List<HabitationAtRiskDto> getAffectedHabitationsForZone(String zoneId) {
        HazardZone zone = hazardZoneRepository.findById(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("Hazard zone not found with ID: " + zoneId));

        List<Habitation> habitations = habitationRepository.findAll();
        return geospatialService.evaluateHabitationRisks(habitations, List.of(zone));
    }

    public GeoJsonFeatureCollectionDto getGeoJson(String state, String type) {
        List<HazardZone> zones = getZones(state, type, true);
        return geospatialService.toFeatureCollection(zones);
    }

    @Transactional
    public HazardZoneDetailDto createHazardZone(CreateHazardZoneRequest request) {
        if (hazardZoneRepository.findByZoneCode(request.getZoneCode()).isPresent()) {
            throw new IllegalArgumentException("Hazard zone code already exists: " + request.getZoneCode());
        }

        // Validate GeoJSON format and calculate area/perimeter
        Geometry geom = geospatialService.parseGeoJsonToGeometry(request.getBoundaryGeoJson());
        double area = geospatialService.calculateAreaSqKm(geom);
        double perimeter = geospatialService.calculatePerimeterKm(geom);

        HazardZone zone = new HazardZone();
        zone.setId(UUID.randomUUID().toString());
        zone.setZoneCode(request.getZoneCode());
        zone.setHazardType(request.getHazardType().toUpperCase());
        zone.setSeverityLevel(request.getSeverityLevel().toUpperCase());
        zone.setState(request.getState());
        zone.setDistrict(request.getDistrict());
        zone.setCenterLat(request.getCenterLat());
        zone.setCenterLng(request.getCenterLng());
        zone.setBufferRadiusKm(request.getBufferRadiusKm());
        zone.setBoundaryGeoJson(request.getBoundaryGeoJson());
        zone.setIsActive(true);
        zone.setIsEstimatedBuffer(Boolean.TRUE.equals(request.getIsEstimatedBuffer()));
        zone.setAreaSqKm(area);
        zone.setPerimeterKm(perimeter);

        HazardZone saved = hazardZoneRepository.save(zone);
        return toDetailDto(saved);
    }

    private HazardZoneDetailDto toDetailDto(HazardZone zone) {
        HazardZoneDetailDto dto = new HazardZoneDetailDto();
        dto.setId(zone.getId());
        dto.setZoneCode(zone.getZoneCode());
        dto.setHazardType(zone.getHazardType());
        dto.setSeverityLevel(zone.getSeverityLevel());
        dto.setState(zone.getState());
        dto.setDistrict(zone.getDistrict());
        dto.setCenterLat(zone.getCenterLat());
        dto.setCenterLng(zone.getCenterLng());
        dto.setBufferRadiusKm(zone.getBufferRadiusKm());
        dto.setBoundaryGeoJson(zone.getBoundaryGeoJson());
        dto.setIsActive(zone.getIsActive());
        dto.setIsEstimatedBuffer(zone.getIsEstimatedBuffer());
        dto.setAreaSqKm(zone.getAreaSqKm());
        dto.setPerimeterKm(zone.getPerimeterKm());
        dto.setCreatedAt(zone.getCreatedAt());
        return dto;
    }

    public long countActiveZones() {
        return hazardZoneRepository.count();
    }
}
