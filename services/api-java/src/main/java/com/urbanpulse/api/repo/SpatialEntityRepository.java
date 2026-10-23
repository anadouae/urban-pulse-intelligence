package com.urbanpulse.api.repo;

import com.urbanpulse.api.domain.SpatialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpatialEntityRepository extends JpaRepository<SpatialEntity, String> {
}
