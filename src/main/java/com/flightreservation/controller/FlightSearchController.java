package com.flightreservation.controller;

import com.flightreservation.dao.FlightDAO;
import com.flightreservation.dao.SeatDAO;
import com.flightreservation.model.Flight;
import com.flightreservation.model.Seat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for flight search operations
 */
public class FlightSearchController {
    private static final Logger logger = LoggerFactory.getLogger(FlightSearchController.class);
    private final FlightDAO flightDAO;
    private final SeatDAO seatDAO;

    public FlightSearchController() {
        this.flightDAO = new FlightDAO();
        this.seatDAO = new SeatDAO();
    }

    /**
     * Search for flights
     */
    public List<Flight> searchFlights(String origin, String destination, LocalDateTime departureDate) {
        logger.info("Searching flights: {} -> {} on {}", origin, destination, departureDate.toLocalDate());

        if (origin == null || origin.trim().isEmpty()) {
            throw new IllegalArgumentException("Origin airport is required");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination airport is required");
        }
        if (departureDate == null) {
            throw new IllegalArgumentException("Departure date is required");
        }

        return flightDAO.searchFlights(origin, destination, departureDate);
    }

    /**
     * Get flight details with available seats
     */
    public Flight getFlightDetails(int flightId) {
        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found");
        }
        return flight;
    }

    /**
     * Get available seats for a flight
     */
    public List<Seat> getAvailableSeats(int flightId) {
        return seatDAO.getAvailableSeats(flightId);
    }

    /**
     * Get all seats for a flight
     */
    public List<Seat> getAllSeatsForFlight(int flightId) {
        return seatDAO.getSeatsByFlightId(flightId);
    }

    /**
     * Check seat availability
     */
    public boolean isSeatAvailable(int seatId) {
        Seat seat = seatDAO.getSeatById(seatId);
        return seat != null && seat.isAvailable();
    }
}
