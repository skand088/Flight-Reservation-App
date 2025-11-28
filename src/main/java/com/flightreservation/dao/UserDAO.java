package com.flightreservation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flightreservation.database.DatabaseManager;
import com.flightreservation.model.strategies.authentication.AuthenticationStrategy;
import com.flightreservation.model.strategies.authentication.PasswordAuthenticationStrategy;
import com.flightreservation.model.entities.User;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    private final AuthenticationStrategy authStrategy;

    public UserDAO() {
        this.authStrategy = new PasswordAuthenticationStrategy();
    }

    public User authenticate(String username, String password) {
        String sql = "SELECT user_id, username, password_hash, email, phone_number, role, " +
                "account_status, created_date, last_login_date " +
                "FROM users WHERE username = ? AND account_status = 'ACTIVE'";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password_hash");

                if (authStrategy.authenticate(username, password) && password.equals(storedPassword)) {
                    User user = mapResultSetToUser(rs);
                    updateLastLogin(user.getUserId());
                    logger.info("User authenticated successfully using: {}",
                            authStrategy.getAuthenticationMethodName());
                    return user;
                }
            }
            logger.warn("Authentication failed for user: {}", username);
            return null;

        } catch (SQLException e) {
            logger.error("Error authenticating user: " + username, e);
            return null;
        }
    }

    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login_date = NOW() WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error updating last login for user: " + userId, e);
        }
    }

    public String createSession(int userId, String ipAddress) {
        String sessionId = java.util.UUID.randomUUID().toString();
        String sql = "INSERT INTO sessions (session_id, user_id, ip_address) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionId);
            stmt.setInt(2, userId);
            stmt.setString(3, ipAddress);
            stmt.executeUpdate();

            logger.info("Session created for user: {}", userId);
            return sessionId;

        } catch (SQLException e) {
            logger.error("Error creating session for user: " + userId, e);
            return null;
        }
    }

    public void endSession(String sessionId) {
        String sql = "DELETE FROM sessions WHERE session_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionId);
            stmt.executeUpdate();
            logger.info("Session ended: {}", sessionId);

        } catch (SQLException e) {
            logger.error("Error ending session: " + sessionId, e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setRole(User.UserRole.valueOf(rs.getString("role")));
        user.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));

        Timestamp created = rs.getTimestamp("created_date");
        if (created != null) {
            user.setCreatedDate(created.toLocalDateTime());
        }

        Timestamp lastLogin = rs.getTimestamp("last_login_date");
        if (lastLogin != null) {
            user.setLastLoginDate(lastLogin.toLocalDateTime());
        }

        return user;
    }

    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, password_hash, email, phone_number, role, " +
                "account_status, created_date, last_login_date FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;

        } catch (SQLException e) {
            logger.error("Error getting user by ID: " + userId, e);
            return null;
        }
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, email, phone_number, role, account_status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getRole().name());
            stmt.setString(6, user.getAccountStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
                logger.info("User created: {}", user.getUsername());
                return true;
            }
            return false;

        } catch (SQLException e) {
            logger.error("Error creating user: " + user.getUsername(), e);
            return false;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, phone_number = ?, " +
                "role = ?, account_status = ? WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getRole().name());
            stmt.setString(5, user.getAccountStatus().name());
            stmt.setInt(6, user.getUserId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("User updated: {}", user.getUsername());
                return true;
            }
            return false;

        } catch (SQLException e) {
            logger.error("Error updating user: " + user.getUsername(), e);
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("User deleted: {}", userId);
                return true;
            }
            return false;

        } catch (SQLException e) {
            logger.error("Error deleting user: " + userId, e);
            return false;
        }
    }
}
