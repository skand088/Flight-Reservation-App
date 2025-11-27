package com.flightreservation.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reservation entity representing flight bookings
 */
public class Reservation {
    private int reservationId;
    private String confirmationNumber;
    private LocalDateTime reservationDate;
    private ReservationStatus status;
    private double totalFare;
    private int customerId;
    private int flightId;

    // Associated objects
    private Flight flight;
    private Customer customer;
    private List<Passenger> passengers;

    public enum ReservationStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }

    // Constructors
    public Reservation() {
        this.passengers = new ArrayList<>();
        this.reservationDate = LocalDateTime.now();
    }

    public Reservation(String confirmationNumber, int customerId, int flightId, double totalFare) {
        this();
        this.confirmationNumber = confirmationNumber;
        this.customerId = customerId;
        this.flightId = flightId;
        this.totalFare = totalFare;
        this.status = ReservationStatus.PENDING;
    }

    // Getters and Setters
    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(double totalFare) {
        this.totalFare = totalFare;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public void addPassenger(Passenger passenger) {
        this.passengers.add(passenger);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "confirmationNumber='" + confirmationNumber + '\'' +
                ", status=" + status +
                ", totalFare=" + totalFare +
                '}';
    }
}
