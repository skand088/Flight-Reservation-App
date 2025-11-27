package com.flightreservation.model;

/**
 * Customer entity extending user information
 */
public class Customer {
    private int customerId;
    private int userId;
    private String frequentFlyerNumber;
    private int loyaltyPoints;
    private String preferredAirline;
    private String address;

    // Associated user object
    private User user;

    // Constructors
    public Customer() {
    }

    public Customer(int userId, String address) {
        this.userId = userId;
        this.address = address;
    }

    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFrequentFlyerNumber() {
        return frequentFlyerNumber;
    }

    public void setFrequentFlyerNumber(String frequentFlyerNumber) {
        this.frequentFlyerNumber = frequentFlyerNumber;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getPreferredAirline() {
        return preferredAirline;
    }

    public void setPreferredAirline(String preferredAirline) {
        this.preferredAirline = preferredAirline;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return user != null ? user.getFullName() : "Customer#" + customerId;
    }
}
