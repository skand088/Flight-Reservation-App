package com.flightreservation.model.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flightreservation.patterns.Observer;

/**
 * customer extending user
 * 
 * - implements Observer to receive notifications and newsletter
 * - created with factory pattern
 * - login and payment done by customer with strategy pattern
 */
public class Customer implements Observer {
    private static final Logger logger = LoggerFactory.getLogger(Customer.class);
    private int customerId;
    private int userId;
    private String frequentFlyerNumber;
    private int loyaltyPoints;
    private String preferredAirline;
    private String address;

    private User user;

    public Customer() {
    }

    public Customer(int userId, String address) {
        this.userId = userId;
        this.address = address;
    }

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

    public void update(String message) {
        logger.info("Customer {} received notification: {}",
                user != null ? user.getUsername() : customerId, message);
    }

    public void onFlightStatusChange(String flightNumber, String oldStatus, String newStatus) {
        String message = String.format("Flight %s status changed from %s to %s",
                flightNumber, oldStatus, newStatus);
        logger.info("Customer {} notified: {}",
                user != null ? user.getUsername() : customerId, message);
    }

    public void onFlightDelay(String flightNumber, int delayMinutes) {
        String message = String.format("Flight %s is delayed by %d minutes",
                flightNumber, delayMinutes);
        logger.warn("Customer {} notified: {}",
                user != null ? user.getUsername() : customerId, message);
    }

    @Override
    public void onPromotion(String promotion) {
        logger.info("Customer {} received promotion: {}",
                user != null ? user.getUsername() : customerId, promotion);
    }

    @Override
    public String toString() {
        return user != null ? user.getFullName() : "Customer#" + customerId;
    }
}
