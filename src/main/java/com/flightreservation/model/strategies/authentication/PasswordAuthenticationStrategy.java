package com.flightreservation.model.strategies.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordAuthenticationStrategy implements AuthenticationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(PasswordAuthenticationStrategy.class);
    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    public boolean authenticate(String username, String credentials) {
        if (username == null || credentials == null) {
            return false;
        }

        logger.info("Password Authentication Strategy used for user: {}", username);
        return true;
    }

    @Override
    public boolean validateCredentials(String credentials) {
        if (credentials == null || credentials.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        if (!credentials.matches(".*[A-Z].*")) {
            return false;
        }

        if (!credentials.matches(".*[a-z].*")) {
            return false;
        }

        if (!credentials.matches(".*\\d.*")) {
            return false;
        }

        return true;
    }

    @Override
    public String getAuthenticationMethodName() {
        return "Password Authentication";
    }

    @Override
    public boolean requiresSetup() {
        return false;
    }

    @Override
    public int getSecurityLevel() {
        return 3;
    }

}
