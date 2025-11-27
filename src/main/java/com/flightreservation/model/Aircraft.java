package com.flightreservation.model;

/**
 * Aircraft entity representing aircraft in the fleet
 */
public class Aircraft {
    private int aircraftId;
    private String tailNumber;
    private String model;
    private String manufacturer;
    private int totalSeats;
    private String seatConfiguration;

    // Constructors
    public Aircraft() {
    }

    public Aircraft(String tailNumber, String model, String manufacturer, int totalSeats) {
        this.tailNumber = tailNumber;
        this.model = model;
        this.manufacturer = manufacturer;
        this.totalSeats = totalSeats;
    }

    // Getters and Setters
    public int getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(int aircraftId) {
        this.aircraftId = aircraftId;
    }

    public String getTailNumber() {
        return tailNumber;
    }

    public void setTailNumber(String tailNumber) {
        this.tailNumber = tailNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getSeatConfiguration() {
        return seatConfiguration;
    }

    public void setSeatConfiguration(String seatConfiguration) {
        this.seatConfiguration = seatConfiguration;
    }

    @Override
    public String toString() {
        return manufacturer + " " + model + " (" + tailNumber + ")";
    }
}
