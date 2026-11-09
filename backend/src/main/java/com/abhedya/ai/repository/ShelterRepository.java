package com.abhedya.ai.repository;

import com.abhedya.ai.model.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, String> {

    List<Shelter> findByStatusIgnoreCase(String status);

    List<Shelter> findByStateIgnoreCase(String state);

    @Query("SELECT s FROM Shelter s WHERE s.status = 'OPERATIONAL' AND (s.maxCapacity - s.currentOccupancy) > 0")
    List<Shelter> findAvailableShelters();

    @Query("SELECT COALESCE(SUM(s.maxCapacity), 0) FROM Shelter s")
    long sumTotalShelterCapacity();

    @Query("SELECT COALESCE(SUM(s.currentOccupancy), 0) FROM Shelter s")
    long sumCurrentShelterOccupancy();
}

