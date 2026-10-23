package com.urbanpulse.api.web.dto;

import com.urbanpulse.api.domain.SourceKind;

import java.time.Instant;

public record HistoryPoint(
        Instant windowStart,
        double avgSpeedKmh,
        double vehicleCount,
        SourceKind sourceKind
) {
}
