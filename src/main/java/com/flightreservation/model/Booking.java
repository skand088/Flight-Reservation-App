package com.flightreservation.model;

import java.sql.Timestamp;

/**
 * Represents a booking in the reservation system.
 */
public class Booking {
    private int id;
    private int userId;
    private int flightId;
    private Timestamp bookingDate;
    private int numberOfSeats;
    private String status;

    public Booking() {
    }

    public Booking(int id, int userId, int flightId, Timestamp bookingDate, 
                   int numberOfSeats, String status) {
        this.id = id;
        this.userId = userId;
        this.flightId = flightId;
        this.bookingDate = bookingDate;
        this.numberOfSeats = numberOfSeats;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public Timestamp getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Timestamp bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking #" + id + " - " + numberOfSeats + " seat(s) - " + status;
    }
}
