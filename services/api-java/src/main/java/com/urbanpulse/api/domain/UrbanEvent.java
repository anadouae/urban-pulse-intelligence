package com.urbanpulse.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "urban_events", indexes = {
        @Index(name = "idx_events_spatial_ts", columnList = "spatialEntityId,eventTimestamp")
})
public class UrbanEvent {

    @Id
    private String eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private Instant eventTimestamp;

    @Column(nullable = false)
    private Instant ingestionTimestamp;

    @Column(nullable = false)
    private String sourceId;

    @Column(nullable = false)
    private String spatialEntityId;

    @Column(nullable = false)
    private String schemaVersion;

    @Column(nullable = false)
    private String measurementName;

    @Column(nullable = false)
    private double measurementValue;

    @Column(nullable = false)
    private String measurementUnit;

    @Column(nullable = false)
    private String qualityStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceKind sourceKind;

    protected UrbanEvent() {
    }

    public UrbanEvent(
            String eventId,
            String eventType,
            Instant eventTimestamp,
            Instant ingestionTimestamp,
            String sourceId,
            String spatialEntityId,
            String schemaVersion,
            String measurementName,
            double measurementValue,
            String measurementUnit,
            String qualityStatus,
            SourceKind sourceKind) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventTimestamp = eventTimestamp;
        this.ingestionTimestamp = ingestionTimestamp;
        this.sourceId = sourceId;
        this.spatialEntityId = spatialEntityId;
        this.schemaVersion = schemaVersion;
        this.measurementName = measurementName;
        this.measurementValue = measurementValue;
        this.measurementUnit = measurementUnit;
        this.qualityStatus = qualityStatus;
        this.sourceKind = sourceKind;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    public Instant getIngestionTimestamp() {
        return ingestionTimestamp;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getSpatialEntityId() {
        return spatialEntityId;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public double getMeasurementValue() {
        return measurementValue;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public String getQualityStatus() {
        return qualityStatus;
    }

    public SourceKind getSourceKind() {
        return sourceKind;
    }
}
