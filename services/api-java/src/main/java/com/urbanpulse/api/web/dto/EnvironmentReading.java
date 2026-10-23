package com.urbanpulse.api.web.dto;

import com.urbanpulse.api.domain.SourceKind;

public record EnvironmentReading(
        String zoneId,
        String zoneName,
        double pm25,
        double no2,
        String airQualityIndex,
        SourceKind sourceKind,
        String notes
) {
}
