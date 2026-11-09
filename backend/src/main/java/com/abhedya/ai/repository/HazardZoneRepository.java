package com.abhedya.ai.repository;

import com.abhedya.ai.model.HazardZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HazardZoneRepository extends JpaRepository<HazardZone, String> {

    List<HazardZone> findByIsActiveTrue();

    List<HazardZone> findByHazardTypeIgnoreCase(String hazardType);

    List<HazardZone> findByStateIgnoreCase(String state);

    Optional<HazardZone> findByZoneCode(String zoneCode);
}

