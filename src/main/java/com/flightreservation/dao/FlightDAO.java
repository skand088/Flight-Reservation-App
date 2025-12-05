package com.flightreservation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flightreservation.database.DatabaseManager;
import com.flightreservation.model.entities.Aircraft;
import com.flightreservation.model.entities.Airline;
import com.flightreservation.model.entities.Flight;
import com.flightreservation.model.entities.Route;

public class FlightDAO {
    private static final Logger logger = LoggerFactory.getLogger(FlightDAO.class);

    public List<Flight> searchFlights(String origin, String destination, LocalDateTime departureDate) {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT f.*, r.origin_airport, r.destination_airport, " +
                "a.model as aircraft_model, a.manufacturer, " +
                "al.airline_name, al.airline_code " +
                "FROM flights f " +
                "JOIN routes r ON f.route_id = r.route_id " +
                "JOIN aircraft a ON f.aircraft_id = a.aircraft_id " +
                "JOIN airlines al ON f.airline_id = al.airline_id " +
                "WHERE r.origin_airport LIKE ? AND r.destination_airport LIKE ? " +
                "AND DATE(f.departure_time) = DATE(?) " +
                "AND f.status = 'SCHEDULED' " +
                "ORDER BY f.departure_time";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + origin + "%");
            stmt.setString(2, "%" + destination + "%");
            stmt.setTimestamp(3, Timestamp.valueOf(departureDate));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                flights.add(mapResultSetToFlight(rs));
            }
            logger.info("Found {} flights from {} to {} on {}", flights.size(), origin, destination,
                    departureDate.toLocalDate());
        } catch (SQLException e) {
            logger.error("Error searching flights", e);
        }
        return flights;
    }

    public Flight getFlightById(int flightId) {
        String sql = "SELECT f.*, r.origin_airport, r.destination_airport, r.distance, r.estimated_duration, " +
                "a.model as aircraft_model, a.manufacturer, a.total_seats, " +
                "al.airline_name, al.airline_code " +
                "FROM flights f " +
                "JOIN routes r ON f.route_id = r.route_id " +
                "JOIN aircraft a ON f.aircraft_id = a.aircraft_id " +
                "JOIN airlines al ON f.airline_id = al.airline_id " +
                "WHERE f.flight_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToFlight(rs);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving flight by ID: {}", flightId, e);
        }
        return null;
    }

    public List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT f.*, r.origin_airport, r.destination_airport, " +
                "a.model as aircraft_model, a.manufacturer, " +
                "al.airline_name, al.airline_code " +
                "FROM flights f " +
                "JOIN routes r ON f.route_id = r.route_id " +
                "JOIN aircraft a ON f.aircraft_id = a.aircraft_id " +
                "JOIN airlines al ON f.airline_id = al.airline_id " +
                "ORDER BY f.departure_time DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                flights.add(mapResultSetToFlight(rs));
            }
            logger.info("Retrieved {} flights", flights.size());
        } catch (SQLException e) {
            logger.error("Error retrieving all flights", e);
        }
        return flights;
    }

    public boolean createFlight(Flight flight) {
        String sql = "INSERT INTO flights (flight_number, departure_time, arrival_time, duration, " +
                "status, base_price, available_seats, aircraft_id, route_id, airline_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, flight.getFlightNumber());
            stmt.setTimestamp(2, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(flight.getArrivalTime()));
            stmt.setInt(4, flight.getDuration());
            stmt.setString(5, flight.getStatus().name());
            stmt.setDouble(6, flight.getBasePrice());
            stmt.setInt(7, flight.getAvailableSeats());
            stmt.setInt(8, flight.getAircraftId());
            stmt.setInt(9, flight.getRouteId());
            stmt.setInt(10, flight.getAirlineId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    flight.setFlightId(generatedKeys.getInt(1));
                }
                logger.info("Created flight: {}", flight.getFlightNumber());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating flight", e);
        }
        return false;
    }

    public boolean updateFlight(Flight flight) {
        String sql = "UPDATE flights SET flight_number = ?, departure_time = ?, arrival_time = ?, " +
                "duration = ?, status = ?, base_price = ?, available_seats = ?, " +
                "aircraft_id = ?, route_id = ?, airline_id = ? WHERE flight_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, flight.getFlightNumber());
            stmt.setTimestamp(2, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(flight.getArrivalTime()));
            stmt.setInt(4, flight.getDuration());
            stmt.setString(5, flight.getStatus().name());
            stmt.setDouble(6, flight.getBasePrice());
            stmt.setInt(7, flight.getAvailableSeats());
            stmt.setInt(8, flight.getAircraftId());
            stmt.setInt(9, flight.getRouteId());
            stmt.setInt(10, flight.getAirlineId());
            stmt.setInt(11, flight.getFlightId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Updated flight: {}", flight.getFlightNumber());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating flight", e);
        }
        return false;
    }

    public boolean deleteFlight(int flightId) {
        String sql = "DELETE FROM flights WHERE flight_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, flightId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Deleted flight ID: {}", flightId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting flight", e);
        }
        return false;
    }

    public boolean updateAvailableSeats(int flightId, int seatChange) {
        String sql = "UPDATE flights SET available_seats = available_seats + ? WHERE flight_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, seatChange);
            stmt.setInt(2, flightId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating available seats", e);
        }
        return false;
    }

    private Flight mapResultSetToFlight(ResultSet rs) throws SQLException {
        Flight flight = new Flight();
        flight.setFlightId(rs.getInt("flight_id"));
        flight.setFlightNumber(rs.getString("flight_number"));

        Timestamp departure = rs.getTimestamp("departure_time");
        if (departure != null) {
            flight.setDepartureTime(departure.toLocalDateTime());
        }

        Timestamp arrival = rs.getTimestamp("arrival_time");
        if (arrival != null) {
            flight.setArrivalTime(arrival.toLocalDateTime());
        }

        flight.setDuration(rs.getInt("duration"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            flight.setStatus(Flight.FlightStatus.valueOf(statusStr));
        }

        flight.setBasePrice(rs.getDouble("base_price"));
        flight.setAvailableSeats(rs.getInt("available_seats"));
        flight.setAircraftId(rs.getInt("aircraft_id"));
        flight.setRouteId(rs.getInt("route_id"));
        flight.setAirlineId(rs.getInt("airline_id"));

        try {
            Route route = new Route();
            route.setRouteId(rs.getInt("route_id"));
            route.setOriginAirport(rs.getString("origin_airport"));
            route.setDestinationAirport(rs.getString("destination_airport"));
            flight.setRoute(route);
        } catch (SQLException e) {
        }

        try {
            Aircraft aircraft = new Aircraft();
            aircraft.setAircraftId(rs.getInt("aircraft_id"));
            aircraft.setModel(rs.getString("aircraft_model"));
            aircraft.setManufacturer(rs.getString("manufacturer"));
            flight.setAircraft(aircraft);
        } catch (SQLException e) {
        }

        try {
            Airline airline = new Airline();
            airline.setAirlineId(rs.getInt("airline_id"));
            airline.setAirlineName(rs.getString("airline_name"));
            airline.setAirlineCode(rs.getString("airline_code"));
            flight.setAirline(airline);
        } catch (SQLException e) {
        }

        return flight;
    }
}
