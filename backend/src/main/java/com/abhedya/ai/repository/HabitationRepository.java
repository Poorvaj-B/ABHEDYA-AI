package com.abhedya.ai.repository;

import com.abhedya.ai.model.Habitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitationRepository extends JpaRepository<Habitation, String> {

    List<Habitation> findByStateIgnoreCase(String state);

    List<Habitation> findByInRedZoneTrue();

    @Query("SELECT h FROM Habitation h WHERE h.totalPopulation > h.safeCarryingCapacity")
    List<Habitation> findHabitationsExceedingCapacity();

    @Query("SELECT COUNT(h) FROM Habitation h WHERE h.inRedZone = true")
    long countRedZoneHabitations();

    @Query("SELECT COALESCE(SUM(h.totalPopulation), 0) FROM Habitation h")
    long sumTotalPopulation();

    @Query("SELECT COALESCE(SUM(h.vulnerablePopulation), 0) FROM Habitation h")
    long sumVulnerablePopulation();
}

