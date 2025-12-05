package com.flightreservation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flightreservation.database.DatabaseManager;
import com.flightreservation.model.entities.Customer;
import com.flightreservation.model.entities.User;

public class CustomerDAO {
    private static final Logger logger = LoggerFactory.getLogger(CustomerDAO.class);

    public Customer getCustomerByUserId(int userId) {
        String sql = "SELECT user_id, username, email, phone_number, role, account_status " +
                "FROM users WHERE user_id = ? AND role = 'CUSTOMER'";

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

    public Customer getCustomerById(int customerId) {
        String sql = "SELECT user_id, username, email, phone_number, role, account_status " +
                "FROM users WHERE user_id = ? AND role = 'CUSTOMER'";

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

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT user_id, username, email, phone_number, role, account_status " +
                "FROM users WHERE role = 'CUSTOMER'";

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

    public List<Customer> searchCustomers(String keyword) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT user_id, username, email, phone_number, role, account_status " +
                "FROM users WHERE role = 'CUSTOMER' " +
                "AND (username LIKE ? OR email LIKE ? OR phone_number LIKE ?)";

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

    public boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customer_id, frequent_flyer_number, loyalty_points, preferred_airline, address) "
                +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customer.getUserId());
            stmt.setString(2, customer.getFrequentFlyerNumber());
            stmt.setInt(3, customer.getLoyaltyPoints());
            stmt.setString(4, customer.getPreferredAirline());
            stmt.setString(5, customer.getAddress());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                customer.setCustomerId(customer.getUserId());
                logger.info("Created customer for user ID: {}", customer.getUserId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating customer", e);
        }
        return false;
    }

    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET frequent_flyer_number = ?, loyalty_points = ?, preferred_airline = ?, address = ? "
                +
                "WHERE customer_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFrequentFlyerNumber());
            stmt.setInt(2, customer.getLoyaltyPoints());
            stmt.setString(3, customer.getPreferredAirline());
            stmt.setString(4, customer.getAddress());
            stmt.setInt(5, customer.getCustomerId());

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

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        int userId = rs.getInt("user_id");
        customer.setCustomerId(userId);
        customer.setUserId(userId);

        User user = new User();
        user.setUserId(userId);
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setRole(User.UserRole.valueOf(rs.getString("role")));
        user.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));

        customer.setUser(user);

        return customer;
    }
}
