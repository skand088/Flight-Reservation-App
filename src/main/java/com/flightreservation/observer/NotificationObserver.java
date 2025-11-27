package com.flightreservation.observer;

/**
 * Observer interface for the notification system
 * Customers implement this to receive notifications
 */
public interface NotificationObserver {
    /**
     * Called when a notification is sent
     * 
     * @param subject The notification subject/title
     * @param message The notification message content
     * @param type    The type of notification
     */
    void update(String subject, String message, NotificationType type);

    /**
     * Get the email address of the observer
     * 
     * @return email address
     */
    String getEmail();

    /**
     * Get the name of the observer
     * 
     * @return name
     */
    String getName();
}
