package com.example.model;

import java.util.UUID;

public class Waypoint {
    private String id;
    private double latitude;
    private double longitude;
    private String name;

    public Waypoint() {
        this.id = UUID.randomUUID().toString();
    }

    public Waypoint(double latitude, double longitude, String name) {
        this();
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    // Getter und Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                '}';
    }
}