package com.urbanpulse.api.service;

import com.urbanpulse.api.domain.SpatialEntity;
import com.urbanpulse.api.domain.TrafficAggregate;
import com.urbanpulse.api.repo.SpatialEntityRepository;
import com.urbanpulse.api.repo.TrafficAggregateRepository;
import com.urbanpulse.api.repo.UrbanEventRepository;
import com.urbanpulse.api.web.dto.SystemStatusView;
import com.urbanpulse.api.web.dto.TrafficSegmentView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final SpatialEntityRepository spatialEntityRepository;
    private final TrafficAggregateRepository aggregateRepository;
    private final UrbanEventRepository eventRepository;

    @Value("${urbanpulse.city-id}")
    private String cityId;

    @Value("${urbanpulse.city-label}")
    private String cityLabel;

    public AnalyticsService(
            SpatialEntityRepository spatialEntityRepository,
            TrafficAggregateRepository aggregateRepository,
            UrbanEventRepository eventRepository) {
        this.spatialEntityRepository = spatialEntityRepository;
        this.aggregateRepository = aggregateRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<SpatialEntity> listEntities() {
        return spatialEntityRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TrafficSegmentView> latestTrafficBySegment() {
        Map<String, SpatialEntity> entities = new HashMap<>();
        for (SpatialEntity entity : spatialEntityRepository.findAll()) {
            entities.put(entity.getId(), entity);
        }

        Map<String, TrafficAggregate> latest = new HashMap<>();
        for (TrafficAggregate aggregate : aggregateRepository.findTop200ByOrderByWindowStartDesc()) {
            latest.putIfAbsent(aggregate.getSpatialEntityId(), aggregate);
        }

        return entities.values().stream()
                .sorted(Comparator.comparing(SpatialEntity::getId))
                .map(entity -> {
                    TrafficAggregate agg = latest.get(entity.getId());
                    if (agg == null) {
                        return new TrafficSegmentView(
                                entity.getId(),
                                entity.getName(),
                                entity.getX(),
                                entity.getZ(),
                                entity.getLength(),
                                entity.getHeadingDeg(),
                                0,
                                0,
                                0,
                                null,
                                null,
                                null,
                                "unknown");
                    }
                    return new TrafficSegmentView(
                            entity.getId(),
                            entity.getName(),
                            entity.getX(),
                            entity.getZ(),
                            entity.getLength(),
                            entity.getHeadingDeg(),
                            round(agg.getAvgSpeedKmh()),
                            round(agg.getVehicleCount()),
                            agg.getSampleCount(),
                            agg.getWindowStart(),
                            agg.getWindowEnd(),
                            agg.getSourceKind(),
                            congestionLevel(agg.getAvgSpeedKmh()));
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public SystemStatusView status() {
        return new SystemStatusView(
                cityId,
                cityLabel,
                "M1",
                eventRepository.count(),
                aggregateRepository.count(),
                spatialEntityRepository.count(),
                Instant.now(),
                "SIMULATED_DEMO",
                "M1 uses synthetic events + in-process aggregation (H2). Kafka/PostGIS planned for M2.");
    }

    private static String congestionLevel(double avgSpeedKmh) {
        if (avgSpeedKmh <= 0) {
            return "unknown";
        }
        if (avgSpeedKmh < 15) {
            return "severe";
        }
        if (avgSpeedKmh < 30) {
            return "heavy";
        }
        if (avgSpeedKmh < 45) {
            return "moderate";
        }
        return "free";
    }

    private static double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
