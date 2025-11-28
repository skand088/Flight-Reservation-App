package com.flightreservation.model.strategies.authentication;

public interface AuthenticationStrategy {

    /**
     * authenticate user with the specific strategy - right now only password is supported by UI
     * can be extended for other ways to login in the future
     * @return true if authentication successful, false otherwise
     */
    boolean authenticate(String username, String credentials);

    /**
     * validate the format/strength of credentials
     * 
     * @param credentials 
     * @return true if credentials are valid format, false otherwise
     */
    boolean validateCredentials(String credentials);

    /**
     * get name of auth method
     * 
     * @return 
     */
    String getAuthenticationMethodName();

    /**
     * check if authentication method requires additional things
     * 
     * @return true if more is required (e.g., 2FA enrollment)
     */
    boolean requiresSetup();

    /**
     * get security level of this authentication method (1-5, 5 being most secure)
     * 
     * @return 
     */
    int getSecurityLevel();
}
