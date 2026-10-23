package com.urbanpulse.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "traffic_aggregates", indexes = {
        @Index(name = "idx_agg_spatial_window", columnList = "spatialEntityId,windowStart")
})
public class TrafficAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String spatialEntityId;

    @Column(nullable = false)
    private Instant windowStart;

    @Column(nullable = false)
    private Instant windowEnd;

    private double avgSpeedKmh;
    private double vehicleCount;
    private int sampleCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceKind sourceKind;

    protected TrafficAggregate() {
    }

    public TrafficAggregate(
            String spatialEntityId,
            Instant windowStart,
            Instant windowEnd,
            double avgSpeedKmh,
            double vehicleCount,
            int sampleCount,
            SourceKind sourceKind) {
        this.spatialEntityId = spatialEntityId;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.avgSpeedKmh = avgSpeedKmh;
        this.vehicleCount = vehicleCount;
        this.sampleCount = sampleCount;
        this.sourceKind = sourceKind;
    }

    public void mergeSample(double speedKmh, double vehicles) {
        double totalSpeed = this.avgSpeedKmh * this.sampleCount + speedKmh;
        this.sampleCount += 1;
        this.avgSpeedKmh = totalSpeed / this.sampleCount;
        this.vehicleCount += vehicles;
    }

    public Long getId() {
        return id;
    }

    public String getSpatialEntityId() {
        return spatialEntityId;
    }

    public Instant getWindowStart() {
        return windowStart;
    }

    public Instant getWindowEnd() {
        return windowEnd;
    }

    public double getAvgSpeedKmh() {
        return avgSpeedKmh;
    }

    public double getVehicleCount() {
        return vehicleCount;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public SourceKind getSourceKind() {
        return sourceKind;
    }
}
