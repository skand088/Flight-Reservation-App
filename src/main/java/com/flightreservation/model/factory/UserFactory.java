package com.flightreservation.model.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flightreservation.model.entities.Customer;
import com.flightreservation.model.entities.User;

/**
 * factory pattern for creating diff users
 * 
 * user type passed in and factory creates accordingly
 */
public class UserFactory {

    private static final Logger logger = LoggerFactory.getLogger(UserFactory.class);

    public static final String USER_TYPE_CUSTOMER = "CUSTOMER";
    public static final String USER_TYPE_FLIGHT_AGENT = "FLIGHT_AGENT";
    public static final String USER_TYPE_SYSTEM_ADMIN = "SYSTEM_ADMIN";

    /**
     * create user based on the specified type
     * 
     * @param userType     type of user (CUSTOMER, FLIGHT_AGENT,
     *                     SYSTEM_ADMIN)
     * @param username
     * @param passwordHash not actually a hash but in real life it should be
     * @param email
     * @param phoneNumber
     * @return
     * @throws IllegalArgumentException if user type is invalid
     */
    public static User createUser(String userType, String username, String passwordHash,
            String email, String phoneNumber) {
        if (userType == null || userType.trim().isEmpty()) {
            logger.error("User type cannot be null or empty");
            throw new IllegalArgumentException("User type is required");
        }

        logger.info("Creating user of type: {}", userType);

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setAccountStatus(User.AccountStatus.ACTIVE);

        User.UserRole role;

        switch (userType.toUpperCase()) {
            case USER_TYPE_CUSTOMER:
                role = User.UserRole.CUSTOMER;
                logger.debug("Created Customer user: {}", username);
                break;

            case USER_TYPE_FLIGHT_AGENT:
                role = User.UserRole.AGENT;
                logger.debug("Created Flight Agent user: {}", username);
                break;

            case USER_TYPE_SYSTEM_ADMIN:
                role = User.UserRole.ADMIN;
                logger.debug("Created System Admin user: {}", username);
                break;

            default:
                logger.error("Invalid user type: {}", userType);
                throw new IllegalArgumentException("Invalid user type: " + userType);
        }

        user.setRole(role);
        logger.info("Successfully created user: {} ({})", username, userType);
        return user;
    }

    public static User createUser(String userType, String username, String email) {
        return createUser(userType, username, "", email, null);
    }

    public static Customer createCustomerWithEntity(String username, String passwordHash,
            String email, String phoneNumber) {
        User user = createUser(USER_TYPE_CUSTOMER, username, passwordHash, email, phoneNumber);
        Customer customer = new Customer();
        customer.setUser(user);
        logger.debug("Created Customer entity for user: {}", username);
        return customer;
    }

    public static boolean isValidUserType(String userType) {
        if (userType == null) {
            return false;
        }

        String type = userType.toUpperCase();
        return type.equals(USER_TYPE_CUSTOMER) ||
                type.equals(USER_TYPE_FLIGHT_AGENT) ||
                type.equals(USER_TYPE_SYSTEM_ADMIN);
    }

    public static String[] getSupportedUserTypes() {
        return new String[] {
                USER_TYPE_CUSTOMER,
                USER_TYPE_FLIGHT_AGENT,
                USER_TYPE_SYSTEM_ADMIN
        };
    }
}
