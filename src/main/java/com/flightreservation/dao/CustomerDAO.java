package com.flightreservation.dao;

import com.flightreservation.database.DatabaseManager;
import com.flightreservation.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Customer operations
 */
public class CustomerDAO {
    private static final Logger logger = LoggerFactory.getLogger(CustomerDAO.class);

    /**
     * Get customer by user ID
     */
    public Customer getCustomerByUserId(int userId) {
        String sql = "SELECT * FROM customers WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving customer by user ID: {}", userId, e);
        }
        return null;
    }

    /**
     * Get customer by ID
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving customer by ID: {}", customerId, e);
        }
        return null;
    }

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT c.*, u.username, u.email FROM customers c " +
                "JOIN users u ON c.user_id = u.user_id";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
            logger.info("Retrieved {} customers", customers.size());
        } catch (SQLException e) {
            logger.error("Error retrieving all customers", e);
        }
        return customers;
    }

    /**
     * Search customers by name or email
     */
    public List<Customer> searchCustomers(String keyword) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT c.*, u.username, u.email FROM customers c " +
                "JOIN users u ON c.user_id = u.user_id " +
                "WHERE u.username LIKE ? OR u.email LIKE ? OR c.phone_number LIKE ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
            logger.info("Found {} customers matching '{}'", customers.size(), keyword);
        } catch (SQLException e) {
            logger.error("Error searching customers", e);
        }
        return customers;
    }

    /**
     * Create new customer
     */
    public boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (user_id, phone_number, address, preferred_payment_method) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, customer.getUserId());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.setString(3, customer.getAddress());
            stmt.setString(4, customer.getPreferredPaymentMethod());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                }
                logger.info("Created customer for user ID: {}", customer.getUserId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating customer", e);
        }
        return false;
    }

    /**
     * Update customer
     */
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET phone_number = ?, address = ?, preferred_payment_method = ? " +
                "WHERE customer_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getPhoneNumber());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPreferredPaymentMethod());
            stmt.setInt(4, customer.getCustomerId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Updated customer ID: {}", customer.getCustomerId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating customer", e);
        }
        return false;
    }

    /**
     * Delete customer
     */
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Deleted customer ID: {}", customerId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting customer", e);
        }
        return false;
    }

    /**
     * Map ResultSet to Customer object
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setUserId(rs.getInt("user_id"));
        customer.setPhoneNumber(rs.getString("phone_number"));
        customer.setAddress(rs.getString("address"));
        customer.setPreferredPaymentMethod(rs.getString("preferred_payment_method"));
        return customer;
    }
}
