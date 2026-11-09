package com.abhedya.ai.service;

import com.abhedya.ai.model.Shelter;
import com.abhedya.ai.repository.ShelterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ShelterService {

    private final ShelterRepository shelterRepository;

    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    public List<Shelter> getAllShelters() {
        return shelterRepository.findAll();
    }

    public Optional<Shelter> getShelterById(String id) {
        return shelterRepository.findById(id);
    }

    public List<Shelter> getAvailableShelters() {
        return shelterRepository.findAvailableShelters();
    }

    public List<Shelter> getSheltersByState(String state) {
        if ("ALL".equalsIgnoreCase(state) || state == null || state.isBlank()) {
            return shelterRepository.findAll();
        }
        return shelterRepository.findByStateIgnoreCase(state);
    }

    public long countAll() {
        return shelterRepository.count();
    }

    public long sumTotalCapacity() {
        return shelterRepository.sumTotalShelterCapacity();
    }

    public long sumCurrentOccupancy() {
        return shelterRepository.sumCurrentShelterOccupancy();
    }
}

