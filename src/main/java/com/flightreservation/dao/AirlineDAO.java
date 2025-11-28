package com.flightreservation.dao;

import com.flightreservation.database.DatabaseManager;
import com.flightreservation.model.entities.Airline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirlineDAO {
    private static final Logger logger = LoggerFactory.getLogger(AirlineDAO.class);

    public List<Airline> getAllAirlines() {
        List<Airline> airlines = new ArrayList<>();
        String sql = "SELECT * FROM airlines ORDER BY airline_name";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                airlines.add(mapResultSetToAirline(rs));
            }
            logger.info("Retrieved {} airlines", airlines.size());
        } catch (SQLException e) {
            logger.error("Error retrieving all airlines", e);
        }
        return airlines;
    }

    public Airline getAirlineById(int airlineId) {
        String sql = "SELECT * FROM airlines WHERE airline_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, airlineId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAirline(rs);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving airline by ID: {}", airlineId, e);
        }
        return null;
    }

    public Airline getAirlineByCode(String airlineCode) {
        String sql = "SELECT * FROM airlines WHERE airline_code = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, airlineCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAirline(rs);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving airline by code: {}", airlineCode, e);
        }
        return null;
    }

    public boolean createAirline(Airline airline) {
        String sql = "INSERT INTO airlines (airline_name, airline_code, contact_info) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, airline.getAirlineName());
            stmt.setString(2, airline.getAirlineCode());
            stmt.setString(3, airline.getContactInfo());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    airline.setAirlineId(generatedKeys.getInt(1));
                }
                logger.info("Created airline: {} ({})", airline.getAirlineName(), airline.getAirlineCode());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating airline", e);
        }
        return false;
    }

    public boolean updateAirline(Airline airline) {
        String sql = "UPDATE airlines SET airline_name = ?, airline_code = ?, contact_info = ? WHERE airline_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, airline.getAirlineName());
            stmt.setString(2, airline.getAirlineCode());
            stmt.setString(3, airline.getContactInfo());
            stmt.setInt(4, airline.getAirlineId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Updated airline: {}", airline.getAirlineName());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating airline", e);
        }
        return false;
    }

    public boolean deleteAirline(int airlineId) {
        String sql = "DELETE FROM airlines WHERE airline_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, airlineId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Deleted airline with ID: {}", airlineId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting airline", e);
        }
        return false;
    }

    public boolean isAirlineCodeExists(String airlineCode) {
        String sql = "SELECT COUNT(*) FROM airlines WHERE airline_code = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, airlineCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking airline code existence", e);
        }
        return false;
    }

    private Airline mapResultSetToAirline(ResultSet rs) throws SQLException {
        Airline airline = new Airline();
        airline.setAirlineId(rs.getInt("airline_id"));
        airline.setAirlineName(rs.getString("airline_name"));
        airline.setAirlineCode(rs.getString("airline_code"));
        airline.setContactInfo(rs.getString("contact_info"));
        return airline;
    }
}
