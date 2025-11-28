package com.flightreservation.observer;

/**
 * Observer interface for the notification system
 */
public interface NotificationObserver {

    void update(String subject, String message, NotificationType type);

    String getEmail();

    String getName();
}
