package com.urbanpulse.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "scenarios")
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2000)
    private String assumptions;

    private double demandMultiplier = 1.0;
    private double capacityMultiplier = 1.0;
    private double incidentMinutes = 0;

    private double predictedAvgSpeedKmh;
    private double predictedCongestionIndex;
    private String evaluationNotes;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Scenario() {
    }

    public Scenario(
            String name,
            String assumptions,
            double demandMultiplier,
            double capacityMultiplier,
            double incidentMinutes,
            double predictedAvgSpeedKmh,
            double predictedCongestionIndex,
            String evaluationNotes) {
        this.name = name;
        this.assumptions = assumptions;
        this.demandMultiplier = demandMultiplier;
        this.capacityMultiplier = capacityMultiplier;
        this.incidentMinutes = incidentMinutes;
        this.predictedAvgSpeedKmh = predictedAvgSpeedKmh;
        this.predictedCongestionIndex = predictedCongestionIndex;
        this.evaluationNotes = evaluationNotes;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAssumptions() {
        return assumptions;
    }

    public double getDemandMultiplier() {
        return demandMultiplier;
    }

    public double getCapacityMultiplier() {
        return capacityMultiplier;
    }

    public double getIncidentMinutes() {
        return incidentMinutes;
    }

    public double getPredictedAvgSpeedKmh() {
        return predictedAvgSpeedKmh;
    }

    public double getPredictedCongestionIndex() {
        return predictedCongestionIndex;
    }

    public String getEvaluationNotes() {
        return evaluationNotes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
