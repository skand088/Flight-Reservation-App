package com.flightreservation.dao;

import com.flightreservation.database.DatabaseManager;
import com.flightreservation.model.entities.Aircraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AircraftDAO {
    private static final Logger logger = LoggerFactory.getLogger(AircraftDAO.class);

    public List<Aircraft> getAllAircraft() {
        List<Aircraft> aircraftList = new ArrayList<>();
        String sql = "SELECT * FROM aircraft ORDER BY manufacturer, model";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                aircraftList.add(mapResultSetToAircraft(rs));
            }
            logger.info("Retrieved {} aircraft", aircraftList.size());
        } catch (SQLException e) {
            logger.error("Error retrieving all aircraft", e);
        }
        return aircraftList;
    }

    public Aircraft getAircraftById(int aircraftId) {
        String sql = "SELECT * FROM aircraft WHERE aircraft_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, aircraftId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAircraft(rs);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving aircraft by ID: {}", aircraftId, e);
        }
        return null;
    }

    public boolean createAircraft(Aircraft aircraft) {
        String sql = "INSERT INTO aircraft (tail_number, model, manufacturer, total_seats, seat_configuration) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, aircraft.getTailNumber());
            stmt.setString(2, aircraft.getModel());
            stmt.setString(3, aircraft.getManufacturer());
            stmt.setInt(4, aircraft.getTotalSeats());
            stmt.setString(5, aircraft.getSeatConfiguration());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    aircraft.setAircraftId(generatedKeys.getInt(1));
                }
                logger.info("Created aircraft: {}", aircraft.getTailNumber());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating aircraft", e);
        }
        return false;
    }

    public boolean updateAircraft(Aircraft aircraft) {
        String sql = "UPDATE aircraft SET tail_number = ?, model = ?, manufacturer = ?, " +
                "total_seats = ?, seat_configuration = ? WHERE aircraft_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aircraft.getTailNumber());
            stmt.setString(2, aircraft.getModel());
            stmt.setString(3, aircraft.getManufacturer());
            stmt.setInt(4, aircraft.getTotalSeats());
            stmt.setString(5, aircraft.getSeatConfiguration());
            stmt.setInt(6, aircraft.getAircraftId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Updated aircraft ID: {}", aircraft.getAircraftId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating aircraft", e);
        }
        return false;
    }

    public boolean deleteAircraft(int aircraftId) {
        String sql = "DELETE FROM aircraft WHERE aircraft_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, aircraftId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Deleted aircraft ID: {}", aircraftId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting aircraft", e);
        }
        return false;
    }

    private Aircraft mapResultSetToAircraft(ResultSet rs) throws SQLException {
        Aircraft aircraft = new Aircraft();
        aircraft.setAircraftId(rs.getInt("aircraft_id"));
        aircraft.setTailNumber(rs.getString("tail_number"));
        aircraft.setModel(rs.getString("model"));
        aircraft.setManufacturer(rs.getString("manufacturer"));
        aircraft.setTotalSeats(rs.getInt("total_seats"));
        aircraft.setSeatConfiguration(rs.getString("seat_configuration"));
        return aircraft;
    }
}
