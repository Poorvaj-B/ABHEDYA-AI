-- ===================================================================
-- ABHEDYA AI: Seed Demonstration Dataset (V2)
-- 20 Habitations, Multi-Polygon Red Zones, Verified Shelters & Logs
-- ===================================================================

-- 1. SEED HABITATIONS
INSERT INTO habitations (id, name, district, state, latitude, longitude, total_population, vulnerable_population, safe_carrying_capacity, elevation_meters, terrain_type, in_red_zone) VALUES
('hab-ka-01', 'Kodagu Vulnerable Village', 'Kodagu', 'Karnataka', 12.4244, 75.7382, 1450, 410, 700, 1150.0, 'Steep Hill Slope', TRUE),
('hab-ka-02', 'Mysuru Flood-Prone Village', 'Mysuru', 'Karnataka', 12.2958, 76.6394, 1250, 320, 800, 763.0, 'River Floodplain', FALSE),
('hab-ka-03', 'Uttara Kannada Coastal Village', 'Uttara Kannada', 'Karnataka', 14.8000, 74.1300, 1100, 260, 800, 12.0, 'Coastal Estuary', FALSE),
('hab-kl-01', 'Wayanad Hill Settlement', 'Wayanad', 'Kerala', 11.6854, 76.1320, 1800, 520, 900, 980.0, 'Steep Hill Slope', TRUE),
('hab-kl-02', 'Alappuzha Lowland Village', 'Alappuzha', 'Kerala', 9.4981, 76.3388, 2100, 480, 1400, 1.0, 'Below Sea Level Basin', FALSE),
('hab-tn-01', 'Nilgiris Hill Settlement', 'Nilgiris', 'Tamil Nadu', 11.4102, 76.6950, 1250, 290, 800, 2100.0, 'Mountain Escarpment', TRUE),
('hab-tn-02', 'Chennai Coastal Zone', 'Chennai', 'Tamil Nadu', 13.0827, 80.2707, 3200, 450, 3000, 6.0, 'Urban Coastal Plain', FALSE),
('hab-mh-01', 'Konkan Coastal Village', 'Ratnagiri', 'Maharashtra', 16.9902, 73.3120, 1600, 380, 900, 45.0, 'Coastal Escarpment', FALSE),
('hab-mh-02', 'Mumbai Flood Zone', 'Mumbai Suburban', 'Maharashtra', 19.0760, 72.8777, 4500, 700, 3500, 8.0, 'Low-lying Urban Wetland', FALSE),
('hab-uk-01', 'Chamoli Mountain Village', 'Chamoli', 'Uttarakhand', 30.4010, 79.3200, 980, 310, 500, 1450.0, 'Active Fault Mountain Slope', TRUE),
('hab-uk-02', 'Uttarkashi Vulnerable Settlement', 'Uttarkashi', 'Uttarakhand', 30.7268, 78.4354, 1200, 350, 600, 1158.0, 'River Gorge Terraces', TRUE),
('hab-hp-01', 'Mandi Mountain Village', 'Mandi', 'Himachal Pradesh', 31.7088, 76.9310, 1100, 280, 650, 850.0, 'River Valley Slopes', FALSE),
('hab-hp-02', 'Kullu Valley Village', 'Kullu', 'Himachal Pradesh', 31.9579, 77.1095, 1350, 400, 700, 1220.0, 'River Terraces', TRUE),
('hab-as-01', 'Majuli Flood Zone', 'Majuli', 'Assam', 27.0016, 94.2243, 2400, 650, 1200, 84.0, 'River Island Lowland', TRUE),
('hab-as-02', 'Dibrugarh Flood Zone', 'Dibrugarh', 'Assam', 27.4728, 94.9120, 2100, 500, 1300, 108.0, 'Brahmaputra Floodplain', FALSE),
('hab-br-01', 'Kosi Floodplain Village', 'Supaul', 'Bihar', 25.5200, 86.0000, 2800, 750, 1500, 45.0, 'Active Alluvial Plain', TRUE),
('hab-od-01', 'Mahanadi Flood Zone', 'Cuttack', 'Odisha', 20.4625, 85.8828, 2200, 490, 1400, 36.0, 'Deltaic Plain', FALSE),
('hab-od-02', 'Odisha Coastal Village', 'Puri', 'Odisha', 19.8135, 85.8312, 1900, 560, 900, 5.0, 'Sandy Coastal Fringe', TRUE),
('hab-wb-01', 'Sundarbans Vulnerable Village', 'South 24 Parganas', 'West Bengal', 21.9497, 88.9111, 2600, 700, 1300, 3.0, 'Tidal Mudflat Island', TRUE),
('hab-gj-01', 'Kutch Vulnerable Settlement', 'Kutch', 'Gujarat', 23.7337, 69.8597, 1300, 210, 1000, 25.0, 'Arid Alluvial Plain', FALSE);

-- 2. SEED HAZARD RED ZONES (With Verified GeoJSON Polygons)
INSERT INTO hazard_zones (id, zone_code, hazard_type, severity_level, state, district, center_lat, center_lng, buffer_radius_km, boundary_geojson, is_active) VALUES
('hz-01', 'HZ-KA-KOD-01', 'LANDSLIDE', 'VERY_HIGH', 'Karnataka', 'Kodagu', 12.4244, 75.7382, 5.0,
'{"type":"Polygon","coordinates":[[[75.710,12.400],[75.760,12.410],[75.770,12.445],[75.725,12.450],[75.705,12.420],[75.710,12.400]]]}', TRUE),

('hz-02', 'HZ-KL-WAY-01', 'LANDSLIDE', 'VERY_HIGH', 'Kerala', 'Wayanad', 11.6854, 76.1320, 6.5,
'{"type":"Polygon","coordinates":[[[76.100,11.660],[76.160,11.670],[76.170,11.710],[76.120,11.715],[76.090,11.680],[76.100,11.660]]]}', TRUE),

('hz-03', 'HZ-UK-CHA-01', 'LANDSLIDE', 'VERY_HIGH', 'Uttarakhand', 'Chamoli', 30.4010, 79.3200, 7.0,
'{"type":"Polygon","coordinates":[[[79.280,30.370],[79.350,30.380],[79.360,30.430],[79.300,30.435],[79.270,30.395],[79.280,30.370]]]}', TRUE),

('hz-04', 'HZ-AS-MAJ-01', 'FLOOD', 'VERY_HIGH', 'Assam', 'Majuli', 27.0016, 94.2243, 8.0,
'{"type":"Polygon","coordinates":[[[94.170,26.960],[94.270,26.970],[94.280,27.040],[94.190,27.050],[94.150,27.000],[94.170,26.960]]]}', TRUE),

('hz-05', 'HZ-BR-KOS-01', 'FLOOD', 'VERY_HIGH', 'Bihar', 'Supaul', 25.5200, 86.0000, 9.5,
'{"type":"Polygon","coordinates":[[[85.940,25.470],[86.060,25.480],[86.070,25.570],[85.960,25.580],[85.920,25.510],[85.940,25.470]]]}', TRUE),

('hz-06', 'HZ-OD-PUR-01', 'CYCLONE', 'VERY_HIGH', 'Odisha', 'Puri', 19.8135, 85.8312, 10.0,
'{"type":"Polygon","coordinates":[[[85.760,19.760],[85.890,19.780],[85.910,19.860],[85.800,19.870],[85.740,19.810],[85.760,19.760]]]}', TRUE);

-- 3. SEED RELIEF SHELTERS
INSERT INTO shelters (id, name, district, state, latitude, longitude, max_capacity, current_occupancy, available_beds, potable_water_liters_day, has_backup_power, sanitation_units, medical_support_level, status, is_verified_operational) VALUES
('sh-ka-01', 'Madikeri Community Relief Haven', 'Kodagu', 'Karnataka', 12.4200, 75.7450, 1200, 250, 950, 15000.0, TRUE, 45, 'INTERMEDIATE', 'OPERATIONAL', TRUE),
('sh-kl-01', 'Meppadi High Ground Shelter', 'Wayanad', 'Kerala', 11.5500, 76.1200, 1500, 300, 1200, 20000.0, TRUE, 60, 'ADVANCED', 'OPERATIONAL', TRUE),
('sh-tn-01', 'Coonoor Multi-Purpose Relief Center', 'Nilgiris', 'Tamil Nadu', 11.3530, 76.7959, 1000, 150, 850, 12000.0, TRUE, 35, 'INTERMEDIATE', 'OPERATIONAL', TRUE),
('sh-uk-01', 'Joshimath Safe Plateau Haven', 'Chamoli', 'Uttarakhand', 30.5500, 79.5600, 900, 120, 780, 10000.0, TRUE, 30, 'INTERMEDIATE', 'OPERATIONAL', TRUE),
('sh-as-01', 'Jorhat University Disaster Campus', 'Jorhat', 'Assam', 26.7509, 94.2037, 2500, 400, 2100, 30000.0, TRUE, 85, 'ADVANCED', 'OPERATIONAL', TRUE),
('sh-br-01', 'Saharsa High Embankment Camp', 'Saharsa', 'Bihar', 25.8835, 86.6006, 2000, 500, 1500, 25000.0, TRUE, 70, 'BASIC', 'OPERATIONAL', TRUE),
('sh-od-01', 'Puri Cyclone Resilient Shelter #4', 'Puri', 'Odisha', 19.8200, 85.8500, 1800, 350, 1450, 22000.0, TRUE, 55, 'ADVANCED', 'OPERATIONAL', TRUE);

-- 4. SEED INITIAL INCIDENT AUDIT LOGS
INSERT INTO incident_logs (id, incident_type, severity, title, description, affected_state, affected_district, source) VALUES
('inc-001', 'SYSTEM_INITIALIZATION', 'INFO', 'ABHEDYA AI Command Center Online', 'Operational baseline initialized with 20 habitations, 6 active red zones, and 7 certified safe havens.', 'National', 'All Sectors', 'SYSTEM_MONITOR'),
('inc-002', 'HAZARD_ALERT', 'CRITICAL', 'Landslide Red Alert Escalation: Wayanad', 'Habitation Wayanad Hill Settlement intersects HZ-KL-WAY-01 scarp zone. Rainfall saturation threshold exceeded.', 'Kerala', 'Wayanad', 'OPEN_METEO'),
('inc-003', 'CAPACITY_BREACH', 'WARNING', 'Carrying Capacity Deficit in Kodagu', 'Population exceeds safe geotechnical threshold by 750 persons. Evacuation priority triggered.', 'Karnataka', 'Kodagu', 'SYSTEM_MONITOR');

