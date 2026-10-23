package com.urbanpulse.api.service;

import com.urbanpulse.api.domain.TrafficAggregate;
import com.urbanpulse.api.domain.UrbanEvent;
import com.urbanpulse.api.repo.TrafficAggregateRepository;
import com.urbanpulse.api.repo.UrbanEventRepository;
import com.urbanpulse.api.web.dto.UrbanEventRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class EventIngestionService {

    private static final String SUPPORTED_SCHEMA = "1.0.0";

    private final UrbanEventRepository eventRepository;
    private final TrafficAggregateRepository aggregateRepository;

    public EventIngestionService(
            UrbanEventRepository eventRepository,
            TrafficAggregateRepository aggregateRepository) {
        this.eventRepository = eventRepository;
        this.aggregateRepository = aggregateRepository;
    }

    @Transactional
    public UrbanEvent ingest(UrbanEventRequest request) {
        if (!SUPPORTED_SCHEMA.equals(request.schemaVersion())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported schema_version. Expected " + SUPPORTED_SCHEMA);
        }
        if (eventRepository.existsById(request.eventId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate event_id");
        }

        UrbanEvent event = new UrbanEvent(
                request.eventId(),
                request.eventType(),
                request.eventTimestamp(),
                Instant.now(),
                request.sourceId(),
                request.spatialEntityId(),
                request.schemaVersion(),
                request.measurementName(),
                request.measurementValue(),
                request.measurementUnit(),
                request.qualityStatus(),
                request.sourceKind());
        eventRepository.save(event);
        applyAggregation(event);
        return event;
    }

    private void applyAggregation(UrbanEvent event) {
        long epoch = event.getEventTimestamp().getEpochSecond();
        Instant windowStart = Instant.ofEpochSecond(epoch - (epoch % 300));
        Instant windowEnd = windowStart.plus(5, ChronoUnit.MINUTES);

        TrafficAggregate aggregate = aggregateRepository
                .findBySpatialEntityIdAndWindowStart(event.getSpatialEntityId(), windowStart)
                .orElseGet(() -> new TrafficAggregate(
                        event.getSpatialEntityId(),
                        windowStart,
                        windowEnd,
                        0,
                        0,
                        0,
                        event.getSourceKind()));

        double speed = "speed_kmh".equals(event.getMeasurementName()) ? event.getMeasurementValue() : aggregate.getAvgSpeedKmh();
        double vehicles = "vehicle_count".equals(event.getMeasurementName()) ? event.getMeasurementValue() : 0;
        aggregate.mergeSample(speed, vehicles);
        aggregateRepository.save(aggregate);
    }
}
