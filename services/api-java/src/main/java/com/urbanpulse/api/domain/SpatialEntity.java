package com.urbanpulse.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "spatial_entities")
public class SpatialEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String entityType;

    private double x;
    private double z;
    private double length;
    private double headingDeg;

    protected SpatialEntity() {
    }

    public SpatialEntity(String id, String name, String entityType, double x, double z, double length, double headingDeg) {
        this.id = id;
        this.name = name;
        this.entityType = entityType;
        this.x = x;
        this.z = z;
        this.length = length;
        this.headingDeg = headingDeg;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEntityType() {
        return entityType;
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    public double getLength() {
        return length;
    }

    public double getHeadingDeg() {
        return headingDeg;
    }
}
