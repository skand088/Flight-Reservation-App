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
import com.flightreservation.model.entities.Seat;

public class SeatDAO {
    private static final Logger logger = LoggerFactory.getLogger(SeatDAO.class);

    public List<Seat> getSeatsByFlightId(int flightId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE flight_id = ? ORDER BY seat_number";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                seats.add(mapResultSetToSeat(rs));
            }
            logger.info("Retrieved {} seats for flight {}", seats.size(), flightId);
        } catch (SQLException e) {
            logger.error("Error retrieving seats for flight: {}", flightId, e);
        }
        return seats;
    }

    public List<Seat> getAvailableSeats(int flightId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE flight_id = ? AND status = 'AVAILABLE'";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, flightId);
            logger.info("Executing query: {} with flight_id={}", sql, flightId);
            ResultSet rs = stmt.executeQuery();

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                Seat seat = mapResultSetToSeat(rs);
                logger.info("Found seat: {} - {} - {} - {}",
                        seat.getSeatNumber(), seat.getSeatClass(), seat.getStatus(), seat.getPrice());
                seats.add(seat);
            }
            logger.info("Query returned {} rows, mapped to {} seats for flight {}", rowCount, seats.size(), flightId);
        } catch (SQLException e) {
            logger.error("Error retrieving available seats for flight {}: {}", flightId, e.getMessage(), e);
        }
        return seats;
    }

    public Seat getSeatById(int seatId) {
        String sql = "SELECT * FROM seats WHERE seat_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, seatId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSeat(rs);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving seat by ID: {}", seatId, e);
        }
        return null;
    }

    public boolean reserveSeat(int seatId) {
        return updateSeatStatus(seatId, Seat.SeatStatus.RESERVED);
    }

    public boolean releaseSeat(int seatId) {
        return updateSeatStatus(seatId, Seat.SeatStatus.AVAILABLE);
    }

    public boolean updateSeatStatus(int seatId, Seat.SeatStatus status) {
        String sql = "UPDATE seats SET status = ? WHERE seat_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, seatId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Updated seat {} to status {}", seatId, status);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating seat status", e);
        }
        return false;
    }

    public boolean createSeat(Seat seat) {
        String sql = "INSERT INTO seats (seat_number, seat_class, seat_type, price, status, flight_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, seat.getSeatNumber());
            stmt.setString(2, seat.getSeatClass().name());
            stmt.setString(3, seat.getSeatType().name());
            stmt.setDouble(4, seat.getPrice());
            stmt.setString(5, seat.getStatus().name());
            stmt.setInt(6, seat.getFlightId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    seat.setSeatId(generatedKeys.getInt(1));
                }
                logger.info("Created seat: {}", seat.getSeatNumber());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating seat", e);
        }
        return false;
    }

    private Seat mapResultSetToSeat(ResultSet rs) throws SQLException {
        Seat seat = new Seat();
        seat.setSeatId(rs.getInt("seat_id"));
        seat.setSeatNumber(rs.getString("seat_number"));
        seat.setSeatClass(Seat.SeatClass.valueOf(rs.getString("seat_class")));
        seat.setSeatType(Seat.SeatType.valueOf(rs.getString("seat_type")));
        seat.setPrice(rs.getDouble("price"));
        seat.setStatus(Seat.SeatStatus.valueOf(rs.getString("status")));
        seat.setFlightId(rs.getInt("flight_id"));
        return seat;
    }
}
