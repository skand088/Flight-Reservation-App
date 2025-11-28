package com.flightreservation.observer;

/**
 * types of notifications that can be sent
 */
public enum NotificationType {
    NEWSLETTER("Newsletter"); // only one used in our current UI implementation, extendable for future

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
