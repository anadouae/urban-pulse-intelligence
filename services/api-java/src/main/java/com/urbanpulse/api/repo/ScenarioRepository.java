package com.urbanpulse.api.repo;

import com.urbanpulse.api.domain.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    List<Scenario> findAllByOrderByCreatedAtDesc();
}
