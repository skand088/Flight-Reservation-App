package com.flightreservation.dao;

import com.flightreservation.database.DatabaseManager;
import com.flightreservation.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO for Reservation operations
 */
public class ReservationDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReservationDAO.class);

    /**
     * Create new reservation
     */
    public boolean createReservation(Reservation reservation) {
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Generate confirmation number if not set
            if (reservation.getConfirmationNumber() == null) {
                reservation.setConfirmationNumber(generateConfirmationNumber());
            }

            // Insert reservation
            String sql = "INSERT INTO reservations (confirmation_number, reservation_date, status, " +
                    "total_fare, customer_id, flight_id) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, reservation.getConfirmationNumber());
            stmt.setTimestamp(2, Timestamp.valueOf(reservation.getReservationDate()));
            stmt.setString(3, reservation.getStatus().name());
            stmt.setDouble(4, reservation.getTotalFare());
            stmt.setInt(5, reservation.getCustomerId());
            stmt.setInt(6, reservation.getFlightId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    reservation.setReservationId(generatedKeys.getInt(1));
                }

                // Insert passengers if any
                if (reservation.getPassengers() != null && !reservation.getPassengers().isEmpty()) {
                    PassengerDAO passengerDAO = new PassengerDAO();
                    for (Passenger passenger : reservation.getPassengers()) {
                        passengerDAO.createPassenger(passenger, conn);
                        linkPassengerToReservation(reservation.getReservationId(), passenger.getPassengerId(),
                                passenger.getSeatId(), conn);
                    }
                }

                conn.commit();
                logger.info("Created reservation: {}", reservation.getConfirmationNumber());
                return true;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error creating reservation", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                }
            }
        }
        return false;
    }

    /**
     * Link passenger to reservation
     */
    private void linkPassengerToReservation(int reservationId, int passengerId, int seatId, Connection conn)
            throws SQLException {
        String sql = "INSERT INTO reservation_passengers (reservation_id, passenger_id, seat_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            stmt.setInt(2, passengerId);
            stmt.setInt(3, seatId);
            stmt.executeUpdate();
        }
    }

    /**
     * Get reservation by ID
     */
    public Reservation getReservationById(int reservationId) {
        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                loadReservationPassengers(reservation);
                return reservation;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving reservation by ID: {}", reservationId, e);
        }
        return null;
    }

    /**
     * Get reservation by confirmation number
     */
    public Reservation getReservationByConfirmation(String confirmationNumber) {
        String sql = "SELECT * FROM reservations WHERE confirmation_number = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, confirmationNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                loadReservationPassengers(reservation);
                return reservation;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving reservation by confirmation: {}", confirmationNumber, e);
        }
        return null;
    }

    /**
     * Get all reservations for a customer
     */
    public List<Reservation> getReservationsByCustomerId(int customerId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE customer_id = ? ORDER BY reservation_date DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                loadReservationPassengers(reservation);
                reservations.add(reservation);
            }
            logger.info("Retrieved {} reservations for customer {}", reservations.size(), customerId);
        } catch (SQLException e) {
            logger.error("Error retrieving reservations for customer: {}", customerId, e);
        }
        return reservations;
    }

    /**
     * Update reservation status
     */
    public boolean updateReservationStatus(int reservationId, Reservation.ReservationStatus status) {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, reservationId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Updated reservation {} to status {}", reservationId, status);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating reservation status", e);
        }
        return false;
    }

    /**
     * Get all reservations
     */
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY reservation_date DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                loadReservationPassengers(reservation);
                
                // Load flight details
                FlightDAO flightDAO = new FlightDAO();
                reservation.setFlight(flightDAO.getFlightById(reservation.getFlightId()));
                
                // Load customer details
                CustomerDAO customerDAO = new CustomerDAO();
                reservation.setCustomer(customerDAO.getCustomerById(reservation.getCustomerId()));
                
                reservations.add(reservation);
            }
            logger.info("Retrieved {} total reservations", reservations.size());
        } catch (SQLException e) {
            logger.error("Error retrieving all reservations", e);
        }
        return reservations;
    }

    /**
     * Get reservations by status
     */
    public List<Reservation> getReservationsByStatus(Reservation.ReservationStatus status) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE status = ? ORDER BY reservation_date DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                loadReservationPassengers(reservation);
                
                // Load flight details
                FlightDAO flightDAO = new FlightDAO();
                reservation.setFlight(flightDAO.getFlightById(reservation.getFlightId()));
                
                // Load customer details
                CustomerDAO customerDAO = new CustomerDAO();
                reservation.setCustomer(customerDAO.getCustomerById(reservation.getCustomerId()));
                
                reservations.add(reservation);
            }
            logger.info("Retrieved {} reservations with status {}", reservations.size(), status);
        } catch (SQLException e) {
            logger.error("Error retrieving reservations by status", e);
        }
        return reservations;
    }

    /**
     * Cancel reservation
     */
    public boolean cancelReservation(int reservationId) {
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Update reservation status
            updateReservationStatus(reservationId, Reservation.ReservationStatus.CANCELLED);

            // Release seats
            String sql = "SELECT seat_id FROM reservation_passengers WHERE reservation_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, reservationId);
            ResultSet rs = stmt.executeQuery();

            SeatDAO seatDAO = new SeatDAO();
            while (rs.next()) {
                int seatId = rs.getInt("seat_id");
                seatDAO.releaseSeat(seatId);
            }

            conn.commit();
            logger.info("Cancelled reservation {}", reservationId);
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error cancelling reservation", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                }
            }
        }
        return false;
    }

    /**
     * Load passengers for a reservation
     */
    private void loadReservationPassengers(Reservation reservation) {
        String sql = "SELECT p.*, rp.seat_id FROM passengers p " +
                "JOIN reservation_passengers rp ON p.passenger_id = rp.passenger_id " +
                "WHERE rp.reservation_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getReservationId());
            ResultSet rs = stmt.executeQuery();

            PassengerDAO passengerDAO = new PassengerDAO();
            List<Passenger> passengers = new ArrayList<>();
            while (rs.next()) {
                Passenger passenger = passengerDAO.mapResultSetToPassenger(rs);
                passenger.setSeatId(rs.getInt("seat_id"));
                passengers.add(passenger);
            }
            reservation.setPassengers(passengers);
        } catch (SQLException e) {
            logger.error("Error loading passengers for reservation", e);
        }
    }

    /**
     * Generate unique confirmation number
     */
    private String generateConfirmationNumber() {
        return "FRA" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Map ResultSet to Reservation object
     */
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setConfirmationNumber(rs.getString("confirmation_number"));

        Timestamp reservationDate = rs.getTimestamp("reservation_date");
        if (reservationDate != null) {
            reservation.setReservationDate(reservationDate.toLocalDateTime());
        }

        reservation.setStatus(Reservation.ReservationStatus.valueOf(rs.getString("status")));
        reservation.setTotalFare(rs.getDouble("total_fare"));
        reservation.setCustomerId(rs.getInt("customer_id"));
        reservation.setFlightId(rs.getInt("flight_id"));
        return reservation;
    }
}
