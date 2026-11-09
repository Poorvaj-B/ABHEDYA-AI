-- ===================================================================
-- ABHEDYA AI: Risk Assessment & Capacity Enhancements (V4)
-- Multi-Factor Weights, Resource Constraints & Sphere Standards
-- ===================================================================

-- 1. Enhance shelters with effective capacity and limiting factors
ALTER TABLE shelters ADD COLUMN effective_safe_capacity INT DEFAULT 0;
ALTER TABLE shelters ADD COLUMN capacity_limiting_factor VARCHAR(50) DEFAULT 'PHYSICAL_SPACE';

-- Calculate effective safe capacity based on Sphere Standards:
-- Water: 15 L/person/day -> capacity = potable_water / 15
-- Sanitation: 20 persons/unit -> capacity = sanitation_units * 20
-- Effective = LEAST(max_capacity, potable_water / 15, sanitation_units * 20)
UPDATE shelters 
SET effective_safe_capacity = LEAST(max_capacity, CAST(potable_water_liters_day / 15 AS INT), sanitation_units * 20),
    capacity_limiting_factor = CASE 
        WHEN max_capacity <= (potable_water_liters_day / 15) AND max_capacity <= (sanitation_units * 20) THEN 'PHYSICAL_SPACE'
        WHEN (potable_water_liters_day / 15) <= (sanitation_units * 20) THEN 'WATER_SUPPLY'
        ELSE 'SANITATION'
    END;

-- 2. Enhance risk assessments with explainability fields
ALTER TABLE risk_assessments ADD COLUMN vulnerable_ratio_index DOUBLE PRECISION DEFAULT 0.0;
ALTER TABLE risk_assessments ADD COLUMN assessment_rationale TEXT;

-- 3. Seed initial baseline risk assessments for demonstration habitations
INSERT INTO risk_assessments (
    id, habitation_id, hazard_index, vulnerability_index, capacity_pressure_index, 
    vulnerable_ratio_index, composite_score, classification, excess_population, 
    relocation_recommended, rainfall_24h_mm, model_weights_json, assessment_rationale
) VALUES
('ra-01', 'hab-ka-01', 95.0, 85.0, 100.0, 42.4, 82.5, 'VERY_HIGH', 750, TRUE, 125.0,
 '{"hazard":0.35,"vulnerability":0.25,"capacity":0.20,"vulnerableRatio":0.20}',
 'Habitation is strictly inside Landslide Red Zone HZ-KA-KOD-01. Rainfall 125mm exceeds saturation trigger. Population exceeds carrying capacity by 750.'),

('ra-02', 'hab-kl-01', 98.0, 90.0, 100.0, 43.3, 85.2, 'VERY_HIGH', 900, TRUE, 160.0,
 '{"hazard":0.35,"vulnerability":0.25,"capacity":0.20,"vulnerableRatio":0.20}',
 'Direct intersection with Wayanad active scarp. Severe rainfall 160mm/24h. 900 excess evacuees require immediate relocation.'),

('ra-03', 'hab-uk-01', 92.0, 80.0, 96.0, 47.4, 79.7, 'VERY_HIGH', 480, TRUE, 85.0,
 '{"hazard":0.35,"vulnerability":0.25,"capacity":0.20,"vulnerableRatio":0.20}',
 'Active fault mountain slope with high geotechnical vulnerability. Immediate relocation priority.');
