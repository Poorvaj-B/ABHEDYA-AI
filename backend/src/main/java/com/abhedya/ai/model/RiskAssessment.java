package com.abhedya.ai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessment {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitation_id", nullable = false)
    private Habitation habitation;

    @Column(name = "hazard_index", nullable = false)
    private Double hazardIndex;

    @Column(name = "vulnerability_index", nullable = false)
    private Double vulnerabilityIndex;

    @Column(name = "capacity_pressure_index", nullable = false)
    private Double capacityPressureIndex;

    @Column(name = "vulnerable_ratio_index")
    private Double vulnerableRatioIndex = 0.0;

    @Column(name = "composite_score", nullable = false)
    private Double compositeScore;

    @Column(nullable = false, length = 30)
    private String classification;

    @Column(name = "excess_population", nullable = false)
    private Integer excessPopulation;

    @Column(name = "relocation_recommended", nullable = false)
    private Boolean relocationRecommended;

    @Column(name = "rainfall_24h_mm")
    private Double rainfall24hMm = 0.0;

    @Column(name = "model_weights_json", columnDefinition = "TEXT")
    private String modelWeightsJson;

    @Column(name = "assessment_rationale", columnDefinition = "TEXT")
    private String assessmentRationale;

    @Column(name = "assessed_at")
    private LocalDateTime assessedAt = LocalDateTime.now();

    public RiskAssessment() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Habitation getHabitation() { return habitation; }
    public void setHabitation(Habitation habitation) { this.habitation = habitation; }

    public Double getHazardIndex() { return hazardIndex; }
    public void setHazardIndex(Double hazardIndex) { this.hazardIndex = hazardIndex; }

    public Double getVulnerabilityIndex() { return vulnerabilityIndex; }
    public void setVulnerabilityIndex(Double vulnerabilityIndex) { this.vulnerabilityIndex = vulnerabilityIndex; }

    public Double getCapacityPressureIndex() { return capacityPressureIndex; }
    public void setCapacityPressureIndex(Double capacityPressureIndex) { this.capacityPressureIndex = capacityPressureIndex; }

    public Double getVulnerableRatioIndex() { return vulnerableRatioIndex; }
    public void setVulnerableRatioIndex(Double vulnerableRatioIndex) { this.vulnerableRatioIndex = vulnerableRatioIndex; }

    public Double getCompositeScore() { return compositeScore; }
    public void setCompositeScore(Double compositeScore) { this.compositeScore = compositeScore; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    public Integer getExcessPopulation() { return excessPopulation; }
    public void setExcessPopulation(Integer excessPopulation) { this.excessPopulation = excessPopulation; }

    public Boolean getRelocationRecommended() { return relocationRecommended; }
    public void setRelocationRecommended(Boolean relocationRecommended) { this.relocationRecommended = relocationRecommended; }

    public Double getRainfall24hMm() { return rainfall24hMm; }
    public void setRainfall24hMm(Double rainfall24hMm) { this.rainfall24hMm = rainfall24hMm; }

    public String getModelWeightsJson() { return modelWeightsJson; }
    public void setModelWeightsJson(String modelWeightsJson) { this.modelWeightsJson = modelWeightsJson; }

    public String getAssessmentRationale() { return assessmentRationale; }
    public void setAssessmentRationale(String assessmentRationale) { this.assessmentRationale = assessmentRationale; }

    public LocalDateTime getAssessedAt() { return assessedAt; }
    public void setAssessedAt(LocalDateTime assessedAt) { this.assessedAt = assessedAt; }
}

