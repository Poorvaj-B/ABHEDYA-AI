-- ===================================================================
-- ABHEDYA AI: Database Schema Initialization (V1)
-- Multi-Factor Disaster Hazard, Shelter & Relocation Management
-- ===================================================================

-- 1. HABITATIONS (Settlements, Villages, Wards)
CREATE TABLE habitations (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    district VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    total_population INT NOT NULL,
    vulnerable_population INT NOT NULL,
    safe_carrying_capacity INT NOT NULL,
    elevation_meters DOUBLE PRECISION,
    terrain_type VARCHAR(100),
    in_red_zone BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_habitations_state_dist ON habitations(state, district);
CREATE INDEX idx_habitations_red_zone ON habitations(in_red_zone);

-- 2. HAZARD ZONES (Red Zone Polygons & Threat Delimitation)
CREATE TABLE hazard_zones (
    id VARCHAR(36) PRIMARY KEY,
    zone_code VARCHAR(50) UNIQUE NOT NULL,
    hazard_type VARCHAR(50) NOT NULL,
    severity_level VARCHAR(30) NOT NULL,
    state VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    center_lat DOUBLE PRECISION NOT NULL,
    center_lng DOUBLE PRECISION NOT NULL,
    buffer_radius_km DOUBLE PRECISION NOT NULL,
    boundary_geojson TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hazard_zones_type_sev ON hazard_zones(hazard_type, severity_level);
CREATE INDEX idx_hazard_zones_active ON hazard_zones(is_active);

-- 3. SHELTERS (Relief Camps, Evacuation Havens)
CREATE TABLE shelters (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    district VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    max_capacity INT NOT NULL,
    current_occupancy INT DEFAULT 0,
    available_beds INT NOT NULL,
    potable_water_liters_day DOUBLE PRECISION DEFAULT 0.0,
    has_backup_power BOOLEAN DEFAULT TRUE,
    sanitation_units INT DEFAULT 0,
    medical_support_level VARCHAR(50) DEFAULT 'BASIC',
    status VARCHAR(50) DEFAULT 'OPERATIONAL',
    is_verified_operational BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shelters_state_dist ON shelters(state, district);
CREATE INDEX idx_shelters_status ON shelters(status);

-- 4. RISK ASSESSMENTS (Multi-Factor Scoring Records)
CREATE TABLE risk_assessments (
    id VARCHAR(36) PRIMARY KEY,
    habitation_id VARCHAR(36) NOT NULL,
    hazard_index DOUBLE PRECISION NOT NULL,
    vulnerability_index DOUBLE PRECISION NOT NULL,
    capacity_pressure_index DOUBLE PRECISION NOT NULL,
    composite_score DOUBLE PRECISION NOT NULL,
    classification VARCHAR(30) NOT NULL,
    excess_population INT NOT NULL,
    relocation_recommended BOOLEAN NOT NULL,
    rainfall_24h_mm DOUBLE PRECISION DEFAULT 0.0,
    model_weights_json TEXT,
    assessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_risk_habitation FOREIGN KEY (habitation_id) REFERENCES habitations(id) ON DELETE CASCADE
);

CREATE INDEX idx_risk_hab ON risk_assessments(habitation_id);
CREATE INDEX idx_risk_class ON risk_assessments(classification);

-- 5. RELOCATION PLANS (Executive Evacuation Directives)
CREATE TABLE relocation_plans (
    id VARCHAR(36) PRIMARY KEY,
    plan_code VARCHAR(50) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_evacuees INT NOT NULL,
    total_buses_required INT DEFAULT 0,
    total_ambulances_required INT DEFAULT 0,
    notes TEXT,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_by VARCHAR(100)
);

CREATE INDEX idx_reloc_plan_status ON relocation_plans(status);

-- 6. RELOCATION ASSIGNMENTS (Habitation-to-Shelter Allocations)
CREATE TABLE relocation_assignments (
    id VARCHAR(36) PRIMARY KEY,
    plan_id VARCHAR(36) NOT NULL,
    source_habitation_id VARCHAR(36) NOT NULL,
    destination_shelter_id VARCHAR(36) NOT NULL,
    allocated_evacuees INT NOT NULL,
    vulnerable_evacuees INT DEFAULT 0,
    priority_tier VARCHAR(30) NOT NULL,
    assignment_status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assign_plan FOREIGN KEY (plan_id) REFERENCES relocation_plans(id) ON DELETE CASCADE,
    CONSTRAINT fk_assign_hab FOREIGN KEY (source_habitation_id) REFERENCES habitations(id),
    CONSTRAINT fk_assign_shelter FOREIGN KEY (destination_shelter_id) REFERENCES shelters(id)
);

CREATE INDEX idx_assign_plan ON relocation_assignments(plan_id);

-- 7. EVACUATION ROUTES (OSRM Safe Routing Corridors)
CREATE TABLE evacuation_routes (
    id VARCHAR(36) PRIMARY KEY,
    plan_id VARCHAR(36) NOT NULL,
    source_habitation_id VARCHAR(36) NOT NULL,
    destination_shelter_id VARCHAR(36) NOT NULL,
    route_geojson TEXT NOT NULL,
    distance_km DOUBLE PRECISION NOT NULL,
    duration_minutes DOUBLE PRECISION NOT NULL,
    avoids_red_zones BOOLEAN DEFAULT TRUE,
    validated_safe BOOLEAN DEFAULT FALSE,
    safety_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_route_plan FOREIGN KEY (plan_id) REFERENCES relocation_plans(id) ON DELETE CASCADE,
    CONSTRAINT fk_route_hab FOREIGN KEY (source_habitation_id) REFERENCES habitations(id),
    CONSTRAINT fk_route_shelter FOREIGN KEY (destination_shelter_id) REFERENCES shelters(id)
);

CREATE INDEX idx_route_plan ON evacuation_routes(plan_id);

-- 8. INCIDENT LOGS (Audit Trail & Field Operations Log)
CREATE TABLE incident_logs (
    id VARCHAR(36) PRIMARY KEY,
    incident_type VARCHAR(50) NOT NULL,
    severity VARCHAR(30) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    affected_state VARCHAR(100),
    affected_district VARCHAR(100),
    source VARCHAR(50) NOT NULL,
    logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_incidents_sev ON incident_logs(severity);
CREATE INDEX idx_incidents_time ON incident_logs(logged_at);

