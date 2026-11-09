package com.abhedya.ai.repository;

import com.abhedya.ai.model.RelocationPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RelocationPlanRepository extends JpaRepository<RelocationPlan, String> {

    Optional<RelocationPlan> findByPlanCode(String planCode);
}

