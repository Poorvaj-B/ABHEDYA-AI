package com.abhedya.ai.service;

import com.abhedya.ai.dto.ShelterCapacityAuditDto;
import com.abhedya.ai.model.Shelter;
import com.abhedya.ai.repository.ShelterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShelterCapacityService {

    // Sphere Project Minimum Standards
    public static final double SPHERE_WATER_LITERS_PER_PERSON_DAY = 15.0;
    public static final int SPHERE_PERSONS_PER_SANITATION_UNIT = 20;

    private final ShelterRepository shelterRepository;

    public ShelterCapacityService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    public List<ShelterCapacityAuditDto> auditAllShelters(String state) {
        List<Shelter> shelters = (state != null && !"ALL".equalsIgnoreCase(state) && !state.isBlank())
                ? shelterRepository.findByStateIgnoreCase(state)
                : shelterRepository.findAll();

        return shelters.stream().map(this::auditShelter).collect(Collectors.toList());
    }

    public ShelterCapacityAuditDto auditShelterById(String shelterId) {
        Shelter shelter = shelterRepository.findById(shelterId)
                .orElseThrow(() -> new IllegalArgumentException("Shelter not found with ID: " + shelterId));
        return auditShelter(shelter);
    }

    public ShelterCapacityAuditDto auditShelter(Shelter shelter) {
        // Resource-Supported Capacities
        double water = shelter.getPotableWaterLitersDay() != null ? shelter.getPotableWaterLitersDay() : 0.0;
        int waterSupported = (int) Math.floor(water / SPHERE_WATER_LITERS_PER_PERSON_DAY);

        int sanitationUnits = shelter.getSanitationUnits() != null ? shelter.getSanitationUnits() : 0;
        int sanitationSupported = sanitationUnits * SPHERE_PERSONS_PER_SANITATION_UNIT;

        int physicalMax = shelter.getMaxCapacity() != null ? shelter.getMaxCapacity() : 0;

        // Effective Safe Capacity is the bottleneck of physical footprint, water, and sanitation
        int effectiveSafeCapacity = Math.min(physicalMax, Math.min(waterSupported > 0 ? waterSupported : physicalMax, sanitationSupported > 0 ? sanitationSupported : physicalMax));

        // Determine Limiting Factor
        String limitingFactor = "PHYSICAL_SPACE";
        if (waterSupported > 0 && waterSupported < physicalMax && waterSupported <= sanitationSupported) {
            limitingFactor = "WATER_SUPPLY";
        } else if (sanitationSupported > 0 && sanitationSupported < physicalMax && sanitationSupported < waterSupported) {
            limitingFactor = "SANITATION";
        }

        int occupancy = shelter.getCurrentOccupancy() != null ? shelter.getCurrentOccupancy() : 0;
        int remainingHeadroom = Math.max(0, effectiveSafeCapacity - occupancy);

        double occupancyRate = effectiveSafeCapacity > 0 ? ((double) occupancy / effectiveSafeCapacity) * 100.0 : 0.0;

        String stressLevel;
        boolean intakeAllowed;
        if (occupancy > effectiveSafeCapacity) {
            stressLevel = "EXCEEDED_UNSAFE";
            intakeAllowed = false;
        } else if (occupancyRate >= 90.0) {
            stressLevel = "CRITICAL";
            intakeAllowed = remainingHeadroom > 0;
        } else if (occupancyRate >= 70.0) {
            stressLevel = "CONGESTED";
            intakeAllowed = true;
        } else {
            stressLevel = "OPTIMAL";
            intakeAllowed = true;
        }

        ShelterCapacityAuditDto dto = new ShelterCapacityAuditDto();
        dto.setShelterId(shelter.getId());
        dto.setName(shelter.getName());
        dto.setDistrict(shelter.getDistrict());
        dto.setState(shelter.getState());
        dto.setLatitude(shelter.getLatitude());
        dto.setLongitude(shelter.getLongitude());
        dto.setMaxCapacity(physicalMax);
        dto.setCurrentOccupancy(occupancy);
        dto.setAvailableBeds(shelter.getAvailableBeds() != null ? shelter.getAvailableBeds() : remainingHeadroom);
        dto.setPotableWaterLitersDay(water);
        dto.setWaterSupportedCapacity(waterSupported);
        dto.setSanitationUnits(sanitationUnits);
        dto.setSanitationSupportedCapacity(sanitationSupported);
        dto.setMedicalSupportLevel(shelter.getMedicalSupportLevel());
        dto.setHasBackupPower(Boolean.TRUE.equals(shelter.getHasBackupPower()));
        dto.setEffectiveSafeCapacity(effectiveSafeCapacity);
        dto.setCapacityLimitingFactor(limitingFactor);
        dto.setRemainingSafeHeadroom(remainingHeadroom);
        dto.setOccupancyRatePercent(Math.round(occupancyRate * 10.0) / 10.0);
        dto.setOccupancyStressLevel(stressLevel);
        dto.setIsVerifiedOperational(Boolean.TRUE.equals(shelter.getIsVerifiedOperational()));
        dto.setIsIntakeAllowed(intakeAllowed);

        return dto;
    }

    @Transactional
    public ShelterCapacityAuditDto updateOccupancy(String shelterId, Integer newOccupancy) {
        Shelter shelter = shelterRepository.findById(shelterId)
                .orElseThrow(() -> new IllegalArgumentException("Shelter not found with ID: " + shelterId));

        if (newOccupancy < 0) {
            throw new IllegalArgumentException("Occupancy cannot be negative: " + newOccupancy);
        }

        // Calculate effective safe capacity to prevent dangerous overloading
        double water = shelter.getPotableWaterLitersDay() != null ? shelter.getPotableWaterLitersDay() : 0.0;
        int waterSupported = (int) Math.floor(water / SPHERE_WATER_LITERS_PER_PERSON_DAY);
        int sanitationUnits = shelter.getSanitationUnits() != null ? shelter.getSanitationUnits() : 0;
        int sanitationSupported = sanitationUnits * SPHERE_PERSONS_PER_SANITATION_UNIT;
        int physicalMax = shelter.getMaxCapacity() != null ? shelter.getMaxCapacity() : 0;
        int effectiveSafeCapacity = Math.min(physicalMax, Math.min(waterSupported > 0 ? waterSupported : physicalMax, sanitationSupported > 0 ? sanitationSupported : physicalMax));

        if (newOccupancy > effectiveSafeCapacity) {
            throw new IllegalArgumentException("Cannot exceed shelter safe capacity! Requested: " + newOccupancy + ", Effective Safe Capacity: " + effectiveSafeCapacity);
        }

        shelter.setCurrentOccupancy(newOccupancy);
        shelter.setAvailableBeds(Math.max(0, effectiveSafeCapacity - newOccupancy));
        shelter.setUpdatedAt(LocalDateTime.now());

        Shelter saved = shelterRepository.save(shelter);
        return auditShelter(saved);
    }
}
