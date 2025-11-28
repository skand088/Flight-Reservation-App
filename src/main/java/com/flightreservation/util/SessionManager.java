package com.flightreservation.util;

import com.flightreservation.model.entities.User;

// singleton for session
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private String sessionId;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void startSession(User user, String sessionId) {
        this.currentUser = user;
        this.sessionId = sessionId;
    }

    public void endSession() {
        this.currentUser = null;
        this.sessionId = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User.UserRole getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
}
