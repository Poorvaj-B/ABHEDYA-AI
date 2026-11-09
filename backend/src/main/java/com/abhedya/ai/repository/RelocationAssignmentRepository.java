package com.abhedya.ai.repository;

import com.abhedya.ai.model.RelocationAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelocationAssignmentRepository extends JpaRepository<RelocationAssignment, String> {

    List<RelocationAssignment> findByPlanId(String planId);
}

