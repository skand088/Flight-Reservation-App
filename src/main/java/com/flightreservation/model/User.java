package com.flightreservation.model;

import java.time.LocalDateTime;

/**
 * User entity representing all system users
 */
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private AccountStatus accountStatus;
    private LocalDateTime createdDate;
    private LocalDateTime lastLoginDate;

    public enum UserRole {
        CUSTOMER, AGENT, ADMIN
    }

    public enum AccountStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }

    // Constructors
    public User() {
    }

    public User(int userId, String username, String email, UserRole role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.accountStatus = AccountStatus.ACTIVE;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getFullName() {
        return username;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", accountStatus=" + accountStatus +
                '}';
    }
}
