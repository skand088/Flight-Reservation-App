package com.flightreservation.model.entities;


public class Flight {
    private int flightId;
    private String flightNumber;
    private java.time.LocalDateTime departureTime;
    private java.time.LocalDateTime arrivalTime;
    private int duration;
    private FlightStatus status;
    private double basePrice;
    private int availableSeats;

    private int aircraftId;
    private int routeId;
    private int airlineId;

    private Aircraft aircraft;
    private Route route;
    private Airline airline;

    public enum FlightStatus {
        SCHEDULED, BOARDING, DEPARTED, ARRIVED, DELAYED, CANCELLED
    }

    public Flight() {
    }

    public Flight(String flightNumber, java.time.LocalDateTime departureTime,
            java.time.LocalDateTime arrivalTime, double basePrice) {
        this.flightNumber = flightNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.basePrice = basePrice;
        this.status = FlightStatus.SCHEDULED;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public java.time.LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(java.time.LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public java.time.LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(java.time.LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(int aircraftId) {
        this.aircraftId = aircraftId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightNumber='" + flightNumber + '\'' +
                ", status=" + status +
                ", basePrice=" + basePrice +
                '}';
    }
}
