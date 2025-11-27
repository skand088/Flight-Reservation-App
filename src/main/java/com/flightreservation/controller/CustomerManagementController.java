package com.flightreservation.controller;

import com.flightreservation.dao.CustomerDAO;
import com.flightreservation.dao.UserDAO;
import com.flightreservation.model.Customer;
import com.flightreservation.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller for customer management operations (used by agents)
 */
public class CustomerManagementController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerManagementController.class);
    private final CustomerDAO customerDAO;
    private final UserDAO userDAO;

    public CustomerManagementController() {
        this.customerDAO = new CustomerDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        logger.info("Retrieving all customers");
        List<Customer> customers = customerDAO.getAllCustomers();

        // Load user details for each customer
        for (Customer customer : customers) {
            User user = userDAO.getUserById(customer.getUserId());
            customer.setUser(user);
        }

        return customers;
    }

    /**
     * Search customers
     */
    public List<Customer> searchCustomers(String keyword) {
        logger.info("Searching customers with keyword: {}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }

        List<Customer> customers = customerDAO.searchCustomers(keyword);

        // Load user details for each customer
        for (Customer customer : customers) {
            User user = userDAO.getUserById(customer.getUserId());
            customer.setUser(user);
        }

        return customers;
    }

    /**
     * Get customer by ID
     */
    public Customer getCustomerById(int customerId) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer != null) {
            User user = userDAO.getUserById(customer.getUserId());
            customer.setUser(user);
        }
        return customer;
    }

    /**
     * Create new customer
     */
    public boolean createCustomer(Customer customer, User user) {
        logger.info("Creating new customer");

        // Validate user data
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Set user role to CUSTOMER
        user.setRole(User.UserRole.CUSTOMER);
        user.setAccountStatus(User.AccountStatus.ACTIVE);

        // Create user first
        if (userDAO.createUser(user)) {
            // Then create customer record
            customer.setUserId(user.getUserId());
            if (customerDAO.createCustomer(customer)) {
                logger.info("Customer created successfully");
                return true;
            } else {
                // Rollback: delete user if customer creation failed
                userDAO.deleteUser(user.getUserId());
                throw new RuntimeException("Failed to create customer record");
            }
        } else {
            throw new RuntimeException("Failed to create user account");
        }
    }

    /**
     * Update customer information
     */
    public boolean updateCustomer(Customer customer, User user) {
        logger.info("Updating customer ID: {}", customer.getCustomerId());

        // Update user information
        boolean userUpdated = userDAO.updateUser(user);

        // Update customer information
        boolean customerUpdated = customerDAO.updateCustomer(customer);

        return userUpdated && customerUpdated;
    }

    /**
     * Delete customer
     */
    public boolean deleteCustomer(int customerId) {
        logger.info("Deleting customer ID: {}", customerId);

        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        // Delete customer record
        boolean customerDeleted = customerDAO.deleteCustomer(customerId);

        if (customerDeleted) {
            // Delete associated user account
            userDAO.deleteUser(customer.getUserId());
        }

        return customerDeleted;
    }

    /**
     * Get customer by user ID
     */
    public Customer getCustomerByUserId(int userId) {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer != null) {
            User user = userDAO.getUserById(userId);
            customer.setUser(user);
        }
        return customer;
    }
}
