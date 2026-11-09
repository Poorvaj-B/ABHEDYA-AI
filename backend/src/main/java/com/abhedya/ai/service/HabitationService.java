package com.abhedya.ai.service;

import com.abhedya.ai.dto.HabitationAtRiskDto;
import com.abhedya.ai.model.Habitation;
import com.abhedya.ai.model.HazardZone;
import com.abhedya.ai.repository.HabitationRepository;
import com.abhedya.ai.repository.HazardZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HabitationService {

    private final HabitationRepository habitationRepository;
    private final HazardZoneRepository hazardZoneRepository;
    private final GeospatialService geospatialService;

    public HabitationService(HabitationRepository habitationRepository,
                             HazardZoneRepository hazardZoneRepository,
                             GeospatialService geospatialService) {
        this.habitationRepository = habitationRepository;
        this.hazardZoneRepository = hazardZoneRepository;
        this.geospatialService = geospatialService;
    }

    public List<Habitation> getAllHabitations() {
        return habitationRepository.findAll();
    }

    public Optional<Habitation> getHabitationById(String id) {
        return habitationRepository.findById(id);
    }

    public List<Habitation> getHabitations(String state, String district, Boolean inRedZoneOnly) {
        List<Habitation> list = habitationRepository.findAll();

        if (Boolean.TRUE.equals(inRedZoneOnly)) {
            list = list.stream().filter(Habitation::getInRedZone).collect(Collectors.toList());
        }

        if (state != null && !"ALL".equalsIgnoreCase(state) && !state.isBlank()) {
            list = list.stream().filter(h -> state.equalsIgnoreCase(h.getState())).collect(Collectors.toList());
        }

        if (district != null && !"ALL".equalsIgnoreCase(district) && !district.isBlank()) {
            list = list.stream().filter(h -> district.equalsIgnoreCase(h.getDistrict())).collect(Collectors.toList());
        }

        return list;
    }

    public List<Habitation> getHabitationsByState(String state) {
        return getHabitations(state, null, false);
    }

    public List<Habitation> getRedZoneHabitations() {
        return habitationRepository.findByInRedZoneTrue();
    }

    public List<Habitation> getHabitationsExceedingCapacity() {
        return habitationRepository.findHabitationsExceedingCapacity();
    }

    public List<HabitationAtRiskDto> getHabitationsAtRisk(String state, String hazardType, String severity) {
        List<Habitation> habitations = habitationRepository.findAll();
        List<HazardZone> activeZones = hazardZoneRepository.findByIsActiveTrue();

        List<HabitationAtRiskDto> atRiskList = geospatialService.evaluateHabitationRisks(habitations, activeZones);

        if (state != null && !"ALL".equalsIgnoreCase(state) && !state.isBlank()) {
            atRiskList = atRiskList.stream().filter(d -> state.equalsIgnoreCase(d.getState())).collect(Collectors.toList());
        }

        if (hazardType != null && !"ALL".equalsIgnoreCase(hazardType) && !hazardType.isBlank()) {
            atRiskList = atRiskList.stream().filter(d -> hazardType.equalsIgnoreCase(d.getHazardType())).collect(Collectors.toList());
        }

        if (severity != null && !"ALL".equalsIgnoreCase(severity) && !severity.isBlank()) {
            atRiskList = atRiskList.stream().filter(d -> severity.equalsIgnoreCase(d.getSeverityLevel())).collect(Collectors.toList());
        }

        return atRiskList;
    }

    public long countAll() {
        return habitationRepository.count();
    }

    public long countRedZone() {
        return habitationRepository.countRedZoneHabitations();
    }

    public long sumTotalPopulation() {
        return habitationRepository.sumTotalPopulation();
    }

    public long sumVulnerablePopulation() {
        return habitationRepository.sumVulnerablePopulation();
    }
}
