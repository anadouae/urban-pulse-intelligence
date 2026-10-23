package com.urbanpulse.api.service;

import com.urbanpulse.api.domain.Scenario;
import com.urbanpulse.api.domain.TrafficAggregate;
import com.urbanpulse.api.repo.ScenarioRepository;
import com.urbanpulse.api.repo.TrafficAggregateRepository;
import com.urbanpulse.api.web.dto.ScenarioRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Transparent, simplified scenario simulator.
 * Not a validated traffic-engineering model — assumptions are stored with each run.
 */
@Service
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final TrafficAggregateRepository aggregateRepository;

    public ScenarioService(ScenarioRepository scenarioRepository, TrafficAggregateRepository aggregateRepository) {
        this.scenarioRepository = scenarioRepository;
        this.aggregateRepository = aggregateRepository;
    }

    @Transactional(readOnly = true)
    public List<Scenario> list() {
        return scenarioRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Scenario create(ScenarioRequest request) {
        double baselineSpeed = aggregateRepository.findTop200ByOrderByWindowStartDesc().stream()
                .mapToDouble(TrafficAggregate::getAvgSpeedKmh)
                .filter(v -> v > 0)
                .average()
                .orElse(35.0);

        // Toy kinematics: more demand / less capacity / incidents reduce speed.
        double load = request.demandMultiplier() / Math.max(0.2, request.capacityMultiplier());
        double incidentPenalty = Math.min(25.0, request.incidentMinutes() * 0.15);
        double predictedSpeed = Math.max(5.0, baselineSpeed / load - incidentPenalty);
        double congestionIndex = Math.min(100.0, (1.0 - predictedSpeed / Math.max(baselineSpeed, 1.0)) * 100.0);

        String notes = "Simplified what-if model on Harbor District aggregates. "
                + "Baseline avg speed=" + round(baselineSpeed) + " km/h. "
                + "Not a calibrated traffic assignment model.";

        Scenario scenario = new Scenario(
                request.name(),
                request.assumptions(),
                request.demandMultiplier(),
                request.capacityMultiplier(),
                request.incidentMinutes(),
                round(predictedSpeed),
                round(congestionIndex),
                notes);
        return scenarioRepository.save(scenario);
    }

    private static double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
