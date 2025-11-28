package com.flightreservation.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flightreservation.dao.FlightDAO;
import com.flightreservation.dao.ReservationDAO;
import com.flightreservation.dao.SeatDAO;
import com.flightreservation.model.strategies.payment.CreditCardPaymentStrategy;
import com.flightreservation.model.entities.Flight;
import com.flightreservation.model.entities.Passenger;
import com.flightreservation.model.strategies.payment.PaymentStrategy;
import com.flightreservation.model.entities.Reservation;
import com.flightreservation.model.entities.Seat;

public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationDAO reservationDAO;
    private final FlightDAO flightDAO;
    private final SeatDAO seatDAO;
    private PaymentStrategy paymentStrategy;

    public ReservationController() {
        this.reservationDAO = new ReservationDAO();
        this.flightDAO = new FlightDAO();
        this.seatDAO = new SeatDAO();
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public Reservation createReservation(int customerId, int flightId, List<Passenger> passengers) {
        logger.info("Creating reservation for customer {} on flight {}", customerId, flightId);

        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found");
        }

        if (passengers == null || passengers.isEmpty()) {
            throw new IllegalArgumentException("At least one passenger is required");
        }

        for (Passenger passenger : passengers) {
            if (passenger.getSeatId() == 0) {
                throw new IllegalArgumentException("Seat must be selected for each passenger");
            }

            Seat seat = seatDAO.getSeatById(passenger.getSeatId());
            if (seat == null || !seat.isAvailable()) {
                throw new IllegalArgumentException("Seat " + passenger.getSeatId() + " is not available");
            }
        }

        double totalFare = 0;
        for (Passenger passenger : passengers) {
            Seat seat = seatDAO.getSeatById(passenger.getSeatId());
            totalFare += seat.getPrice();
        }

        Reservation reservation = new Reservation();
        reservation.setCustomerId(customerId);
        reservation.setFlightId(flightId);
        reservation.setTotalFare(totalFare);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setPassengers(passengers);

        for (Passenger passenger : passengers) {
            seatDAO.reserveSeat(passenger.getSeatId());
        }

        if (paymentStrategy == null) {
            paymentStrategy = new CreditCardPaymentStrategy("****1234", "Default User", "12/25", "123");
            logger.info("No payment strategy set, using default Credit Card");
        }

        if (!paymentStrategy.processPayment(totalFare)) {
            for (Passenger passenger : passengers) {
                seatDAO.releaseSeat(passenger.getSeatId());
            }
            throw new RuntimeException("Payment processing failed");
        }

        if (reservationDAO.createReservation(reservation)) {
            flightDAO.updateAvailableSeats(flightId, -passengers.size());
            logger.info("Reservation created successfully: {}", reservation.getConfirmationNumber());
            return reservation;
        } else {
            for (Passenger passenger : passengers) {
                seatDAO.releaseSeat(passenger.getSeatId());
            }
            throw new RuntimeException("Failed to create reservation");
        }
    }

    public boolean confirmReservation(int reservationId) {
        logger.info("Confirming reservation {}", reservationId);
        return reservationDAO.updateReservationStatus(reservationId, Reservation.ReservationStatus.CONFIRMED);
    }

    public boolean cancelReservation(int reservationId) {
        logger.info("Cancelling reservation {}", reservationId);

        Reservation reservation = reservationDAO.getReservationById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found");
        }

        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled");
        }

        boolean success = reservationDAO.cancelReservation(reservationId);

        if (success) {
            int passengerCount = reservation.getPassengers().size();
            flightDAO.updateAvailableSeats(reservation.getFlightId(), passengerCount);
        }

        return success;
    }

    public Reservation getReservationByConfirmation(String confirmationNumber) {
        Reservation reservation = reservationDAO.getReservationByConfirmation(confirmationNumber);
        if (reservation != null) {
            Flight flight = flightDAO.getFlightById(reservation.getFlightId());
            reservation.setFlight(flight);
        }
        return reservation;
    }

    public List<Reservation> getCustomerReservations(int customerId) {
        List<Reservation> reservations = reservationDAO.getReservationsByCustomerId(customerId);

        for (Reservation reservation : reservations) {
            Flight flight = flightDAO.getFlightById(reservation.getFlightId());
            reservation.setFlight(flight);
        }

        return reservations;
    }

    public Reservation getReservationById(int reservationId) {
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        if (reservation != null) {
            Flight flight = flightDAO.getFlightById(reservation.getFlightId());
            reservation.setFlight(flight);
        }
        return reservation;
    }
}
