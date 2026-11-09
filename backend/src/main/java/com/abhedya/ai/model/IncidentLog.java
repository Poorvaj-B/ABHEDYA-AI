package com.abhedya.ai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incident_logs")
public class IncidentLog {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "incident_type", nullable = false, length = 50)
    private String incidentType;

    @Column(nullable = false, length = 30)
    private String severity;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "affected_state", length = 100)
    private String affectedState;

    @Column(name = "affected_district", length = 100)
    private String affectedDistrict;

    @Column(nullable = false, length = 50)
    private String source;

    @Column(name = "logged_at", updatable = false)
    private LocalDateTime loggedAt = LocalDateTime.now();

    public IncidentLog() {}

    public IncidentLog(String id, String incidentType, String severity, String title, String description,
                       String affectedState, String affectedDistrict, String source) {
        this.id = id;
        this.incidentType = incidentType;
        this.severity = severity;
        this.title = title;
        this.description = description;
        this.affectedState = affectedState;
        this.affectedDistrict = affectedDistrict;
        this.source = source;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAffectedState() { return affectedState; }
    public void setAffectedState(String affectedState) { this.affectedState = affectedState; }

    public String getAffectedDistrict() { return affectedDistrict; }
    public void setAffectedDistrict(String affectedDistrict) { this.affectedDistrict = affectedDistrict; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
}

