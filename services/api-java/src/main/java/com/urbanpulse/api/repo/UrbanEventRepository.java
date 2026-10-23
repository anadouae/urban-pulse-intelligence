package com.urbanpulse.api.repo;

import com.urbanpulse.api.domain.UrbanEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrbanEventRepository extends JpaRepository<UrbanEvent, String> {
    long countBySpatialEntityId(String spatialEntityId);
}
