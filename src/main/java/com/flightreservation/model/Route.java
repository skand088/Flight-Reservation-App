package com.flightreservation.model;

/**
 * Route entity representing flight routes between airports
 */
public class Route {
    private int routeId;
    private String originAirport;
    private String destinationAirport;
    private int distance;
    private int estimatedDuration;

    // Constructors
    public Route() {
    }

    public Route(String originAirport, String destinationAirport, int distance, int estimatedDuration) {
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.distance = distance;
        this.estimatedDuration = estimatedDuration;
    }

    // Getters and Setters
    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getOriginAirport() {
        return originAirport;
    }

    public void setOriginAirport(String originAirport) {
        this.originAirport = originAirport;
    }

    public String getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    @Override
    public String toString() {
        return originAirport + " → " + destinationAirport;
    }
}
