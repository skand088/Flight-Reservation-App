package com.flightreservation.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flightreservation.dao.CustomerDAO;
import com.flightreservation.dao.UserDAO;
import com.flightreservation.model.entities.Customer;
import com.flightreservation.model.entities.User;

public class CustomerManagementController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerManagementController.class);
    private final CustomerDAO customerDAO;
    private final UserDAO userDAO;

    public CustomerManagementController() {
        this.customerDAO = new CustomerDAO();
        this.userDAO = new UserDAO();
    }

    public List<Customer> getAllCustomers() {
        logger.info("Retrieving all customers");
        List<Customer> customers = customerDAO.getAllCustomers();

        for (Customer customer : customers) {
            if (customer.getUser() == null) {
                User user = userDAO.getUserById(customer.getUserId());
                customer.setUser(user);
            }
        }

        return customers;
    }

    public List<Customer> searchCustomers(String keyword) {
        logger.info("Searching customers with keyword: {}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }

        List<Customer> customers = customerDAO.searchCustomers(keyword);

        for (Customer customer : customers) {
            if (customer.getUser() == null) {
                User user = userDAO.getUserById(customer.getUserId());
                customer.setUser(user);
            }
        }

        return customers;
    }

    public Customer getCustomerById(int customerId) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer != null) {
            User user = userDAO.getUserById(customer.getUserId());
            customer.setUser(user);
        }
        return customer;
    }

    public boolean createCustomer(Customer customer, User user) {
        logger.info("Creating new customer");

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        user.setRole(User.UserRole.CUSTOMER);
        user.setAccountStatus(User.AccountStatus.ACTIVE);

        if (userDAO.createUser(user)) {
            customer.setUserId(user.getUserId());
            customer.setCustomerId(user.getUserId());
            logger.info("Customer created successfully");
            return true;
        } else {
            throw new RuntimeException("Failed to create user account");
        }
    }

    public boolean updateCustomer(Customer customer, User user) {
        logger.info("Updating customer ID: {}", customer.getCustomerId());

        boolean userUpdated = userDAO.updateUser(user);

        return userUpdated;
    }

    public boolean deleteCustomer(int customerId) {
        logger.info("Deleting customer ID: {}", customerId);

        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        boolean deleted = userDAO.deleteUser(customer.getUserId());

        return deleted;
    }

    public Customer getCustomerByUserId(int userId) {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer != null) {
            User user = userDAO.getUserById(userId);
            customer.setUser(user);
        }
        return customer;
    }
}
