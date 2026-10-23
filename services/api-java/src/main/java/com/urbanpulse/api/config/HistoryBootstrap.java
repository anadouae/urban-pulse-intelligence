package com.urbanpulse.api.config;

import com.urbanpulse.api.domain.SourceKind;
import com.urbanpulse.api.domain.SpatialEntity;
import com.urbanpulse.api.domain.TrafficAggregate;
import com.urbanpulse.api.repo.SpatialEntityRepository;
import com.urbanpulse.api.repo.TrafficAggregateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Seeds 24×5-min SIMULATED aggregate windows so forecast/anomaly demos work immediately.
 */
@Component
@Order(2)
public class HistoryBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(HistoryBootstrap.class);

    private final SpatialEntityRepository spatialEntityRepository;
    private final TrafficAggregateRepository aggregateRepository;

    public HistoryBootstrap(
            SpatialEntityRepository spatialEntityRepository,
            TrafficAggregateRepository aggregateRepository) {
        this.spatialEntityRepository = spatialEntityRepository;
        this.aggregateRepository = aggregateRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (aggregateRepository.count() >= 50) {
            return;
        }
        List<SpatialEntity> entities = spatialEntityRepository.findAll();
        Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        long epoch = now.getEpochSecond();
        Instant alignedNow = Instant.ofEpochSecond(epoch - (epoch % 300));

        int created = 0;
        for (SpatialEntity entity : entities) {
            for (int i = 24; i >= 1; i--) {
                Instant start = alignedNow.minus(i * 5L, ChronoUnit.MINUTES);
                if (aggregateRepository.findBySpatialEntityIdAndWindowStart(entity.getId(), start).isPresent()) {
                    continue;
                }
                double phase = (entity.getId().hashCode() + i) % 17;
                double rush = 0.45 + 0.45 * Math.sin((24 - i) / 3.0 + phase);
                double speed = 14 + 40 * (1.0 - rush * 0.65);
                double vehicles = 5 + rush * 30;
                aggregateRepository.save(new TrafficAggregate(
                        entity.getId(),
                        start,
                        start.plus(5, ChronoUnit.MINUTES),
                        speed,
                        vehicles,
                        4,
                        SourceKind.SIMULATED));
                created++;
            }
        }
        log.info("Seeded {} SIMULATED historical aggregate windows", created);
    }
}
