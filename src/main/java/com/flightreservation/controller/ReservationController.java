package com.flightreservation.controller;

import com.flightreservation.dao.*;
import com.flightreservation.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for reservation operations
 */
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationDAO reservationDAO;
    private final FlightDAO flightDAO;
    private final SeatDAO seatDAO;
    private final PassengerDAO passengerDAO;

    public ReservationController() {
        this.reservationDAO = new ReservationDAO();
        this.flightDAO = new FlightDAO();
        this.seatDAO = new SeatDAO();
        this.passengerDAO = new PassengerDAO();
    }

    /**
     * Create a new reservation
     */
    public Reservation createReservation(int customerId, int flightId, List<Passenger> passengers) {
        logger.info("Creating reservation for customer {} on flight {}", customerId, flightId);

        // Validate flight exists
        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found");
        }

        // Validate passengers
        if (passengers == null || passengers.isEmpty()) {
            throw new IllegalArgumentException("At least one passenger is required");
        }

        // Validate seats are available
        for (Passenger passenger : passengers) {
            if (passenger.getSeatId() == 0) {
                throw new IllegalArgumentException("Seat must be selected for each passenger");
            }

            Seat seat = seatDAO.getSeatById(passenger.getSeatId());
            if (seat == null || !seat.isAvailable()) {
                throw new IllegalArgumentException("Seat " + passenger.getSeatId() + " is not available");
            }
        }

        // Calculate total fare
        double totalFare = 0;
        for (Passenger passenger : passengers) {
            Seat seat = seatDAO.getSeatById(passenger.getSeatId());
            totalFare += seat.getPrice();
        }

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setCustomerId(customerId);
        reservation.setFlightId(flightId);
        reservation.setTotalFare(totalFare);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setPassengers(passengers);

        // Reserve seats
        for (Passenger passenger : passengers) {
            seatDAO.reserveSeat(passenger.getSeatId());
        }

        // Save reservation
        if (reservationDAO.createReservation(reservation)) {
            // Update available seats count
            flightDAO.updateAvailableSeats(flightId, -passengers.size());
            logger.info("Reservation created successfully: {}", reservation.getConfirmationNumber());
            return reservation;
        } else {
            // Rollback seat reservations if save failed
            for (Passenger passenger : passengers) {
                seatDAO.releaseSeat(passenger.getSeatId());
            }
            throw new RuntimeException("Failed to create reservation");
        }
    }

    /**
     * Confirm reservation (after payment)
     */
    public boolean confirmReservation(int reservationId) {
        logger.info("Confirming reservation {}", reservationId);
        return reservationDAO.updateReservationStatus(reservationId, Reservation.ReservationStatus.CONFIRMED);
    }

    /**
     * Cancel reservation
     */
    public boolean cancelReservation(int reservationId) {
        logger.info("Cancelling reservation {}", reservationId);

        Reservation reservation = reservationDAO.getReservationById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found");
        }

        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled");
        }

        // Cancel and release seats
        boolean success = reservationDAO.cancelReservation(reservationId);

        if (success) {
            // Update available seats count
            int passengerCount = reservation.getPassengers().size();
            flightDAO.updateAvailableSeats(reservation.getFlightId(), passengerCount);
        }

        return success;
    }

    /**
     * Get reservation by confirmation number
     */
    public Reservation getReservationByConfirmation(String confirmationNumber) {
        Reservation reservation = reservationDAO.getReservationByConfirmation(confirmationNumber);
        if (reservation != null) {
            // Load flight details
            Flight flight = flightDAO.getFlightById(reservation.getFlightId());
            reservation.setFlight(flight);
        }
        return reservation;
    }

    /**
     * Get all reservations for a customer
     */
    public List<Reservation> getCustomerReservations(int customerId) {
        List<Reservation> reservations = reservationDAO.getReservationsByCustomerId(customerId);

        // Load flight details for each reservation
        for (Reservation reservation : reservations) {
            Flight flight = flightDAO.getFlightById(reservation.getFlightId());
            reservation.setFlight(flight);
        }

        return reservations;
    }

    /**
     * Get reservation details by ID
     */
    public Reservation getReservationById(int reservationId) {
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        if (reservation != null) {
            // Load flight details
            Flight flight = flightDAO.getFlightById(reservation.getFlightId());
            reservation.setFlight(flight);
        }
        return reservation;
    }
}
