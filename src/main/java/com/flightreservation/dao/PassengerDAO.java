package com.flightreservation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flightreservation.database.DatabaseManager;
import com.flightreservation.model.entities.Passenger;

public class PassengerDAO {
    private static final Logger logger = LoggerFactory.getLogger(PassengerDAO.class);

    public boolean createPassenger(Passenger passenger) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            return createPassenger(passenger, conn);
        } catch (SQLException e) {
            logger.error("Error creating passenger", e);
            return false;
        }
    }

    public boolean createPassenger(Passenger passenger, Connection conn) throws SQLException {
        String sql = "INSERT INTO passengers (first_name, last_name, age, id_number, id_type, " +
                "contact_email, contact_phone) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, passenger.getFirstName());
            stmt.setString(2, passenger.getLastName());
            stmt.setInt(3, passenger.getAge());
            stmt.setString(4, passenger.getIdNumber());
            stmt.setString(5, passenger.getIdType().name());
            stmt.setString(6, passenger.getContactEmail());
            stmt.setString(7, passenger.getContactPhone());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    passenger.setPassengerId(generatedKeys.getInt(1));
                }
                logger.info("Created passenger: {}", passenger.getFullName());
                return true;
            }
        }
        return false;
    }

    public Passenger getPassengerById(int passengerId) {
        String sql = "SELECT * FROM passengers WHERE passenger_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, passengerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPassenger(rs);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving passenger by ID: {}", passengerId, e);
        }
        return null;
    }

    public boolean updatePassenger(Passenger passenger) {
        String sql = "UPDATE passengers SET first_name = ?, last_name = ?, age = ?, id_number = ?, " +
                "id_type = ?, contact_email = ?, contact_phone = ? WHERE passenger_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, passenger.getFirstName());
            stmt.setString(2, passenger.getLastName());
            stmt.setInt(3, passenger.getAge());
            stmt.setString(4, passenger.getIdNumber());
            stmt.setString(5, passenger.getIdType().name());
            stmt.setString(6, passenger.getContactEmail());
            stmt.setString(7, passenger.getContactPhone());
            stmt.setInt(8, passenger.getPassengerId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Updated passenger: {}", passenger.getFullName());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating passenger", e);
        }
        return false;
    }

    public Passenger mapResultSetToPassenger(ResultSet rs) throws SQLException {
        Passenger passenger = new Passenger();
        passenger.setPassengerId(rs.getInt("passenger_id"));
        passenger.setFirstName(rs.getString("first_name"));
        passenger.setLastName(rs.getString("last_name"));
        passenger.setAge(rs.getInt("age"));
        passenger.setIdNumber(rs.getString("id_number"));
        passenger.setIdType(Passenger.IdType.valueOf(rs.getString("id_type")));
        passenger.setContactEmail(rs.getString("contact_email"));
        passenger.setContactPhone(rs.getString("contact_phone"));
        return passenger;
    }
}
