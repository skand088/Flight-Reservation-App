package com.flightreservation.model.strategies.authentication;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwoFactorAuthenticationStrategy implements AuthenticationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthenticationStrategy.class);
    private final PasswordAuthenticationStrategy passwordStrategy;

    public TwoFactorAuthenticationStrategy() {
        this.passwordStrategy = new PasswordAuthenticationStrategy();
    }

    @Override
    public boolean authenticate(String username, String credentials) {
        if (username == null || credentials == null) {
            return false;
        }

        logger.info("Two-Factor Authentication Strategy used for user: {}", username);
        return true;
    }

    @Override
    public boolean validateCredentials(String credentials) {
        if (credentials == null || !credentials.contains(":")) {
            return false;
        }

        String[] parts = credentials.split(":");
        if (parts.length != 2) {
            return false;
        }

        String password = parts[0];
        String code = parts[1];

        if (!passwordStrategy.validateCredentials(password)) {
            return false;
        }

        if (!code.matches("\\d{6}")) {
            return false;
        }

        return true;
    }

    @Override
    public String getAuthenticationMethodName() {
        return "Two-Factor Authentication (2FA)";
    }

    @Override
    public boolean requiresSetup() {
        return true;
    }

    @Override
    public int getSecurityLevel() {
        return 5;
    }

    public static String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
