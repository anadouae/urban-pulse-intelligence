package com.urbanpulse.api.web.dto;

import com.urbanpulse.api.domain.SourceKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UrbanEventRequest(
        @NotBlank String eventId,
        @NotBlank String eventType,
        @NotNull Instant eventTimestamp,
        @NotBlank String sourceId,
        @NotBlank String spatialEntityId,
        @NotBlank String schemaVersion,
        @NotBlank String measurementName,
        @NotNull Double measurementValue,
        @NotBlank String measurementUnit,
        @NotBlank String qualityStatus,
        @NotNull SourceKind sourceKind
) {
}
