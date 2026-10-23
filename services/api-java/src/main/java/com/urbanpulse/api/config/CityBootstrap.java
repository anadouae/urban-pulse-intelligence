package com.urbanpulse.api.config;

import com.urbanpulse.api.domain.SpatialEntity;
import com.urbanpulse.api.repo.SpatialEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Seeds a small procedural Harbor District road grid for the M1 demo.
 * Geometry is synthetic and labeled as such in the API status payload.
 */
@Component
@Order(1)
public class CityBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CityBootstrap.class);

    private final SpatialEntityRepository spatialEntityRepository;

    public CityBootstrap(SpatialEntityRepository spatialEntityRepository) {
        this.spatialEntityRepository = spatialEntityRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (spatialEntityRepository.count() > 0) {
            return;
        }

        // Axis-aligned grid: 3 E-W avenues + 3 N-S streets
        spatialEntityRepository.save(new SpatialEntity("seg-ave-north", "North Avenue", "road_segment", 0, -12, 36, 0));
        spatialEntityRepository.save(new SpatialEntity("seg-ave-mid", "Harbor Avenue", "road_segment", 0, 0, 36, 0));
        spatialEntityRepository.save(new SpatialEntity("seg-ave-south", "South Avenue", "road_segment", 0, 12, 36, 0));
        spatialEntityRepository.save(new SpatialEntity("seg-st-west", "West Street", "road_segment", -12, 0, 28, 90));
        spatialEntityRepository.save(new SpatialEntity("seg-st-center", "Center Street", "road_segment", 0, 0, 28, 90));
        spatialEntityRepository.save(new SpatialEntity("seg-st-east", "East Street", "road_segment", 12, 0, 28, 90));
        spatialEntityRepository.save(new SpatialEntity("seg-ring-ne", "NE Connector", "road_segment", 8, -8, 14, 45));
        spatialEntityRepository.save(new SpatialEntity("seg-ring-sw", "SW Connector", "road_segment", -8, 8, 14, 45));

        log.info("Seeded {} procedural spatial entities (demo city)", spatialEntityRepository.count());
    }
}
