package com.flightreservation.model;

/**
 * Customer entity extending user information
 */
public class Customer {
    private int customerId;
    private int userId;
    private String phoneNumber;
    private String address;
    private String preferredPaymentMethod;

    // Associated user object
    private User user;

    // Constructors
    public Customer() {
    }

    public Customer(int userId, String phoneNumber, String address) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPreferredPaymentMethod() {
        return preferredPaymentMethod;
    }

    public void setPreferredPaymentMethod(String preferredPaymentMethod) {
        this.preferredPaymentMethod = preferredPaymentMethod;
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
