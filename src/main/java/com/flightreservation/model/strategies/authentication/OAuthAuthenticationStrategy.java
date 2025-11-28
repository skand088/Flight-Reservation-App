package com.flightreservation.model.strategies.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthAuthenticationStrategy implements AuthenticationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthenticationStrategy.class);

    private final String provider;

    public OAuthAuthenticationStrategy(String provider) {
        this.provider = provider;
    }

    @Override
    public boolean authenticate(String username, String credentials) {
        if (username == null || credentials == null) {
            return false;
        }

        logger.info("OAuth Authentication Strategy ({}) used for user: {}", provider, username);
        return true;
    }

    @Override
    public boolean validateCredentials(String credentials) {
        if (credentials == null || credentials.length() < 32) {
            return false;
        }

        if (!credentials.matches("[A-Za-z0-9._-]+")) {
            logger.warn("Invalid OAuth token format");
            return false;
        }

        return true;
    }

    @Override
    public String getAuthenticationMethodName() {
        return "OAuth Authentication (" + provider + ")";
    }

    @Override
    public boolean requiresSetup() {
        return true;
    }

    @Override
    public int getSecurityLevel() {
        return 4;
    }

    public String getProvider() {
        return provider;
    }
}
