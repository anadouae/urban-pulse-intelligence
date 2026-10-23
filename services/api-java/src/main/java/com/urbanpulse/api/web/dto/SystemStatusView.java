package com.urbanpulse.api.web.dto;

import java.time.Instant;

public record SystemStatusView(
        String cityId,
        String cityLabel,
        String milestone,
        long eventCount,
        long aggregateCount,
        long spatialEntityCount,
        Instant serverTime,
        String dataMode,
        String notes
) {
}
