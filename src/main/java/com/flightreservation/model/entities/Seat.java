package com.flightreservation.model.entities;


public class Seat {
    private int seatId;
    private String seatNumber;
    private SeatClass seatClass;
    private SeatType seatType;
    private double price;
    private SeatStatus status;
    private int flightId;

    public enum SeatClass {
        ECONOMY, BUSINESS, FIRST
    }

    public enum SeatType {
        WINDOW, MIDDLE, AISLE
    }

    public enum SeatStatus {
        AVAILABLE, RESERVED, OCCUPIED, BLOCKED
    }

    public Seat() {
    }

    public Seat(String seatNumber, SeatClass seatClass, SeatType seatType, double price, int flightId) {
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.seatType = seatType;
        this.price = price;
        this.flightId = flightId;
        this.status = SeatStatus.AVAILABLE;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(SeatClass seatClass) {
        this.seatClass = seatClass;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return seatNumber + " (" + seatClass + ", " + seatType + ")";
    }
}
