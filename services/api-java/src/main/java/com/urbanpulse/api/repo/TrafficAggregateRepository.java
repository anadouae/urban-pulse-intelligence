package com.urbanpulse.api.repo;

import com.urbanpulse.api.domain.TrafficAggregate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TrafficAggregateRepository extends JpaRepository<TrafficAggregate, Long> {
    Optional<TrafficAggregate> findBySpatialEntityIdAndWindowStart(String spatialEntityId, Instant windowStart);

    List<TrafficAggregate> findByOrderByWindowStartDesc();

    List<TrafficAggregate> findTop200ByOrderByWindowStartDesc();
}
