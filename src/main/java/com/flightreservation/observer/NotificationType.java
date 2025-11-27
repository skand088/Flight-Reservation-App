package com.flightreservation.observer;

/**
 * Types of notifications that can be sent
 */
public enum NotificationType {
    NEWSLETTER("Newsletter"),
    PROMOTION("Promotion"),
    BOOKING_CONFIRMATION("Booking Confirmation"),
    FLIGHT_UPDATE("Flight Update"),
    CANCELLATION("Cancellation"),
    REMINDER("Reminder");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
