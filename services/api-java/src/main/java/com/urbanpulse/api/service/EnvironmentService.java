package com.urbanpulse.api.service;

import com.urbanpulse.api.domain.SourceKind;
import com.urbanpulse.api.domain.TrafficAggregate;
import com.urbanpulse.api.repo.TrafficAggregateRepository;
import com.urbanpulse.api.web.dto.EnvironmentReading;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Derives synthetic air-quality indicators correlated with traffic load.
 * Explicitly SIMULATED — not sensor telemetry.
 */
@Service
public class EnvironmentService {

    private final TrafficAggregateRepository aggregateRepository;

    public EnvironmentService(TrafficAggregateRepository aggregateRepository) {
        this.aggregateRepository = aggregateRepository;
    }

    @Transactional(readOnly = true)
    public List<EnvironmentReading> current() {
        double avgVehicles = aggregateRepository.findTop200ByOrderByWindowStartDesc().stream()
                .mapToDouble(TrafficAggregate::getVehicleCount)
                .average()
                .orElse(10.0);

        List<EnvironmentReading> readings = new ArrayList<>();
        readings.add(zone("zone-harbor", "Harbor Core", avgVehicles, 1.0));
        readings.add(zone("zone-north", "North Quarters", avgVehicles, 0.75));
        readings.add(zone("zone-south", "South Quarters", avgVehicles, 0.85));
        readings.add(zone("zone-east", "East Industrial", avgVehicles, 1.25));
        return readings;
    }

    private EnvironmentReading zone(String id, String name, double vehicles, double factor) {
        double pm25 = round(8 + vehicles * 0.35 * factor);
        double no2 = round(12 + vehicles * 0.55 * factor);
        String aqi;
        if (pm25 < 12) {
            aqi = "good";
        } else if (pm25 < 35) {
            aqi = "moderate";
        } else if (pm25 < 55) {
            aqi = "unhealthy_sensitive";
        } else {
            aqi = "unhealthy";
        }
        return new EnvironmentReading(
                id,
                name,
                pm25,
                no2,
                aqi,
                SourceKind.SIMULATED,
                "Derived from simulated traffic load — not real AQ sensors.");
    }

    private static double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
