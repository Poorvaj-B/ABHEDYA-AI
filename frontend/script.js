/* ===================================================================
   ABHEDYA AI — Intelligent Disaster Management & Relocation Command Center
   Frontend Controller & Real-Time Geospatial Intelligence
=================================================================== */

const API_BASE = "http://localhost:8080";

/* =========================================
   BASELINE / OFFLINE DEMONSTRATION DATA
   (Maps 1:1 to PostGIS Seed Database)
========================================= */

let hazardLocations = [
    {
        id: "hab-ka-01",
        name: "Kodagu Vulnerable Village",
        state: "Karnataka",
        district: "Kodagu",
        lat: 12.4244,
        lng: 75.7382,
        risk: "VERY HIGH",
        hazard: "Landslide",
        population: 1450,
        capacity: 700,
        vulnerable: 410,
        inRedZone: true
    },
    {
        id: "hab-ka-02",
        name: "Mysuru Flood-Prone Village",
        state: "Karnataka",
        district: "Mysuru",
        lat: 12.2958,
        lng: 76.6394,
        risk: "HIGH",
        hazard: "Flood",
        population: 1250,
        capacity: 800,
        vulnerable: 320,
        inRedZone: false
    },
    {
        id: "hab-ka-03",
        name: "Uttara Kannada Coastal Village",
        state: "Karnataka",
        district: "Uttara Kannada",
        lat: 14.8000,
        lng: 74.1300,
        risk: "HIGH",
        hazard: "Coastal Hazard",
        population: 1100,
        capacity: 800,
        vulnerable: 260,
        inRedZone: false
    },
    {
        id: "hab-kl-01",
        name: "Wayanad Hill Settlement",
        state: "Kerala",
        district: "Wayanad",
        lat: 11.6854,
        lng: 76.1320,
        risk: "VERY HIGH",
        hazard: "Landslide",
        population: 1800,
        capacity: 900,
        vulnerable: 520,
        inRedZone: true
    },
    {
        id: "hab-kl-02",
        name: "Alappuzha Lowland Village",
        state: "Kerala",
        district: "Alappuzha",
        lat: 9.4981,
        lng: 76.3388,
        risk: "HIGH",
        hazard: "Flood",
        population: 2100,
        capacity: 1400,
        vulnerable: 480,
        inRedZone: false
    },
    {
        id: "hab-tn-01",
        name: "Nilgiris Hill Settlement",
        state: "Tamil Nadu",
        district: "Nilgiris",
        lat: 11.4102,
        lng: 76.6950,
        risk: "HIGH",
        hazard: "Landslide",
        population: 1250,
        capacity: 800,
        vulnerable: 290,
        inRedZone: true
    },
    {
        id: "hab-tn-02",
        name: "Chennai Coastal Zone",
        state: "Tamil Nadu",
        district: "Chennai",
        lat: 13.0827,
        lng: 80.2707,
        risk: "MEDIUM",
        hazard: "Urban Flooding",
        population: 3200,
        capacity: 3000,
        vulnerable: 450,
        inRedZone: false
    },
    {
        id: "hab-mh-01",
        name: "Konkan Coastal Village",
        state: "Maharashtra",
        district: "Ratnagiri",
        lat: 16.9902,
        lng: 73.3120,
        risk: "HIGH",
        hazard: "Flood / Landslide",
        population: 1600,
        capacity: 900,
        vulnerable: 380,
        inRedZone: false
    },
    {
        id: "hab-mh-02",
        name: "Mumbai Flood Zone",
        state: "Maharashtra",
        district: "Mumbai",
        lat: 19.0760,
        lng: 72.8777,
        risk: "HIGH",
        hazard: "Urban Flood",
        population: 4500,
        capacity: 3500,
        vulnerable: 700,
        inRedZone: false
    },
    {
        id: "hab-uk-01",
        name: "Chamoli Mountain Village",
        state: "Uttarakhand",
        district: "Chamoli",
        lat: 30.4010,
        lng: 79.3200,
        risk: "VERY HIGH",
        hazard: "Landslide",
        population: 980,
        capacity: 500,
        vulnerable: 310,
        inRedZone: true
    },
    {
        id: "hab-uk-02",
        name: "Uttarkashi Vulnerable Settlement",
        state: "Uttarakhand",
        district: "Uttarkashi",
        lat: 30.7268,
        lng: 78.4354,
        risk: "VERY HIGH",
        hazard: "Landslide / Flash Flood",
        population: 1200,
        capacity: 600,
        vulnerable: 350,
        inRedZone: true
    },
    {
        id: "hab-hp-01",
        name: "Mandi Mountain Village",
        state: "Himachal Pradesh",
        district: "Mandi",
        lat: 31.7088,
        lng: 76.9310,
        risk: "HIGH",
        hazard: "Landslide",
        population: 1100,
        capacity: 650,
        vulnerable: 280,
        inRedZone: false
    },
    {
        id: "hab-hp-02",
        name: "Kullu Valley Village",
        state: "Himachal Pradesh",
        district: "Kullu",
        lat: 31.9579,
        lng: 77.1095,
        risk: "VERY HIGH",
        hazard: "Flash Flood / Landslide",
        population: 1350,
        capacity: 700,
        vulnerable: 400,
        inRedZone: true
    },
    {
        id: "hab-as-01",
        name: "Majuli Flood Zone",
        state: "Assam",
        district: "Majuli",
        lat: 27.0016,
        lng: 94.2243,
        risk: "VERY HIGH",
        hazard: "River Flood",
        population: 2400,
        capacity: 1200,
        vulnerable: 650,
        inRedZone: true
    },
    {
        id: "hab-as-02",
        name: "Dibrugarh Flood Zone",
        state: "Assam",
        district: "Dibrugarh",
        lat: 27.4728,
        lng: 94.9120,
        risk: "HIGH",
        hazard: "River Flood",
        population: 2100,
        capacity: 1300,
        vulnerable: 500,
        inRedZone: false
    },
    {
        id: "hab-br-01",
        name: "Kosi Floodplain Village",
        state: "Bihar",
        district: "Supaul",
        lat: 25.5200,
        lng: 86.0000,
        risk: "VERY HIGH",
        hazard: "River Flood",
        population: 2800,
        capacity: 1500,
        vulnerable: 750,
        inRedZone: true
    },
    {
        id: "hab-od-01",
        name: "Mahanadi Flood Zone",
        state: "Odisha",
        district: "Cuttack",
        lat: 20.4625,
        lng: 85.8828,
        risk: "HIGH",
        hazard: "Flood",
        population: 2200,
        capacity: 1400,
        vulnerable: 490,
        inRedZone: false
    },
    {
        id: "hab-od-02",
        name: "Odisha Coastal Village",
        state: "Odisha",
        district: "Puri",
        lat: 19.8135,
        lng: 85.8312,
        risk: "VERY HIGH",
        hazard: "Cyclone / Storm Surge",
        population: 1900,
        capacity: 900,
        vulnerable: 560,
        inRedZone: true
    },
    {
        id: "hab-wb-01",
        name: "Sundarbans Vulnerable Village",
        state: "West Bengal",
        district: "South 24 Parganas",
        lat: 21.9497,
        lng: 88.9111,
        risk: "VERY HIGH",
        hazard: "Cyclone / Flood",
        population: 2600,
        capacity: 1300,
        vulnerable: 700,
        inRedZone: true
    },
    {
        id: "hab-gj-01",
        name: "Kutch Vulnerable Settlement",
        state: "Gujarat",
        district: "Kutch",
        lat: 23.7337,
        lng: 69.8597,
        risk: "MEDIUM",
        hazard: "Earthquake",
        population: 1300,
        capacity: 1000,
        vulnerable: 210,
        inRedZone: false
    }
];

/* =========================================
   APPLICATION STATE & MAP LAYERS
========================================= */

let hazardMap = null;
let hazardMarkers = [];
let hazardZoneLayers = [];
let shelterMarkers = [];
let evacuationRouteLayers = [];

let liveHazardZonesGeoJson = null;
let liveSheltersData = [];
let liveRoutesGeoJson = null;
let liveRelocationPlans = [];
let liveShelterAudits = [];
let activeHighlightedPlanId = null;
let isBackendOnline = false;

/* =========================================
   PHASE 5 BASELINE / OFFLINE FALLBACK DATA
========================================= */

const baselineShelters = [
    {
        id: "sh-ka-01",
        name: "Madikeri Community Relief Haven",
        district: "Kodagu",
        state: "Karnataka",
        latitude: 12.4200,
        longitude: 75.7450,
        maxCapacity: 1200,
        effectiveSafeCapacity: 1000,
        currentOccupancy: 250,
        availableBeds: 750,
        potableWaterLitersDay: 15000,
        sanitationUnits: 45,
        medicalSupportLevel: "INTERMEDIATE",
        hasBackupPower: true,
        status: "OPERATIONAL"
    },
    {
        id: "sh-kl-01",
        name: "Meppadi High Ground Shelter",
        district: "Wayanad",
        state: "Kerala",
        latitude: 11.5500,
        longitude: 76.1200,
        maxCapacity: 1500,
        effectiveSafeCapacity: 1200,
        currentOccupancy: 300,
        availableBeds: 900,
        potableWaterLitersDay: 20000,
        sanitationUnits: 60,
        medicalSupportLevel: "ADVANCED",
        hasBackupPower: true,
        status: "OPERATIONAL"
    },
    {
        id: "sh-tn-01",
        name: "Coonoor Multi-Purpose Relief Center",
        district: "Nilgiris",
        state: "Tamil Nadu",
        latitude: 11.3530,
        longitude: 76.7959,
        maxCapacity: 1000,
        effectiveSafeCapacity: 800,
        currentOccupancy: 150,
        availableBeds: 650,
        potableWaterLitersDay: 12000,
        sanitationUnits: 35,
        medicalSupportLevel: "INTERMEDIATE",
        hasBackupPower: true,
        status: "OPERATIONAL"
    },
    {
        id: "sh-uk-01",
        name: "Joshimath Safe Plateau Haven",
        district: "Chamoli",
        state: "Uttarakhand",
        latitude: 30.5500,
        longitude: 79.5600,
        maxCapacity: 900,
        effectiveSafeCapacity: 600,
        currentOccupancy: 120,
        availableBeds: 480,
        potableWaterLitersDay: 10000,
        sanitationUnits: 30,
        medicalSupportLevel: "INTERMEDIATE",
        hasBackupPower: true,
        status: "OPERATIONAL"
    },
    {
        id: "sh-as-01",
        name: "Jorhat University Disaster Campus",
        district: "Jorhat",
        state: "Assam",
        latitude: 26.7509,
        longitude: 94.2037,
        maxCapacity: 2500,
        effectiveSafeCapacity: 2000,
        currentOccupancy: 400,
        availableBeds: 1600,
        potableWaterLitersDay: 30000,
        sanitationUnits: 85,
        medicalSupportLevel: "ADVANCED",
        hasBackupPower: true,
        status: "OPERATIONAL"
    },
    {
        id: "sh-br-01",
        name: "Saharsa High Embankment Camp",
        district: "Saharsa",
        state: "Bihar",
        latitude: 25.8835,
        longitude: 86.6006,
        maxCapacity: 2000,
        effectiveSafeCapacity: 1400,
        currentOccupancy: 500,
        availableBeds: 900,
        potableWaterLitersDay: 25000,
        sanitationUnits: 70,
        medicalSupportLevel: "BASIC",
        hasBackupPower: true,
        status: "OPERATIONAL"
    },
    {
        id: "sh-od-01",
        name: "Puri Cyclone Resilient Shelter #4",
        district: "Puri",
        state: "Odisha",
        latitude: 19.8200,
        longitude: 85.8500,
        maxCapacity: 1800,
        effectiveSafeCapacity: 1100,
        currentOccupancy: 350,
        availableBeds: 750,
        potableWaterLitersDay: 22000,
        sanitationUnits: 55,
        medicalSupportLevel: "ADVANCED",
        hasBackupPower: true,
        status: "OPERATIONAL"
    }
];

const baselineRelocationPlans = [
    {
        planId: "plan-demo-01",
        planCode: "PLAN-KL-48201",
        status: "DISPATCHED",
        totalEvacuees: 900,
        totalBusesRequired: 23,
        totalAmbulancesRequired: 26,
        notes: "Priority monsoon evacuation from Wayanad Hill Settlement to Meppadi High Ground Haven",
        generatedAt: "2026-11-07T14:30:00",
        approvedBy: "District Collector, Wayanad",
        assignments: [
            {
                sourceHabitationId: "hab-kl-01",
                sourceHabitationName: "Wayanad Hill Settlement",
                destinationShelterId: "sh-kl-01",
                destinationShelterName: "Meppadi High Ground Shelter",
                allocatedEvacuees: 900,
                vulnerableEvacuees: 520,
                priorityTier: "IMMEDIATE",
                assignmentStatus: "EN_ROUTE",
                busesRequired: 23,
                ambulancesRequired: 26
            }
        ],
        routes: [
            {
                routeId: "route-demo-01",
                planId: "plan-demo-01",
                sourceHabitationName: "Wayanad Hill Settlement",
                destinationShelterName: "Meppadi High Ground Shelter",
                distanceKm: 28.5,
                durationMinutes: 42.0,
                avoidsRedZones: true,
                validatedSafe: true,
                safetyNotes: "Verified road corridor via OSRM. Clean road corridor strictly bypassing active hazard zones.",
                routeGeoJson: JSON.stringify({
                    type: "LineString",
                    coordinates: [
                        [76.1320, 11.6854],
                        [76.1345, 11.6500],
                        [76.1280, 11.6100],
                        [76.1210, 11.5800],
                        [76.1200, 11.5500]
                    ]
                })
            }
        ]
    }
];

const baselineRoutesGeoJson = {
    type: "FeatureCollection",
    features: [
        {
            type: "Feature",
            geometry: {
                type: "LineString",
                coordinates: [
                    [76.1320, 11.6854],
                    [76.1345, 11.6500],
                    [76.1280, 11.6100],
                    [76.1210, 11.5800],
                    [76.1200, 11.5500]
                ]
            },
            properties: {
                routeId: "route-demo-01",
                planId: "plan-demo-01",
                sourceHabitation: "Wayanad Hill Settlement",
                destinationShelter: "Meppadi High Ground Shelter",
                distanceKm: 28.5,
                durationMinutes: 42.0,
                avoidsRedZones: true,
                validatedSafe: true,
                safetyNotes: "Verified road corridor via OSRM. Clean road corridor strictly bypassing active hazard zones."
            }
        }
    ]
};

// Initialize with baseline data for instant standalone responsiveness
liveSheltersData = [...baselineShelters];
liveRelocationPlans = [...baselineRelocationPlans];
liveRoutesGeoJson = { ...baselineRoutesGeoJson };

// Relocation modal state
let modalSelectedHabitation = null;
let modalSelectedShelterId = null;
let modalCandidateShelters = [];

/* =========================================
   PAGE NAVIGATION
========================================= */

function showPage(pageId, button = null) {
    document.querySelectorAll(".page").forEach(page => {
        page.classList.remove("active");
    });

    const selectedPage = document.getElementById(pageId);
    if (selectedPage) {
        selectedPage.classList.add("active");
    }

    document.querySelectorAll(".nav-btn").forEach(btn => {
        btn.classList.remove("active");
    });

    if (button) {
        button.classList.add("active");
    }

    if (pageId === "map" && hazardMap) {
        setTimeout(() => {
            hazardMap.invalidateSize();
        }, 150);
    }
}

/* =========================================
   BACKEND SYNCHRONIZATION
========================================= */

async function checkBackendConnectivity() {
    const badge = document.getElementById("systemStatusBadge");
    try {
        const res = await fetch(`${API_BASE}/api/v1/health`, { cache: "no-store" });
        if (res.ok) {
            const data = await res.json();
            if (data.success) {
                isBackendOnline = true;
                if (badge) {
                    badge.textContent = "● ABHEDYA Backend Online";
                    badge.style.background = "#dcfce7";
                    badge.style.color = "#15803d";
                }
                await loadLiveBackendData();
                return;
            }
        }
    } catch (e) {
        console.info("Backend running in standalone/offline mode.");
    }

    if (badge) {
        badge.textContent = "● Standalone Mode";
        badge.style.background = "#fef3c7";
        badge.style.color = "#b45309";
    }
}

async function loadLiveBackendData() {
    try {
        // 1. Fetch live habitations
        const habRes = await fetch(`${API_BASE}/api/v1/habitations`);
        if (habRes.ok) {
            const habJson = await habRes.json();
            if (habJson.success && habJson.data && habJson.data.length > 0) {
                hazardLocations = habJson.data.map(h => ({
                    id: h.id,
                    name: h.name,
                    state: h.state,
                    district: h.district,
                    lat: h.latitude,
                    lng: h.longitude,
                    risk: h.inRedZone ? "VERY HIGH" : (h.totalPopulation > h.safeCarryingCapacity ? "HIGH" : "MEDIUM"),
                    hazard: h.terrainType || "Multi-Hazard",
                    population: h.totalPopulation,
                    capacity: h.safeCarryingCapacity,
                    vulnerable: h.vulnerablePopulation || 0,
                    inRedZone: Boolean(h.inRedZone)
                }));
            }
        }

        // 2. Fetch Hazard Red Zones GeoJSON
        const hzRes = await fetch(`${API_BASE}/api/v1/hazards/geojson`);
        if (hzRes.ok) {
            const hzJson = await hzRes.json();
            if (hzJson.success && hzJson.data) {
                liveHazardZonesGeoJson = hzJson.data;
            }
        }

        // 3. Fetch Certified Safe Relief Shelters
        const shRes = await fetch(`${API_BASE}/api/v1/shelters`);
        if (shRes.ok) {
            const shJson = await shRes.json();
            if (shJson.success && shJson.data) {
                liveSheltersData = shJson.data;
            }
        }

        // 4. Fetch Evacuation Routes GeoJSON
        const rtRes = await fetch(`${API_BASE}/api/v1/relocation/routes/geojson`);
        if (rtRes.ok) {
            const rtJson = await rtRes.json();
            if (rtJson.success && rtJson.data && rtJson.data.features && rtJson.data.features.length > 0) {
                liveRoutesGeoJson = rtJson.data;
            }
        }

        // 5. Fetch Relocation Plans
        const plRes = await fetch(`${API_BASE}/api/v1/relocation/plans`);
        if (plRes.ok) {
            const plJson = await plRes.json();
            if (plJson.success && plJson.data && plJson.data.length > 0) {
                liveRelocationPlans = plJson.data;
            }
        }

        // 6. Fetch Multi-Resource Shelter Capacity Audits
        const audRes = await fetch(`${API_BASE}/api/v1/shelters/capacity-assessment`);
        if (audRes.ok) {
            const audJson = await audRes.json();
            if (audJson.success && audJson.data && audJson.data.length > 0) {
                liveShelterAudits = audJson.data;
            }
        }

        // Refresh UI components with live data
        updateDashboard();
        updateRelocationTable();
        updateDispatchedPlansTable();
        updateSheltersCapacityTable();
        updateRelocationMetricsBar();
        updateMapRoutesPanel();
        displayIndiaHazards();
    } catch (err) {
        console.warn("Error synchronizing with backend APIs:", err);
    }
}

/* =========================================
   MAP INITIALIZATION & RENDERING
========================================= */

function initializeIndiaMap() {
    if (typeof L === "undefined") {
        console.error("Leaflet was not loaded.");
        return;
    }

    hazardMap = L.map("hazardMap").setView([22.5937, 78.9629], 5);

    L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "&copy; OpenStreetMap contributors | ABHEDYA AI Geospatial Engine",
        maxZoom: 19
    }).addTo(hazardMap);

    displayIndiaHazards();
}

function createHazardIcon(risk) {
    let color = "#22c55e";
    if (risk === "VERY HIGH") {
        color = "#dc2626";
    } else if (risk === "HIGH") {
        color = "#f97316";
    } else if (risk === "MEDIUM") {
        color = "#eab308";
    }

    return L.divIcon({
        className: "",
        html: `<div class="hazard-marker" style="background:${color};"></div>`,
        iconSize: [22, 22],
        iconAnchor: [11, 11]
    });
}

function createShelterIcon() {
    return L.divIcon({
        className: "",
        html: `<div class="shelter-marker" title="Safe Relief Shelter">🏥</div>`,
        iconSize: [26, 26],
        iconAnchor: [13, 13]
    });
}

function displayIndiaHazards() {
    if (!hazardMap) return;

    // 1. Clear previous layers
    hazardMarkers.forEach(m => hazardMap.removeLayer(m));
    hazardMarkers = [];

    hazardZoneLayers.forEach(l => hazardMap.removeLayer(l));
    hazardZoneLayers = [];

    shelterMarkers.forEach(s => hazardMap.removeLayer(s));
    shelterMarkers = [];

    evacuationRouteLayers.forEach(r => hazardMap.removeLayer(r));
    evacuationRouteLayers = [];

    const selectedState = document.getElementById("stateFilter") ? document.getElementById("stateFilter").value : "ALL";
    const selectedRisk = document.getElementById("riskFilter") ? document.getElementById("riskFilter").value : "ALL";

    // 2. Render Active Hazard Red Zone Polygons (PostGIS)
    if (liveHazardZonesGeoJson && liveHazardZonesGeoJson.features) {
        const zoneLayer = L.geoJSON(liveHazardZonesGeoJson, {
            style: function (feature) {
                return {
                    color: "#dc2626",
                    weight: 2,
                    fillColor: "#ef4444",
                    fillOpacity: 0.22,
                    dashArray: "4, 4"
                };
            },
            onEachFeature: function (feature, layer) {
                const p = feature.properties || {};
                layer.bindTooltip(`
                    <strong>⚠️ HAZARD RED ZONE: ${p.zoneCode || "ACTIVE"}</strong><br>
                    Type: ${p.hazardType || "Unknown"} | Severity: ${p.severityLevel || "HIGH"}<br>
                    District: ${p.district || ""}, ${p.state || ""}
                `);
            }
        }).addTo(hazardMap);
        hazardZoneLayers.push(zoneLayer);
    }

    // 3. Render Safe Relief Shelters
    if (liveSheltersData && liveSheltersData.length > 0) {
        liveSheltersData.forEach(s => {
            if (s.latitude && s.longitude) {
                const shelterMarker = L.marker([s.latitude, s.longitude], {
                    icon: createShelterIcon()
                }).addTo(hazardMap);

                shelterMarker.bindPopup(`
                    <div style="font-family: Arial, sans-serif; font-size: 13px;">
                        <h4 style="margin: 0 0 4px; color: #0284c7;">🏥 ${s.name}</h4>
                        <span style="color:#6b7280; font-size:11px;">Safe Relief Haven • ${s.district}, ${s.state}</span>
                        <hr style="margin: 6px 0; border: none; border-top: 1px solid #e5e7eb;">
                        <div><b>Safe Capacity:</b> ${s.effectiveSafeCapacity || s.maxCapacity} persons</div>
                        <div><b>Current Occupancy:</b> ${s.currentOccupancy || 0}</div>
                        <div><b>Available Beds:</b> <span style="color:#16a34a; font-weight:bold;">${s.availableBeds}</span></div>
                        <div><b>Medical Tier:</b> ${s.medicalSupportLevel || "BASIC"}</div>
                        <div><b>Backup Power:</b> ${s.hasBackupPower ? "Yes (Dedicated DG)" : "Grid Only"}</div>
                    </div>
                `);

                shelterMarkers.push(shelterMarker);
            }
        });
    }

    // 4. Render Active Evacuation Routes (OSRM Road Network)
    if (liveRoutesGeoJson && liveRoutesGeoJson.features) {
        const routeLayer = L.geoJSON(liveRoutesGeoJson, {
            style: function (feature) {
                const p = feature.properties || {};
                const isFocused = activeHighlightedPlanId && p.planId === activeHighlightedPlanId;
                return {
                    color: isFocused ? "#0284c7" : "#2563eb",
                    weight: isFocused ? 6 : 4,
                    opacity: isFocused ? 1.0 : 0.85,
                    dashArray: isFocused ? null : "6, 6"
                };
            },
            onEachFeature: function (feature, layer) {
                const p = feature.properties || {};
                const clearanceBadge = p.avoidsRedZones !== false
                    ? `<span style="color:#16a34a; font-weight:bold;">✓ Route Clear of Red Zones</span>`
                    : `<span style="color:#dc2626; font-weight:bold;">⚠️ Route Intersects Hazard Red Zone</span>`;

                layer.bindPopup(`
                    <div style="font-family: Arial, sans-serif; font-size: 13px; line-height:1.5;">
                        <h4 style="margin: 0 0 4px; color: #2563eb;">🛣️ OSRM Evacuation Route</h4>
                        <div><b>Plan:</b> ${p.planId || "DISPATCHED"}</div>
                        <div><b>Origin:</b> ${p.sourceHabitation || "Vulnerable Settlement"}</div>
                        <div><b>Safe Haven:</b> ${p.destinationShelter || "Safe Haven"}</div>
                        <div><b>Road Distance:</b> ${p.distanceKm || "N/A"} km</div>
                        <div><b>Est. Travel Time:</b> ${p.durationMinutes || "N/A"} min</div>
                        <div style="margin: 4px 0;">${clearanceBadge}</div>
                        <div style="font-size:11px; color:#475569;">${p.safetyNotes || "Verified road corridor"}</div>
                        <div style="margin-top:8px;">
                            <button class="primary-btn" style="padding:4px 8px; font-size:11px;" onclick="highlightEvacuationRoute('${p.planId}')">
                                🔍 Focus Route Corridor
                            </button>
                        </div>
                    </div>
                `);

                layer.on("click", function () {
                    if (p.planId) highlightEvacuationRoute(p.planId);
                });
            }
        }).addTo(hazardMap);
        evacuationRouteLayers.push(routeLayer);
    }

    // 5. Render Habitation Risk Points
    const filteredLocations = hazardLocations.filter(location => {
        const stateMatch = selectedState === "ALL" || location.state === selectedState;
        const riskMatch = selectedRisk === "ALL" || location.risk === selectedRisk;
        return stateMatch && riskMatch;
    });

    filteredLocations.forEach(location => {
        const marker = L.marker([location.lat, location.lng], {
            icon: createHazardIcon(location.risk)
        }).addTo(hazardMap);

        marker.bindTooltip(`
            <strong>${location.name}</strong><br>
            ${location.state} • ${location.risk} RISK<br>
            Population: ${location.population.toLocaleString()}
        `);

        marker.on("click", function () {
            showIndiaLocationDetails(location);
        });

        hazardMarkers.push(marker);
    });

    if (filteredLocations.length > 0) {
        const bounds = L.latLngBounds(filteredLocations.map(l => [l.lat, l.lng]));
        hazardMap.fitBounds(bounds, {
            padding: [40, 40],
            maxZoom: selectedState === "ALL" ? 5 : 9
        });
    } else {
        hazardMap.setView([22.5937, 78.9629], 5);
    }
}

/* =========================================
   LOCATION DETAILS SIDE PANEL
========================================= */

function showIndiaLocationDetails(location) {
    const panel = document.getElementById("locationDetails");
    if (!panel) return;

    const excess = Math.max(0, location.population - location.capacity);

    let riskClass = "risk-low";
    if (location.risk === "VERY HIGH") {
        riskClass = "risk-very-high";
    } else if (location.risk === "HIGH") {
        riskClass = "risk-high";
    } else if (location.risk === "MEDIUM") {
        riskClass = "risk-medium";
    }

    let actionHTML = "";
    if (excess > 0 && (location.risk === "VERY HIGH" || location.risk === "HIGH")) {
        actionHTML = `
            <div class="relocation-warning">
                ⚠️ Immediate relocation recommended.
                <br><br>
                ${excess.toLocaleString()} people exceed safe carrying capacity.
                <br><br>
                <button class="primary-btn" style="width:100%;" onclick="openRelocationModal('${location.id || location.name}')">
                    🚨 Plan Relocation Now
                </button>
            </div>
        `;
    } else {
        actionHTML = `
            <div class="safe-message">
                ✓ Habitation within safe geotechnical capacity. Routine monitoring advised.
            </div>
        `;
    }

    panel.innerHTML = `
        <h3>${location.name}</h3>
        <p>${location.district || ""}, ${location.state}</p>

        <span class="location-risk ${riskClass}">
            ${location.risk} RISK
        </span>

        <div class="detail-row">
            <span>Terrain / Hazard</span>
            <span>${location.hazard}</span>
        </div>

        <div class="detail-row">
            <span>Total Population</span>
            <span>${location.population.toLocaleString()}</span>
        </div>

        <div class="detail-row">
            <span>Safe Capacity</span>
            <span>${location.capacity.toLocaleString()}</span>
        </div>

        <div class="detail-row">
            <span>Vulnerable Group</span>
            <span>${location.vulnerable.toLocaleString()}</span>
        </div>

        <div class="detail-row">
            <span>Excess Evacuees</span>
            <span style="color:${excess > 0 ? '#dc2626' : '#15803d'}; font-weight:bold;">
                ${excess.toLocaleString()}
            </span>
        </div>

        ${actionHTML}
    `;
}

/* =========================================
   DASHBOARD METRICS & CHARTS
========================================= */

function updateDashboard() {
    const total = hazardLocations.length;
    const counts = { "VERY HIGH": 0, "HIGH": 0, "MEDIUM": 0, "LOW": 0 };
    let totalPopulation = 0;
    let totalVulnerable = 0;
    let relocationRequired = 0;

    hazardLocations.forEach(location => {
        counts[location.risk]++;
        totalPopulation += location.population;
        totalVulnerable += location.vulnerable;

        const excess = location.population - location.capacity;
        if (excess > 0 && (location.risk === "VERY HIGH" || location.risk === "HIGH")) {
            relocationRequired++;
        }
    });

    safeSetText("veryHighCount", counts["VERY HIGH"]);
    safeSetText("highCount", counts["HIGH"]);
    safeSetText("mediumCount", counts["MEDIUM"]);
    safeSetText("lowCount", counts["LOW"]);
    safeSetText("locationCount", total);
    safeSetText("populationCount", totalPopulation.toLocaleString());
    safeSetText("vulnerableCount", totalVulnerable.toLocaleString());
    safeSetText("relocationCount", relocationRequired);

    const veryHighPercent = total > 0 ? (counts["VERY HIGH"] / total) * 100 : 0;
    const highPercent = total > 0 ? (counts["HIGH"] / total) * 100 : 0;
    const mediumPercent = total > 0 ? (counts["MEDIUM"] / total) * 100 : 0;
    const lowPercent = total > 0 ? (counts["LOW"] / total) * 100 : 0;

    setRiskProgress("veryHigh", veryHighPercent);
    setRiskProgress("high", highPercent);
    setRiskProgress("medium", mediumPercent);
    setRiskProgress("low", lowPercent);

    updateAlerts();
}

function safeSetText(id, val) {
    const el = document.getElementById(id);
    if (el) el.textContent = val;
}

function setRiskProgress(type, percentage) {
    const rounded = percentage.toFixed(0);
    const textEl = document.getElementById(type + "Percent");
    const barEl = document.getElementById(type + "Bar");
    if (textEl) textEl.textContent = rounded + "%";
    if (barEl) barEl.style.width = percentage + "%";
}

function updateAlerts() {
    const container = document.getElementById("alertsContainer");
    if (!container) return;
    container.innerHTML = "";

    const alerts = hazardLocations
        .filter(l => l.risk === "VERY HIGH" || l.risk === "HIGH")
        .slice(0, 5);

    alerts.forEach(location => {
        const color = location.risk === "VERY HIGH" ? "red" : "orange";
        container.innerHTML += `
            <div class="alert-item">
                <span class="alert-dot ${color}"></span>
                <div class="alert-content">
                    <strong>${location.name}</strong>
                    <p>${location.hazard} • ${location.risk} Risk (${location.state})</p>
                </div>
            </div>
        `;
    });
}

/* ===================================================================
   PHASE 5: RELOCATION COMMAND CENTER & OSRM DISPATCH LOGIC
=================================================================== */

function switchRelocationTab(tab) {
    const queueSec = document.getElementById("relocSectionQueue");
    const plansSec = document.getElementById("relocSectionPlans");
    const sheltersSec = document.getElementById("relocSectionShelters");

    const tabQueue = document.getElementById("relocTabQueue");
    const tabPlans = document.getElementById("relocTabPlans");
    const tabShelters = document.getElementById("relocTabShelters");

    if (queueSec) queueSec.style.display = tab === "queue" ? "block" : "none";
    if (plansSec) plansSec.style.display = tab === "plans" ? "block" : "none";
    if (sheltersSec) sheltersSec.style.display = tab === "shelters" ? "block" : "none";

    if (tabQueue) tabQueue.className = tab === "queue" ? "secondary-btn active-tab" : "secondary-btn";
    if (tabPlans) tabPlans.className = tab === "plans" ? "secondary-btn active-tab" : "secondary-btn";
    if (tabShelters) tabShelters.className = tab === "shelters" ? "secondary-btn active-tab" : "secondary-btn";
}

function updateRelocationMetricsBar() {
    const queueLocations = hazardLocations.filter(l => l.population > l.capacity);
    const queueCount = queueLocations.length;
    const totalExcess = queueLocations.reduce((sum, l) => sum + Math.max(0, l.population - l.capacity), 0);

    const plans = liveRelocationPlans && liveRelocationPlans.length > 0 ? liveRelocationPlans : baselineRelocationPlans;
    const shelters = liveSheltersData && liveSheltersData.length > 0 ? liveSheltersData : baselineShelters;

    let totalHeadroom = 0;
    shelters.forEach(s => {
        const safeCap = s.effectiveSafeCapacity || s.maxCapacity;
        const occ = s.currentOccupancy || 0;
        totalHeadroom += Math.max(0, safeCap - occ);
    });

    safeSetText("relocQueueCount", queueCount);
    safeSetText("relocTotalEvacuees", totalExcess.toLocaleString());
    safeSetText("relocActivePlansCount", plans.length);
    safeSetText("relocShelterHeadroom", totalHeadroom.toLocaleString() + " beds");
}

function updateRelocationTable() {
    const table = document.getElementById("relocationTable");
    if (!table) return;
    table.innerHTML = "";

    const locations = hazardLocations
        .filter(l => l.population > l.capacity)
        .sort((a, b) => (b.population - b.capacity) - (a.population - a.capacity));

    if (locations.length === 0) {
        table.innerHTML = `<tr><td colspan="8" style="text-align:center; padding:20px; color:#15803d;">✓ All habitations safely accommodated within geotechnical carrying capacity.</td></tr>`;
        return;
    }

    locations.forEach(location => {
        const excess = location.population - location.capacity;
        let riskClass = location.risk === "VERY HIGH" ? "risk-very-high" : (location.risk === "HIGH" ? "risk-high" : "risk-medium");

        table.innerHTML += `
            <tr>
                <td>
                    <strong>${location.name}</strong>
                    ${location.inRedZone ? `<span style="font-size:10px; color:#dc2626; display:block; font-weight:bold;">⚠️ Inside Red Zone</span>` : ""}
                </td>
                <td>${location.district ? location.district + ", " : ""}${location.state}</td>
                <td>${location.hazard}</td>
                <td>${location.population.toLocaleString()}</td>
                <td><span class="table-risk ${riskClass}">${location.risk}</span></td>
                <td>${location.capacity.toLocaleString()}</td>
                <td><strong style="color:#dc2626; font-size:14px;">+${excess.toLocaleString()}</strong></td>
                <td>
                    <button class="primary-btn" style="padding:6px 12px; font-size:12px;" onclick="openRelocationModal('${location.id || location.name}')">
                        🚨 Plan Relocation
                    </button>
                </td>
            </tr>
        `;
    });
}

function updateDispatchedPlansTable() {
    const table = document.getElementById("dispatchedPlansTable");
    if (!table) return;
    table.innerHTML = "";

    const plans = liveRelocationPlans && liveRelocationPlans.length > 0 ? liveRelocationPlans : baselineRelocationPlans;

    if (plans.length === 0) {
        table.innerHTML = `
            <tr>
                <td colspan="9" style="text-align:center; color:#6b7280; padding:25px;">
                    No relocation plans dispatched yet. Select a habitation from the queue to dispatch.
                </td>
            </tr>
        `;
        return;
    }

    plans.forEach(plan => {
        const firstAssignment = plan.assignments && plan.assignments.length > 0 ? plan.assignments[0] : null;
        const firstRoute = plan.routes && plan.routes.length > 0 ? plan.routes[0] : null;

        const origin = firstAssignment ? firstAssignment.sourceHabitationName : (plan.notes ? plan.notes.split("to")[0].replace("Priority evacuation from", "").trim() : "Origin Habitation");
        const destination = firstAssignment ? firstAssignment.destinationShelterName : "Safe Haven";
        const evacuees = plan.totalEvacuees || (firstAssignment ? firstAssignment.allocatedEvacuees : 0);
        const buses = plan.totalBusesRequired || Math.ceil(evacuees / 40);
        const ambulances = plan.totalAmbulancesRequired || Math.max(1, Math.ceil(evacuees * 0.25 / 20));

        const distKm = firstRoute && firstRoute.distanceKm ? firstRoute.distanceKm.toFixed(1) + " km" : "28.5 km";
        const durationMin = firstRoute && firstRoute.durationMinutes ? firstRoute.durationMinutes.toFixed(0) + " min" : "42 min";

        const statusClass = `status-${(plan.status || 'dispatched').toLowerCase()}`;
        const isSafe = firstRoute ? firstRoute.avoidsRedZones : true;

        table.innerHTML += `
            <tr>
                <td><strong>${plan.planCode || plan.planId}</strong></td>
                <td>${origin}</td>
                <td>🏥 ${destination}</td>
                <td><strong>${evacuees.toLocaleString()}</strong></td>
                <td>🚌 ${buses} Buses • 🚑 ${ambulances} Amb</td>
                <td>${distKm} (est. ${durationMin})</td>
                <td>
                    ${isSafe
                ? `<span style="color:#16a34a; font-weight:bold;">✓ Clear Corridor</span>`
                : `<span style="color:#dc2626; font-weight:bold;">⚠️ Escorted Route</span>`}
                </td>
                <td>
                    <span class="plan-status-badge ${statusClass}">${plan.status || "DISPATCHED"}</span>
                </td>
                <td>
                    <div style="display:flex; gap:6px;">
                        <button class="primary-btn" style="padding:5px 9px; font-size:11px;" onclick="viewRouteOnMap('${plan.planId}')">
                            🗺️ Map
                        </button>
                        <button class="secondary-btn" style="padding:5px 9px; font-size:11px;" onclick="advancePlanStatusPrompt('${plan.planId}', '${plan.status || "DISPATCHED"}')">
                            ⏩ Update
                        </button>
                    </div>
                </td>
            </tr>
        `;
    });
}

function updateSheltersCapacityTable() {
    const table = document.getElementById("sheltersCapacityTable");
    if (!table) return;
    table.innerHTML = "";

    const shelters = liveSheltersData && liveSheltersData.length > 0 ? liveSheltersData : baselineShelters;

    shelters.forEach(s => {
        const safeCap = s.effectiveSafeCapacity || s.maxCapacity;
        const currentOcc = s.currentOccupancy || 0;
        const headroom = Math.max(0, safeCap - currentOcc);
        const occPercent = Math.min(100, Math.round((currentOcc / safeCap) * 100));

        let occColor = "#16a34a";
        if (occPercent >= 90) occColor = "#dc2626";
        else if (occPercent >= 70) occColor = "#f97316";

        table.innerHTML += `
            <tr>
                <td><strong>🏥 ${s.name}</strong></td>
                <td>${s.district}, ${s.state}</td>
                <td>${safeCap.toLocaleString()}</td>
                <td>
                    <div class="occupancy-meter">
                        <div class="occupancy-track">
                            <div class="occupancy-fill" style="width:${occPercent}%; background:${occColor};"></div>
                        </div>
                        <span style="font-size:12px; font-weight:bold;">${currentOcc.toLocaleString()} (${occPercent}%)</span>
                    </div>
                </td>
                <td><strong style="color:#15803d; font-size:13px;">${headroom.toLocaleString()} beds</strong></td>
                <td>${(s.potableWaterLitersDay || 15000).toLocaleString()} L/day</td>
                <td><span class="table-risk risk-low" style="background:#dbeafe; color:#1d4ed8;">${s.medicalSupportLevel || 'BASIC'}</span></td>
                <td>${s.hasBackupPower ? '⚡ Dedicated DG' : 'Grid Only'}</td>
                <td>
                    <button class="secondary-btn" style="padding:5px 9px; font-size:11px;" onclick="locateShelterOnMap('${s.id}')">
                        📍 Locate
                    </button>
                </td>
            </tr>
        `;
    });
}

function updateMapRoutesPanel() {
    const container = document.getElementById("mapRoutesList");
    if (!container) return;
    container.innerHTML = "";

    const features = (liveRoutesGeoJson && liveRoutesGeoJson.features && liveRoutesGeoJson.features.length > 0)
        ? liveRoutesGeoJson.features
        : (baselineRoutesGeoJson && baselineRoutesGeoJson.features ? baselineRoutesGeoJson.features : []);

    if (features.length === 0) {
        container.innerHTML = `<p style="color:#6b7280; font-size:12px;">No active evacuation routes dispatched yet.</p>`;
        return;
    }

    features.forEach(f => {
        const p = f.properties || {};
        const isActive = activeHighlightedPlanId && p.planId === activeHighlightedPlanId;

        container.innerHTML += `
            <div class="route-card-item ${isActive ? 'active' : ''}" onclick="highlightEvacuationRoute('${p.planId}')">
                <div style="font-weight:bold; font-size:12px; color:#1e40af;">
                    🛣️ ${p.sourceHabitation || 'Settlement'} ➔ ${p.destinationShelter || 'Haven'}
                </div>
                <div style="font-size:11px; color:#475569; margin-top:3px;">
                    Distance: <b>${p.distanceKm || 'N/A'} km</b> • Time: <b>${p.durationMinutes || 'N/A'} min</b>
                </div>
                <div style="font-size:10px; color:${p.avoidsRedZones !== false ? '#15803d' : '#b91c1c'}; font-weight:bold; margin-top:2px;">
                    ${p.avoidsRedZones !== false ? '✓ Red-Zone Clearance Verified' : '⚠️ Convoy Escort Required'}
                </div>
            </div>
        `;
    });
}

function highlightEvacuationRoute(planId) {
    activeHighlightedPlanId = planId;
    showPage("map");

    const features = (liveRoutesGeoJson && liveRoutesGeoJson.features && liveRoutesGeoJson.features.length > 0)
        ? liveRoutesGeoJson.features
        : (baselineRoutesGeoJson && baselineRoutesGeoJson.features ? baselineRoutesGeoJson.features : []);

    const routeFeature = features.find(f => f.properties && f.properties.planId === planId) || features[0];
    const banner = document.getElementById("activeRouteBanner");

    if (routeFeature && routeFeature.geometry && routeFeature.geometry.coordinates) {
        const p = routeFeature.properties || {};
        if (banner) {
            banner.style.display = "flex";
            banner.innerHTML = `
                <div>
                    <strong>🛣️ Active Evacuation Corridor:</strong>
                    <span>${p.sourceHabitation || 'Settlement'} ➔ ${p.destinationShelter || 'Safe Haven'}</span>
                    <span style="margin-left:12px; color:#93c5fd;">| 📍 ${p.distanceKm || 'N/A'} km</span>
                    <span style="margin-left:8px; color:#93c5fd;">| ⏱️ est. ${p.durationMinutes || 'N/A'} min</span>
                    <span style="margin-left:8px; color:${p.avoidsRedZones !== false ? '#86efac' : '#fca5a5'};">
                        (${p.avoidsRedZones !== false ? '✓ Red Zones Avoided' : '⚠️ Escorted Convoy'})
                    </span>
                </div>
                <div>
                    <button onclick="closeRouteHighlight()" style="background:#374151; color:white; border:none; padding:5px 10px; border-radius:5px; cursor:pointer; font-weight:bold;">
                        ✕ Close Focus
                    </button>
                </div>
            `;
        }

        if (hazardMap) {
            displayIndiaHazards();
            const coords = routeFeature.geometry.coordinates.map(c => [c[1], c[0]]);
            const bounds = L.latLngBounds(coords);
            hazardMap.fitBounds(bounds, { padding: [60, 60], maxZoom: 12 });
        }
    }

    updateMapRoutesPanel();
}

function closeRouteHighlight() {
    activeHighlightedPlanId = null;
    const banner = document.getElementById("activeRouteBanner");
    if (banner) banner.style.display = "none";
    displayIndiaHazards();
    updateMapRoutesPanel();
}

function locateShelterOnMap(shelterId) {
    showPage("map");
    const shelters = liveSheltersData && liveSheltersData.length > 0 ? liveSheltersData : baselineShelters;
    const shelter = shelters.find(s => s.id === shelterId);
    if (shelter && shelter.latitude && shelter.longitude && hazardMap) {
        hazardMap.setView([shelter.latitude, shelter.longitude], 13);
        showToast(`Centered on Safe Relief Haven: ${shelter.name}`);
    }
}

async function advancePlanStatusPrompt(planId, currentStatus) {
    const nextStatus = currentStatus === "DISPATCHED" ? "EN_ROUTE" : (currentStatus === "EN_ROUTE" ? "COMPLETED" : "DISPATCHED");

    try {
        const res = await fetch(`${API_BASE}/api/v1/relocation/plans/${planId}/status?status=${nextStatus}`, {
            method: "PATCH"
        });
        if (res.ok) {
            const data = await res.json();
            if (data.success && data.data) {
                const idx = liveRelocationPlans.findIndex(p => p.planId === planId);
                if (idx !== -1) {
                    liveRelocationPlans[idx] = data.data;
                }
                updateDispatchedPlansTable();
                showToast(`Mission ${data.data.planCode}: Status updated to ${nextStatus}`);
                return;
            }
        }
    } catch (e) {
        console.warn("Backend status update failed, updating local state:", e);
    }

    // Fallback local update
    const target = (liveRelocationPlans.find(p => p.planId === planId) || baselineRelocationPlans.find(p => p.planId === planId));
    if (target) {
        target.status = nextStatus;
        updateDispatchedPlansTable();
        showToast(`Simulated status updated to ${nextStatus}`);
    }
}

/* ===================================================================
   INTERACTIVE RELOCATION PLANNING & OSRM ROUTE DISPATCH MODAL
=================================================================== */

async function openRelocationModal(locationKey) {
    let hab;
    if (locationKey) {
        hab = hazardLocations.find(l => l.id === locationKey || l.name === locationKey);
    }
    if (!hab) {
        hab = hazardLocations.find(l => l.population > l.capacity) || hazardLocations[0];
    }
    if (!hab) {
        showToast("No habitations available for relocation.");
        return;
    }

    modalSelectedHabitation = hab;
    modalSelectedShelterId = null;
    // Reset dispatch button for the newly selected habitation
    const dispatchBtn = document.getElementById("dispatchPlanBtn");

    if (dispatchBtn) {
        dispatchBtn.disabled = false;
        dispatchBtn.textContent = "🚀 Dispatch Relocation & Compute OSRM Route";
    }

    const modal = document.getElementById("relocationModal");
    const nameEl = document.getElementById("modalHabitationName");
    const subEl = document.getElementById("modalHabitationSub");
    const popCapEl = document.getElementById("modalPopCap");
    const excessEl = document.getElementById("modalExcess");
    const riskBadge = document.getElementById("modalRiskBadge");
    const weatherEl = document.getElementById("modalWeather");
    const sheltersList = document.getElementById("modalSheltersList");
    const planResult = document.getElementById("modalPlanResult");

    nameEl.textContent = hab.name;
    subEl.textContent = `${hab.district || ""}, ${hab.state} • Elevation: ${hab.lat.toFixed(4)}°N, ${hab.lng.toFixed(4)}°E`;
    popCapEl.textContent = `${hab.population.toLocaleString()} / ${hab.capacity.toLocaleString()}`;

    const excess = Math.max(0, hab.population - hab.capacity);
    excessEl.textContent = `${excess.toLocaleString()} persons`;
    riskBadge.textContent = hab.risk;
    riskBadge.className = `table-risk ${hab.risk === "VERY HIGH" ? "risk-very-high" : "risk-high"}`;

    weatherEl.textContent = "Querying Open-Meteo telemetry...";
    sheltersList.innerHTML = `<div style="padding:15px; text-align:center; color:#6b7280;">Fetching ranked safe shelters...</div>`;
    planResult.style.display = "none";
    planResult.innerHTML = "";

    modal.style.display = "flex";

    // 1. Fetch live weather for habitation
    try {
        const wRes = await fetch(`${API_BASE}/api/v1/weather/telemetry/${hab.id}`);
        if (wRes.ok) {
            const wData = await wRes.json();
            if (wData.success && wData.data) {
                const tel = wData.data;
                weatherEl.textContent = `${tel.temperatureC}°C • 24h Rain: ${tel.precipitationLast24hMm}mm (${tel.alertTier} Alert)`;
            }
        } else {
            weatherEl.textContent = "22.5°C • Rain: Normal (Baseline)";
        }
    } catch {
        weatherEl.textContent = "22.5°C • Rain: Normal (Standalone)";
    }

    // 2. Fetch ranked safe shelter recommendations
    try {
        const sRes = await fetch(`${API_BASE}/api/v1/relocation/recommendations/${hab.id}`);
        if (sRes.ok) {
            const sData = await sRes.json();
            if (sData.success && sData.data && sData.data.length > 0) {
                modalCandidateShelters = sData.data;
                renderModalShelters(sData.data);
                return;
            }
        }
    } catch (e) {
        console.warn("Could not fetch remote shelter recommendations:", e);
    }

    // Fallback: render baseline shelters if offline
    renderModalShelters([
        {
            shelterId: "sh-ka-01",
            shelterName: "Madikeri Community Relief Haven",
            district: "Kodagu",
            state: "Karnataka",
            distanceKm: 24.5,
            estimatedTravelMinutes: 32.0,
            remainingSafeCapacity: 950,
            medicalSupportLevel: "INTERMEDIATE",
            suitabilityScore: 92.5,
            recommendationRationale: "Certified green-zone haven with dedicated DG backup power."
        },
        {
            shelterId: "sh-kl-01",
            shelterName: "Meppadi High Ground Shelter",
            district: "Wayanad",
            state: "Kerala",
            distanceKm: 31.0,
            estimatedTravelMinutes: 44.0,
            remainingSafeCapacity: 1200,
            medicalSupportLevel: "ADVANCED",
            suitabilityScore: 88.0,
            recommendationRationale: "Elevated plateau site with on-site trauma triage."
        }
    ]);
}

function renderModalShelters(shelters) {
    const container = document.getElementById("modalSheltersList");
    container.innerHTML = "";

    modalSelectedShelterId = shelters[0].shelterId;

    shelters.forEach((s, idx) => {
        const isSelected = idx === 0 ? "selected" : "";
        container.innerHTML += `
            <div class="shelter-option ${isSelected}" id="sopt-${s.shelterId}" onclick="selectModalShelter('${s.shelterId}')">
                <div class="shelter-info">
                    <strong>🏥 ${s.shelterName}</strong>
                    <span>${s.district}, ${s.state} • ${s.distanceKm} km (est. ${s.estimatedTravelMinutes} min)</span>
                    <span style="display:block; color:#16a34a; font-weight:bold; font-size:11px; margin-top:2px;">
                        ${s.remainingSafeCapacity} beds available • Medical: ${s.medicalSupportLevel}
                    </span>
                </div>
                <div class="shelter-badge">
                    <span class="suitability-pill">Score: ${s.suitabilityScore}</span>
                </div>
            </div>
        `;
    });
}

function selectModalShelter(shelterId) {
    modalSelectedShelterId = shelterId;
    document.querySelectorAll(".shelter-option").forEach(el => el.classList.remove("selected"));
    const selectedEl = document.getElementById(`sopt-${shelterId}`);
    if (selectedEl) selectedEl.classList.add("selected");
}

function closeRelocationModal() {
    const modal = document.getElementById("relocationModal");
    if (modal) modal.style.display = "none";
}

async function dispatchSelectedRelocation() {
    if (!modalSelectedHabitation) return;

    const btn = document.getElementById("dispatchPlanBtn");
    const resultBox = document.getElementById("modalPlanResult");
    btn.disabled = true;
    btn.textContent = "⏳ Computing Road Route via OSRM...";

    try {
        const body = {
            habitationId: modalSelectedHabitation.id,
            targetShelterId: modalSelectedShelterId
        };

        const res = await fetch(`${API_BASE}/api/v1/relocation/plans`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });

        if (res.ok) {
            const data = await res.json();
            if (data.success && data.data) {
                const plan = data.data;
                const route = plan.routes && plan.routes.length > 0 ? plan.routes[0] : null;

                // Prepend to live plans
                liveRelocationPlans.unshift(plan);

                // Refresh routes in background
                const rtRes = await fetch(`${API_BASE}/api/v1/relocation/routes/geojson`);
                if (rtRes.ok) {
                    const rtJson = await rtRes.json();
                    if (rtJson.success && rtJson.data) liveRoutesGeoJson = rtJson.data;
                }

                // Refresh shelter capacity
                const shRes = await fetch(`${API_BASE}/api/v1/shelters`);
                if (shRes.ok) {
                    const shJson = await shRes.json();
                    if (shJson.success && shJson.data) liveSheltersData = shJson.data;
                }

                updateDispatchedPlansTable();
                updateSheltersCapacityTable();
                updateRelocationMetricsBar();
                updateMapRoutesPanel();

                resultBox.style.display = "block";
                resultBox.innerHTML = `
                    <div style="color:#15803d; font-weight:bold; font-size:15px; margin-bottom:8px;">
                        ✓ Plan Dispatched: ${plan.planCode} (Status: ${plan.status})
                    </div>
                    <div style="font-size:13px; line-height:1.6;">
                        <div><b>Evacuees Assigned:</b> ${plan.totalEvacuees.toLocaleString()} persons</div>
                        <div><b>Convoy Fleet:</b> 🚌 ${plan.totalBusesRequired} Buses • 🚑 ${plan.totalAmbulancesRequired} Ambulances</div>
                        ${route ? `<div><b>OSRM Road Network Distance:</b> ${route.distanceKm} km (est. ${route.durationMinutes} min)</div>` : ""}
                        <div><b>Clearance:</b> ${route && route.avoidsRedZones ? '<span style="color:#15803d; font-weight:bold;">✓ Route Avoids Red Zones</span>' : '<span style="color:#dc2626; font-weight:bold;">⚠️ Escorted Convoy</span>'}</div>
                    </div>
                    <div style="margin-top:12px;">
                        <button class="primary-btn" onclick="viewRouteOnMap('${plan.planId}')">
                            🗺️ View Evacuation Route on Map
                        </button>
                    </div>
                `;

                showToast(`Relocation Plan ${plan.planCode} successfully dispatched!`);
                btn.textContent = "✓ Relocation Dispatched";
                return;
            }
        }
    } catch (e) {
        console.warn("Backend dispatch failed, showing simulated route:", e);
    }

    // Fallback display if backend is offline
    const excess = Math.max(100, modalSelectedHabitation.population - modalSelectedHabitation.capacity);
    const buses = Math.ceil(excess / 40);
    const ambulances = Math.max(1, Math.ceil(excess * 0.25 / 20));
    const simulatedPlanId = "plan-sim-" + Date.now();
    const simulatedPlan = {
        planId: simulatedPlanId,
        planCode: "PLAN-SIM-" + Math.floor(1000 + Math.random() * 9000),
        status: "DISPATCHED",
        totalEvacuees: excess,
        totalBusesRequired: buses,
        totalAmbulancesRequired: ambulances,
        notes: `Priority evacuation from ${modalSelectedHabitation.name}`,
        assignments: [{
            sourceHabitationName: modalSelectedHabitation.name,
            destinationShelterName: modalSelectedShelterId ? (liveSheltersData.find(s => s.id === modalSelectedShelterId) || {}).name || "Relief Haven" : "Relief Haven",
            allocatedEvacuees: excess
        }],
        routes: [{
            planId: simulatedPlanId,
            sourceHabitationName: modalSelectedHabitation.name,
            destinationShelterName: "Relief Haven",
            distanceKm: 28.5,
            durationMinutes: 42.0,
            avoidsRedZones: true,
            validatedSafe: true
        }]
    };

    liveRelocationPlans.unshift(simulatedPlan);
    updateDispatchedPlansTable();
    updateRelocationMetricsBar();
    updateMapRoutesPanel();

    resultBox.style.display = "block";
    resultBox.innerHTML = `
        <div style="color:#15803d; font-weight:bold; font-size:15px; margin-bottom:8px;">
            ✓ Simulated Plan Dispatched: ${simulatedPlan.planCode} (Status: DISPATCHED)
        </div>
        <div style="font-size:13px; line-height:1.6;">
            <div><b>Evacuees Assigned:</b> ${excess.toLocaleString()} persons</div>
            <div><b>Convoy Fleet:</b> 🚌 ${buses} Buses • 🚑 ${ambulances} Ambulances</div>
            <div><b>Road Distance:</b> 28.5 km (est. 42 min via OSRM fallback)</div>
        </div>
        <div style="margin-top:12px;">
            <button class="primary-btn" onclick="closeRelocationModal(); showPage('map');">
                🗺️ Switch to Map
            </button>
        </div>
    `;

    btn.disabled = false;
    btn.textContent = "🚀 Dispatch Relocation & Compute OSRM Route";
}

function viewRouteOnMap(planId) {
    closeRelocationModal();
    highlightEvacuationRoute(planId);
}

function showToast(msg) {
    const existing = document.querySelector(".toast");
    if (existing) existing.remove();

    const toast = document.createElement("div");
    toast.className = "toast";
    toast.textContent = msg;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = "0";
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

/* =========================================
   MAP FILTER LISTENERS
========================================= */

const stateFilterEl = document.getElementById("stateFilter");
if (stateFilterEl) stateFilterEl.addEventListener("change", displayIndiaHazards);

const riskFilterEl = document.getElementById("riskFilter");
if (riskFilterEl) riskFilterEl.addEventListener("change", displayIndiaHazards);

/* =========================================
   RISK ASSESSMENT FORM HANDLER
========================================= */

const riskForm = document.getElementById("riskForm");
if (riskForm) {
    riskForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const location = document.getElementById("locationName").value.trim();
        const population = Number(document.getElementById("population").value);
        const capacity = Number(document.getElementById("capacity").value);
        const hazard = Number(document.getElementById("hazardLevel").value);
        const vulnerability = Number(document.getElementById("vulnerability").value);

        if (!location || population < 0 || capacity <= 0) {
            alert("Please enter valid parameters.");
            return;
        }

        const hazardScore = (hazard / 4) * 100;
        const vulnerabilityScore = (vulnerability / 3) * 100;

        let capacityPressure = 0;
        if (population > capacity) {
            capacityPressure = ((population - capacity) / capacity) * 100;
        }
        capacityPressure = Math.min(100, Math.max(0, capacityPressure));

        const riskScore = (hazardScore * 0.45) + (vulnerabilityScore * 0.35) + (capacityPressure * 0.20);

        let classification = "LOW";
        if (riskScore >= 75) classification = "VERY HIGH";
        else if (riskScore >= 50) classification = "HIGH";
        else if (riskScore >= 25) classification = "MEDIUM";

        const excess = Math.max(0, population - capacity);
        const relocation = excess > 0 && (classification === "VERY HIGH" || classification === "HIGH");

        let resultColor = "#22c55e";
        if (classification === "VERY HIGH") resultColor = "#dc2626";
        else if (classification === "HIGH") resultColor = "#f97316";
        else if (classification === "MEDIUM") resultColor = "#eab308";

        document.getElementById("riskResult").innerHTML = `
            <div class="result-box">
                <h3>Multi-Factor Risk Assessment</h3>
                <p>Location: <strong>${location}</strong></p>
                <div class="result-score" style="color:${resultColor}">
                    ${riskScore.toFixed(1)} / 100
                </div>
                <h2 style="color:${resultColor}; margin-bottom:10px;">${classification} RISK</h2>
                <p>Hazard Proximity Index: ${hazardScore.toFixed(1)}</p>
                <p>Environmental Vulnerability: ${vulnerabilityScore.toFixed(1)}</p>
                <p>Carrying Capacity Pressure: ${capacityPressure.toFixed(1)}</p>
                <br>
                <p>Excess Population: <strong>${excess.toLocaleString()}</strong></p>
                <br>
                ${relocation ? `
                    <div class="relocation-warning">
                        ⚠️ Priority Relocation Recommended for this habitation.
                    </div>
                ` : `
                    <div class="safe-message">
                        ✓ Habitation is safely accommodated within current capacity.
                    </div>
                `}
            </div>
        `;
    });
}

/* =========================================
   REPORT GENERATION
========================================= */

function generateReport() {
    const total = hazardLocations.length;
    const veryHigh = hazardLocations.filter(l => l.risk === "VERY HIGH").length;
    const high = hazardLocations.filter(l => l.risk === "HIGH").length;
    const medium = hazardLocations.filter(l => l.risk === "MEDIUM").length;
    const low = hazardLocations.filter(l => l.risk === "LOW").length;

    const population = hazardLocations.reduce((sum, l) => sum + l.population, 0);
    const vulnerable = hazardLocations.reduce((sum, l) => sum + l.vulnerable, 0);
    const relocation = hazardLocations.filter(l => l.population > l.capacity && (l.risk === "VERY HIGH" || l.risk === "HIGH")).length;

    const reportWindow = window.open("", "_blank");
    reportWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
            <title>ABHEDYA AI — Disaster Assessment & Relocation Report</title>
            <style>
                body { font-family: Arial, sans-serif; padding: 40px; color: #172033; line-height: 1.5; }
                h1 { color: #dc2626; margin-bottom: 5px; }
                .box { padding: 20px; border: 1px solid #ddd; margin: 15px 0; border-radius: 8px; }
                table { width: 100%; border-collapse: collapse; margin-top: 10px; }
                th, td { padding: 10px 12px; border: 1px solid #ddd; text-align: left; font-size: 13px; }
                th { background: #f9fafb; }
            </style>
        </head>
        <body>
            <h1>ABHEDYA AI — National Disaster Intelligence Report</h1>
            <p style="color:#6b7280; font-size:12px;">Disaster Management &amp; Intelligent Relocation Command Center • Generated: ${new Date().toLocaleString()}</p>
            <hr>
            <div class="box">
                <h2>Operational Summary</h2>
                <p>Locations Analysed: <b>${total}</b></p>
                <p>Total Exposed Population: <b>${population.toLocaleString()}</b></p>
                <p>Vulnerable Population: <b>${vulnerable.toLocaleString()}</b></p>
                <p>Habitations Requiring Relocation: <b style="color:#dc2626;">${relocation}</b></p>
            </div>
            <div class="box">
                <h2>Risk Distribution</h2>
                <table>
                    <tr><th>Risk Level</th><th>Habitations Count</th></tr>
                    <tr><td>Very High Risk</td><td>${veryHigh}</td></tr>
                    <tr><td>High Risk</td><td>${high}</td></tr>
                    <tr><td>Medium Risk</td><td>${medium}</td></tr>
                    <tr><td>Low Risk</td><td>${low}</td></tr>
                </table>
            </div>
            <script>
                window.onload = function() { window.print(); };
            <\/script>
        </body>
        </html>
    `);
    reportWindow.document.close();
}

/* =========================================
   APPLICATION BOOTSTRAP
========================================= */

updateDashboard();
updateRelocationTable();
updateDispatchedPlansTable();
updateSheltersCapacityTable();
updateRelocationMetricsBar();
updateMapRoutesPanel();

setTimeout(() => {
    initializeIndiaMap();
    checkBackendConnectivity();
}, 250);
