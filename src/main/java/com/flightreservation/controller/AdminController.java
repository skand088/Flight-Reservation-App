package com.flightreservation.controller;

import com.flightreservation.dao.*;
import com.flightreservation.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for admin operations
 */
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final FlightDAO flightDAO;
    private final RouteDAO routeDAO;
    private final AircraftDAO aircraftDAO;
    private final SeatDAO seatDAO;

    public AdminController() {
        this.flightDAO = new FlightDAO();
        this.routeDAO = new RouteDAO();
        this.aircraftDAO = new AircraftDAO();
        this.seatDAO = new SeatDAO();
    }

    // Flight Management
    public List<Flight> getAllFlights() {
        return flightDAO.getAllFlights();
    }

    public boolean createFlight(Flight flight) {
        logger.info("Creating new flight: {}", flight.getFlightNumber());

        // Validate flight data
        validateFlight(flight);

        // Check for schedule conflicts
        if (hasScheduleConflict(flight)) {
            throw new IllegalStateException("Aircraft is already scheduled for this time period");
        }

        // Create flight
        if (flightDAO.createFlight(flight)) {
            // Generate seats for the flight
            generateSeatsForFlight(flight);
            logger.info("Flight created successfully");
            return true;
        }
        return false;
    }

    public boolean updateFlight(Flight flight) {
        logger.info("Updating flight: {}", flight.getFlightNumber());
        validateFlight(flight);
        return flightDAO.updateFlight(flight);
    }

    public boolean deleteFlight(int flightId) {
        logger.info("Deleting flight ID: {}", flightId);

        // Check if flight has reservations
        // TODO: Add check for existing reservations

        return flightDAO.deleteFlight(flightId);
    }

    // Route Management
    public List<Route> getAllRoutes() {
        return routeDAO.getAllRoutes();
    }

    public boolean createRoute(Route route) {
        logger.info("Creating new route: {} -> {}", route.getOriginAirport(), route.getDestinationAirport());
        validateRoute(route);
        return routeDAO.createRoute(route);
    }

    public boolean updateRoute(Route route) {
        logger.info("Updating route ID: {}", route.getRouteId());
        validateRoute(route);
        return routeDAO.updateRoute(route);
    }

    public boolean deleteRoute(int routeId) {
        logger.info("Deleting route ID: {}", routeId);
        return routeDAO.deleteRoute(routeId);
    }

    // Aircraft Management
    public List<Aircraft> getAllAircraft() {
        return aircraftDAO.getAllAircraft();
    }

    public boolean createAircraft(Aircraft aircraft) {
        logger.info("Creating new aircraft: {}", aircraft.getTailNumber());
        validateAircraft(aircraft);
        return aircraftDAO.createAircraft(aircraft);
    }

    public boolean updateAircraft(Aircraft aircraft) {
        logger.info("Updating aircraft ID: {}", aircraft.getAircraftId());
        validateAircraft(aircraft);
        return aircraftDAO.updateAircraft(aircraft);
    }

    public boolean deleteAircraft(int aircraftId) {
        logger.info("Deleting aircraft ID: {}", aircraftId);
        return aircraftDAO.deleteAircraft(aircraftId);
    }

    // Validation Methods
    private void validateFlight(Flight flight) {
        if (flight.getFlightNumber() == null || flight.getFlightNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Flight number is required");
        }
        if (flight.getDepartureTime() == null) {
            throw new IllegalArgumentException("Departure time is required");
        }
        if (flight.getArrivalTime() == null) {
            throw new IllegalArgumentException("Arrival time is required");
        }
        if (flight.getDepartureTime().isAfter(flight.getArrivalTime())) {
            throw new IllegalArgumentException("Departure time must be before arrival time");
        }
        if (flight.getBasePrice() <= 0) {
            throw new IllegalArgumentException("Base price must be greater than zero");
        }
        if (flight.getAircraftId() == 0) {
            throw new IllegalArgumentException("Aircraft must be selected");
        }
        if (flight.getRouteId() == 0) {
            throw new IllegalArgumentException("Route must be selected");
        }
    }

    private void validateRoute(Route route) {
        if (route.getOriginAirport() == null || route.getOriginAirport().trim().isEmpty()) {
            throw new IllegalArgumentException("Origin airport is required");
        }
        if (route.getDestinationAirport() == null || route.getDestinationAirport().trim().isEmpty()) {
            throw new IllegalArgumentException("Destination airport is required");
        }
        if (route.getOriginAirport().equals(route.getDestinationAirport())) {
            throw new IllegalArgumentException("Origin and destination must be different");
        }
        if (route.getDistance() <= 0) {
            throw new IllegalArgumentException("Distance must be greater than zero");
        }
    }

    private void validateAircraft(Aircraft aircraft) {
        if (aircraft.getTailNumber() == null || aircraft.getTailNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Tail number is required");
        }
        if (aircraft.getModel() == null || aircraft.getModel().trim().isEmpty()) {
            throw new IllegalArgumentException("Model is required");
        }
        if (aircraft.getManufacturer() == null || aircraft.getManufacturer().trim().isEmpty()) {
            throw new IllegalArgumentException("Manufacturer is required");
        }
        if (aircraft.getTotalSeats() <= 0) {
            throw new IllegalArgumentException("Total seats must be greater than zero");
        }
    }

    private boolean hasScheduleConflict(Flight flight) {
        // TODO: Implement schedule conflict checking
        // Check if the same aircraft is scheduled for overlapping times
        return false;
    }

    private void generateSeatsForFlight(Flight flight) {
        logger.info("Generating seats for flight {}", flight.getFlightNumber());

        Aircraft aircraft = aircraftDAO.getAircraftById(flight.getAircraftId());
        if (aircraft == null) {
            logger.warn("Aircraft not found for seat generation");
            return;
        }

        // Generate economy seats (70% of total)
        int economySeats = (int) (aircraft.getTotalSeats() * 0.7);
        int businessSeats = (int) (aircraft.getTotalSeats() * 0.2);
        int firstClassSeats = aircraft.getTotalSeats() - economySeats - businessSeats;

        int seatNumber = 1;
        String[] seatLetters = { "A", "B", "C", "D", "E", "F" };

        // First class
        for (int i = 0; i < firstClassSeats; i++) {
            int row = (i / 4) + 1;
            String letter = seatLetters[i % 4];
            createSeat(flight.getFlightId(), row + letter, Seat.SeatClass.FIRST,
                    determineSeatType(i % 4), flight.getBasePrice() * 3);
        }

        // Business class
        int businessStart = (firstClassSeats / 4) + 1;
        for (int i = 0; i < businessSeats; i++) {
            int row = (i / 4) + businessStart;
            String letter = seatLetters[i % 4];
            createSeat(flight.getFlightId(), row + letter, Seat.SeatClass.BUSINESS,
                    determineSeatType(i % 4), flight.getBasePrice() * 2);
        }

        // Economy class
        int economyStart = businessStart + (businessSeats / 4) + 1;
        for (int i = 0; i < economySeats; i++) {
            int row = (i / 6) + economyStart;
            String letter = seatLetters[i % 6];
            createSeat(flight.getFlightId(), row + letter, Seat.SeatClass.ECONOMY,
                    determineSeatType(i % 6), flight.getBasePrice());
        }

        // Update available seats count
        flight.setAvailableSeats(aircraft.getTotalSeats());
        flightDAO.updateFlight(flight);
    }

    private void createSeat(int flightId, String seatNumber, Seat.SeatClass seatClass,
            Seat.SeatType seatType, double price) {
        Seat seat = new Seat(seatNumber, seatClass, seatType, price, flightId);
        seatDAO.createSeat(seat);
    }

    private Seat.SeatType determineSeatType(int position) {
        if (position == 0 || position == 5) {
            return Seat.SeatType.WINDOW;
        } else if (position == 2 || position == 3) {
            return Seat.SeatType.MIDDLE;
        } else {
            return Seat.SeatType.AISLE;
        }
    }
}
