package com.flightreservation.model;

/**
 * Airline entity
 */
public class Airline {
    private int airlineId;
    private String airlineName;
    private String airlineCode;
    private String contactInfo;

    // Constructors
    public Airline() {
    }

    public Airline(String airlineName, String airlineCode) {
        this.airlineName = airlineName;
        this.airlineCode = airlineCode;
    }

    // Getters and Setters
    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public String toString() {
        return airlineName + " (" + airlineCode + ")";
    }
}
