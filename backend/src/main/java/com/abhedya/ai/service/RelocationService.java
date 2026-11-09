package com.abhedya.ai.service;

import com.abhedya.ai.client.OsrmClient;
import com.abhedya.ai.dto.*;
import com.abhedya.ai.model.*;
import com.abhedya.ai.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RelocationService {

    private static final Logger log = LoggerFactory.getLogger(RelocationService.class);

    private final HabitationRepository habitationRepository;
    private final ShelterRepository shelterRepository;
    private final HazardZoneRepository hazardZoneRepository;
    private final RelocationPlanRepository relocationPlanRepository;
    private final RelocationAssignmentRepository relocationAssignmentRepository;
    private final EvacuationRouteRepository evacuationRouteRepository;
    private final GeospatialService geospatialService;
    private final OsrmClient osrmClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RelocationService(
            HabitationRepository habitationRepository,
            ShelterRepository shelterRepository,
            HazardZoneRepository hazardZoneRepository,
            RelocationPlanRepository relocationPlanRepository,
            RelocationAssignmentRepository relocationAssignmentRepository,
            EvacuationRouteRepository evacuationRouteRepository,
            GeospatialService geospatialService,
            OsrmClient osrmClient) {

        this.habitationRepository = habitationRepository;
        this.shelterRepository = shelterRepository;
        this.hazardZoneRepository = hazardZoneRepository;
        this.relocationPlanRepository = relocationPlanRepository;
        this.relocationAssignmentRepository = relocationAssignmentRepository;
        this.evacuationRouteRepository = evacuationRouteRepository;
        this.geospatialService = geospatialService;
        this.osrmClient = osrmClient;
    }

    /**
     * Evaluates and ranks suitable safe shelters for a vulnerable habitation.
     */
    @Transactional(readOnly = true)
    public List<ShelterRecommendationDto> getShelterRecommendations(String habitationId) {
        Habitation hab = habitationRepository.findById(habitationId)
                .orElseThrow(() -> new IllegalArgumentException("Habitation not found with ID: " + habitationId));

        List<HazardZone> activeZones = hazardZoneRepository.findByIsActiveTrue();
        List<Shelter> shelters = shelterRepository.findAll();

        Map<String, org.locationtech.jts.geom.Geometry> activeGeoms = new HashMap<>();
        for (HazardZone z : activeZones) {
            try {
                if (z.getBoundaryGeoJson() != null && !z.getBoundaryGeoJson().isBlank()) {
                    activeGeoms.put(z.getId(), geospatialService.parseGeoJsonToGeometry(z.getBoundaryGeoJson()));
                }
            } catch (Exception ignored) {
            }
        }

        List<ShelterRecommendationDto> recommendations = new ArrayList<>();

        // Progressive geographical search radii (in kilometres)
        double[] searchRadiiKm = { 50, 100, 200, 500, 1000 };

        // Desired minimum number of recommendations
        int minimumRecommendations = 5;

        for (double searchRadiusKm : searchRadiiKm) {

            // Recalculate recommendations for the current radius
            recommendations.clear();

            for (Shelter s : shelters) {

                // Validate coordinates before distance calculation
                if (hab.getLatitude() == null || hab.getLongitude() == null
                        || s.getLatitude() == null || s.getLongitude() == null) {
                    continue;
                }

                double distanceKm = Math.round(
                        geospatialService.haversineDistanceMeters(
                                hab.getLatitude(), hab.getLongitude(),
                                s.getLatitude(), s.getLongitude()) / 1000.0 * 10.0)
                        / 10.0;

                // Exclude shelters outside the current search radius
                if (distanceKm > searchRadiusKm) {
                    continue;
                }
                // Exclude shelters outside the current search radius
                if (distanceKm > searchRadiusKm) {
                    continue;
                }

                // Check if shelter itself lies inside an active hazard zone
                boolean insideHazard = false;
                for (org.locationtech.jts.geom.Geometry geom : activeGeoms.values()) {
                    if (geospatialService.isPointInPolygon(s.getLatitude(), s.getLongitude(), geom)) {
                        insideHazard = true;
                        break;
                    }
                }

                int safeCap = s.getEffectiveSafeCapacity() != null && s.getEffectiveSafeCapacity() > 0
                        ? s.getEffectiveSafeCapacity()
                        : s.getMaxCapacity();
                int currentOcc = s.getCurrentOccupancy() != null ? s.getCurrentOccupancy() : 0;
                int remainingHeadroom = Math.max(0, safeCap - currentOcc);
                // Only recommend operational shelters with available capacity
                if (!"OPERATIONAL".equalsIgnoreCase(s.getStatus())
                        || remainingHeadroom <= 0) {
                    continue;
                }

                // Never recommend shelters located inside active hazard zones
                if (insideHazard) {
                    continue;
                }

                // Compute Multi-Factor Suitability Score (0 to 100)
                // 1. Proximity Score (40 pts) - closer is better (up to 200km)
                double proximityScore = 40.0 * Math.max(0.0, 1.0 - (distanceKm / 200.0));

                // 2. Capacity Headroom Score (35 pts)
                double capacityScore = 35.0 * Math.min(1.0, (double) remainingHeadroom / 400.0);

                // 3. Medical Capability Score (15 pts)
                double medicalScore = switch (s.getMedicalSupportLevel() != null
                        ? s.getMedicalSupportLevel().toUpperCase()
                        : "BASIC") {
                    case "ADVANCED" -> 15.0;
                    case "INTERMEDIATE" -> 10.0;
                    default -> 5.0;
                };

                // 4. Operational Safety Score (10 pts)
                double safetyScore = (Boolean.TRUE.equals(s.getHasBackupPower()) ? 5.0 : 0.0)
                        + (insideHazard ? 0.0 : 5.0);

                double suitability = proximityScore + capacityScore + medicalScore + safetyScore;

                suitability = Math.round(suitability * 10.0) / 10.0;

                double estMinutes = Math.round(((distanceKm * 1.25 / 45.0) * 60.0) * 10.0) / 10.0;

                StringBuilder rationale = new StringBuilder();
                if (insideHazard) {
                    rationale.append("WARNING: Shelter is located inside an active hazard zone perimeter! ");
                } else {
                    rationale.append("Safe green-zone shelter. ");
                }
                rationale.append(String.format(Locale.US,
                        "Distance: %.1f km (est. %.0f min). Available headroom: %d persons. Medical tier: %s.",
                        distanceKm, estMinutes, remainingHeadroom, s.getMedicalSupportLevel()));

                ShelterRecommendationDto dto = new ShelterRecommendationDto();
                dto.setShelterId(s.getId());
                dto.setShelterName(s.getName());
                dto.setDistrict(s.getDistrict());
                dto.setState(s.getState());
                dto.setLatitude(s.getLatitude());
                dto.setLongitude(s.getLongitude());
                dto.setMaxCapacity(s.getMaxCapacity());
                dto.setCurrentOccupancy(currentOcc);
                dto.setAvailableBeds(s.getAvailableBeds());
                dto.setEffectiveSafeCapacity(safeCap);
                dto.setRemainingSafeCapacity(remainingHeadroom);
                dto.setDistanceKm(distanceKm);
                dto.setEstimatedTravelMinutes(estMinutes);
                dto.setSuitabilityScore(suitability);
                dto.setMedicalSupportLevel(s.getMedicalSupportLevel());
                dto.setHasBackupPower(s.getHasBackupPower());
                dto.setInsideHazardZone(insideHazard);
                dto.setRecommendationRationale(rationale.toString());
                recommendations.add(dto);
            }

            // Stop expanding once enough suitable shelters are found
            if (recommendations.size() >= minimumRecommendations) {
                break;
            }
        }

        recommendations.sort(Comparator.comparingDouble(ShelterRecommendationDto::getSuitabilityScore).reversed());
        return recommendations;
    }

    /**
     * Creates an intelligent relocation plan with OSRM road-network evacuation
     * route and fleet sizing.
     */
    public RelocationPlanDto createRelocationPlan(CreateRelocationPlanRequest request) {
        Habitation hab = habitationRepository.findById(request.getHabitationId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Habitation not found with ID: " + request.getHabitationId()));

        // Determine evacuee cohort size
        int evacueesNeeded;
        if (request.getEvacueeCount() != null && request.getEvacueeCount() > 0) {
            evacueesNeeded = request.getEvacueeCount();
        } else {
            int excess = Math.max(0, hab.getTotalPopulation() - hab.getSafeCarryingCapacity());
            if (excess > 0) {
                evacueesNeeded = excess;
            } else if (hab.getVulnerablePopulation() != null && hab.getVulnerablePopulation() > 0) {
                evacueesNeeded = hab.getVulnerablePopulation();
            } else {
                evacueesNeeded = 100; // Minimum default evacuation cohort
            }
        }

        // Identify target safe shelter
        Shelter destinationShelter;
        if (request.getTargetShelterId() != null && !request.getTargetShelterId().isBlank()) {
            destinationShelter = shelterRepository.findById(request.getTargetShelterId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Specified shelter not found: " + request.getTargetShelterId()));
        } else {
            List<ShelterRecommendationDto> recs = getShelterRecommendations(hab.getId());
            if (recs.isEmpty()) {
                throw new IllegalStateException("No candidate shelters available in system.");
            }
            destinationShelter = shelterRepository.findById(recs.get(0).getShelterId())
                    .orElseThrow(() -> new IllegalStateException("Recommended shelter not found."));
        }

        // Validate shelter capacity & increment occupancy
        int currentOcc = destinationShelter.getCurrentOccupancy() != null ? destinationShelter.getCurrentOccupancy()
                : 0;
        destinationShelter.setCurrentOccupancy(currentOcc + evacueesNeeded);
        destinationShelter.setAvailableBeds(Math.max(0, destinationShelter.getAvailableBeds() - evacueesNeeded));
        destinationShelter.setUpdatedAt(LocalDateTime.now());
        shelterRepository.save(destinationShelter);

        // Calculate actual road network evacuation route via OSRM
        OsrmClient.OsrmRouteResult routeResult = osrmClient.calculateRoute(
                hab.getLatitude(), hab.getLongitude(),
                destinationShelter.getLatitude(), destinationShelter.getLongitude());

        // Logistical Fleet Requirements (Sphere Standards: 40 passengers/bus, 1
        // ambulance/20 vulnerable)
        int busesRequired = (int) Math.ceil((double) evacueesNeeded / 40.0);
        int vulnerableCount = hab.getVulnerablePopulation() != null
                ? Math.min(evacueesNeeded, hab.getVulnerablePopulation())
                : (int) Math.ceil(evacueesNeeded * 0.25);
        int ambulancesRequired = Math.max(1, (int) Math.ceil((double) vulnerableCount / 20.0));

        // Generate Relocation Plan
        String stateCode = hab.getState() != null && hab.getState().length() >= 2
                ? hab.getState().substring(0, 2).toUpperCase()
                : "IN";
        String planCode = String.format("PLAN-%s-%05d", stateCode, Math.abs(UUID.randomUUID().hashCode() % 100000));

        RelocationPlan plan = new RelocationPlan();
        plan.setId(UUID.randomUUID().toString());
        plan.setPlanCode(planCode);
        plan.setStatus("DISPATCHED");
        plan.setTotalEvacuees(evacueesNeeded);
        plan.setTotalBusesRequired(busesRequired);
        plan.setTotalAmbulancesRequired(ambulancesRequired);
        plan.setNotes(request.getNotes() != null ? request.getNotes()
                : "Priority evacuation from " + hab.getName() + " to safe shelter " + destinationShelter.getName());
        plan.setApprovedBy(request.getApprovedBy() != null ? request.getApprovedBy() : "ABHEDYA Command System");
        plan.setGeneratedAt(LocalDateTime.now());

        RelocationPlan savedPlan = relocationPlanRepository.save(plan);

        // Generate Relocation Assignment
        RelocationAssignment assignment = new RelocationAssignment();
        assignment.setId(UUID.randomUUID().toString());
        assignment.setPlan(savedPlan);
        assignment.setSourceHabitation(hab);
        assignment.setDestinationShelter(destinationShelter);
        assignment.setAllocatedEvacuees(evacueesNeeded);
        assignment.setVulnerableEvacuees(vulnerableCount);
        assignment.setPriorityTier(Boolean.TRUE.equals(hab.getInRedZone()) ? "IMMEDIATE" : "PRIORITY_1");
        assignment.setAssignmentStatus("EN_ROUTE");
        assignment.setCreatedAt(LocalDateTime.now());

        RelocationAssignment savedAssignment = relocationAssignmentRepository.save(assignment);
        savedPlan.getAssignments().add(savedAssignment);

        // Evaluate route safety against active hazard zones
        List<HazardZone> activeHazardZones = hazardZoneRepository.findByIsActiveTrue();
        GeospatialService.RouteSafetyAssessment safetyAssessment = geospatialService.evaluateRouteSafety(
                routeResult.geoJson(), activeHazardZones);

        // Persist Evacuation Route
        EvacuationRoute route = new EvacuationRoute();
        route.setId(UUID.randomUUID().toString());
        route.setPlan(savedPlan);
        route.setSourceHabitation(hab);
        route.setDestinationShelter(destinationShelter);
        route.setRouteGeoJson(routeResult.geoJson());
        route.setDistanceKm(routeResult.distanceKm());
        route.setDurationMinutes(routeResult.durationMinutes());
        route.setAvoidsRedZones(safetyAssessment.avoidsRedZones());
        route.setValidatedSafe(safetyAssessment.validatedSafe());
        route.setSafetyNotes(safetyAssessment.safetyNotes() + " [Engine: " + routeResult.source() + "]");
        route.setCreatedAt(LocalDateTime.now());

        EvacuationRoute savedRoute = evacuationRouteRepository.save(route);

        return toPlanDto(savedPlan, List.of(savedAssignment), List.of(savedRoute));
    }

    /**
     * Calculates evacuation route between habitation and shelter on-demand without
     * persisting a plan.
     */
    @Transactional(readOnly = true)
    public EvacuationRouteDto calculateOnDemandRoute(String habitationId, String shelterId) {
        Habitation hab = habitationRepository.findById(habitationId)
                .orElseThrow(() -> new IllegalArgumentException("Habitation not found: " + habitationId));
        Shelter shelter = shelterRepository.findById(shelterId)
                .orElseThrow(() -> new IllegalArgumentException("Shelter not found: " + shelterId));

        OsrmClient.OsrmRouteResult result = osrmClient.calculateRoute(
                hab.getLatitude(), hab.getLongitude(),
                shelter.getLatitude(), shelter.getLongitude());

        List<HazardZone> activeHazardZones = hazardZoneRepository.findByIsActiveTrue();
        GeospatialService.RouteSafetyAssessment safetyAssessment = geospatialService.evaluateRouteSafety(
                result.geoJson(), activeHazardZones);

        EvacuationRouteDto dto = new EvacuationRouteDto();
        dto.setSourceHabitationId(hab.getId());
        dto.setSourceHabitationName(hab.getName());
        dto.setSourceLat(hab.getLatitude());
        dto.setSourceLng(hab.getLongitude());
        dto.setDestinationShelterId(shelter.getId());
        dto.setDestinationShelterName(shelter.getName());
        dto.setDestinationLat(shelter.getLatitude());
        dto.setDestinationLng(shelter.getLongitude());
        dto.setDistanceKm(result.distanceKm());
        dto.setDurationMinutes(result.durationMinutes());
        dto.setRouteGeoJson(result.geoJson());
        dto.setRouteSource(result.source());
        dto.setAvoidsRedZones(safetyAssessment.avoidsRedZones());
        dto.setValidatedSafe(safetyAssessment.validatedSafe());
        dto.setSafetyNotes(safetyAssessment.safetyNotes() + " [Engine: " + result.source() + "]");
        return dto;
    }

    /**
     * Retrieves all evacuation routes as a GeoJSON FeatureCollection for Leaflet
     * map overlay.
     */
    @Transactional(readOnly = true)
    public GeoJsonFeatureCollectionDto getEvacuationRoutesGeoJson() {
        List<EvacuationRoute> routes = evacuationRouteRepository.findAll();
        List<GeoJsonFeatureDto> features = new ArrayList<>();

        for (EvacuationRoute r : routes) {
            try {
                Object geometry = objectMapper.readValue(r.getRouteGeoJson(), Object.class);

                Map<String, Object> props = new LinkedHashMap<>();
                props.put("routeId", r.getId());
                props.put("planId", r.getPlan() != null ? r.getPlan().getId() : null);
                props.put("sourceHabitation",
                        r.getSourceHabitation() != null ? r.getSourceHabitation().getName() : null);
                props.put("destinationShelter",
                        r.getDestinationShelter() != null ? r.getDestinationShelter().getName() : null);
                props.put("distanceKm", r.getDistanceKm());
                props.put("durationMinutes", r.getDurationMinutes());
                props.put("avoidsRedZones", r.getAvoidsRedZones());
                props.put("safetyNotes", r.getSafetyNotes());

                features.add(new GeoJsonFeatureDto("Feature", geometry, props));
            } catch (Exception e) {
                log.warn("Failed to serialize route {} to GeoJSON: {}", r.getId(), e.getMessage());
            }
        }

        return new GeoJsonFeatureCollectionDto(features);
    }

    /**
     * Retrieves all relocation plans.
     */
    @Transactional(readOnly = true)
    public List<RelocationPlanDto> getAllRelocationPlans() {
        List<RelocationPlan> plans = relocationPlanRepository.findAll();
        return plans.stream().map(p -> {
            List<RelocationAssignment> assignments = relocationAssignmentRepository.findByPlanId(p.getId());
            List<EvacuationRoute> routes = evacuationRouteRepository.findByPlanId(p.getId());
            return toPlanDto(p, assignments, routes);
        }).collect(Collectors.toList());
    }

    /**
     * Retrieves a single relocation plan by ID.
     */
    @Transactional(readOnly = true)
    public Optional<RelocationPlanDto> getRelocationPlanById(String id) {
        return relocationPlanRepository.findById(id).map(p -> {
            List<RelocationAssignment> assignments = relocationAssignmentRepository.findByPlanId(p.getId());
            List<EvacuationRoute> routes = evacuationRouteRepository.findByPlanId(p.getId());
            return toPlanDto(p, assignments, routes);
        });
    }

    /**
     * Updates an evacuation plan status and synchronizes convoy assignments.
     */
    public RelocationPlanDto updatePlanStatus(String id, String newStatus) {
        RelocationPlan plan = relocationPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relocation plan not found with ID: " + id));

        String sanitizedStatus = newStatus.trim().toUpperCase();
        plan.setStatus(sanitizedStatus);
        RelocationPlan saved = relocationPlanRepository.save(plan);

        List<RelocationAssignment> assignments = relocationAssignmentRepository.findByPlanId(id);
        String assignmentStatus = switch (sanitizedStatus) {
            case "COMPLETED" -> "ARRIVED";
            case "EN_ROUTE" -> "IN_TRANSIT";
            case "CANCELLED" -> "CANCELLED";
            default -> "EN_ROUTE";
        };

        for (RelocationAssignment a : assignments) {
            a.setAssignmentStatus(assignmentStatus);
            relocationAssignmentRepository.save(a);
        }

        List<EvacuationRoute> routes = evacuationRouteRepository.findByPlanId(id);
        return toPlanDto(saved, assignments, routes);
    }

    private RelocationPlanDto toPlanDto(RelocationPlan plan, List<RelocationAssignment> assignments,
            List<EvacuationRoute> routes) {
        RelocationPlanDto dto = new RelocationPlanDto();
        dto.setPlanId(plan.getId());
        dto.setPlanCode(plan.getPlanCode());
        dto.setStatus(plan.getStatus());
        dto.setTotalEvacuees(plan.getTotalEvacuees());
        dto.setTotalBusesRequired(plan.getTotalBusesRequired());
        dto.setTotalAmbulancesRequired(plan.getTotalAmbulancesRequired());
        dto.setNotes(plan.getNotes());
        dto.setApprovedBy(plan.getApprovedBy());
        dto.setGeneratedAt(plan.getGeneratedAt());

        if (assignments != null) {
            dto.setAssignments(assignments.stream().map(a -> {
                RelocationAssignmentDto adto = new RelocationAssignmentDto();
                adto.setAssignmentId(a.getId());
                adto.setPlanId(plan.getId());
                adto.setSourceHabitationId(a.getSourceHabitation() != null ? a.getSourceHabitation().getId() : null);
                adto.setSourceHabitationName(
                        a.getSourceHabitation() != null ? a.getSourceHabitation().getName() : null);
                adto.setDestinationShelterId(
                        a.getDestinationShelter() != null ? a.getDestinationShelter().getId() : null);
                adto.setDestinationShelterName(
                        a.getDestinationShelter() != null ? a.getDestinationShelter().getName() : null);
                adto.setAllocatedEvacuees(a.getAllocatedEvacuees());
                adto.setVulnerableEvacuees(a.getVulnerableEvacuees());
                adto.setPriorityTier(a.getPriorityTier());
                adto.setAssignmentStatus(a.getAssignmentStatus());
                adto.setCreatedAt(a.getCreatedAt());

                int buses = (int) Math.ceil((double) a.getAllocatedEvacuees() / 40.0);
                int ambulances = Math.max(1, (int) Math.ceil((double) a.getVulnerableEvacuees() / 20.0));
                adto.setBusesRequired(buses);
                adto.setAmbulancesRequired(ambulances);
                return adto;
            }).collect(Collectors.toList()));
        }

        if (routes != null) {
            dto.setRoutes(routes.stream().map(r -> {
                EvacuationRouteDto rdto = new EvacuationRouteDto();
                rdto.setRouteId(r.getId());
                rdto.setPlanId(plan.getId());
                rdto.setPlanCode(plan.getPlanCode());
                rdto.setSourceHabitationId(r.getSourceHabitation() != null ? r.getSourceHabitation().getId() : null);
                rdto.setSourceHabitationName(
                        r.getSourceHabitation() != null ? r.getSourceHabitation().getName() : null);
                rdto.setDestinationShelterId(
                        r.getDestinationShelter() != null ? r.getDestinationShelter().getId() : null);
                rdto.setDestinationShelterName(
                        r.getDestinationShelter() != null ? r.getDestinationShelter().getName() : null);
                rdto.setDistanceKm(r.getDistanceKm());
                rdto.setDurationMinutes(r.getDurationMinutes());
                rdto.setRouteGeoJson(r.getRouteGeoJson());
                rdto.setAvoidsRedZones(r.getAvoidsRedZones());
                rdto.setValidatedSafe(r.getValidatedSafe());
                rdto.setSafetyNotes(r.getSafetyNotes());
                rdto.setCreatedAt(r.getCreatedAt());
                return rdto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}
