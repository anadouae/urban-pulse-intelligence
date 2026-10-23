package com.urbanpulse.api.web.dto;

import com.urbanpulse.api.domain.SourceKind;

import java.time.Instant;

public record TrafficSegmentView(
        String spatialEntityId,
        String name,
        double x,
        double z,
        double length,
        double headingDeg,
        double avgSpeedKmh,
        double vehicleCount,
        int sampleCount,
        Instant windowStart,
        Instant windowEnd,
        SourceKind sourceKind,
        String congestionLevel
) {
}
