package com.flightreservation.dao;

import com.flightreservation.database.DatabaseManager;
import com.flightreservation.model.entities.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RouteDAO {
    private static final Logger logger = LoggerFactory.getLogger(RouteDAO.class);

    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT * FROM routes ORDER BY origin_airport, destination_airport";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                routes.add(mapResultSetToRoute(rs));
            }
            logger.info("Retrieved {} routes", routes.size());
        } catch (SQLException e) {
            logger.error("Error retrieving all routes", e);
        }
        return routes;
    }

    public Route getRouteById(int routeId) {
        String sql = "SELECT * FROM routes WHERE route_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, routeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRoute(rs);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving route by ID: {}", routeId, e);
        }
        return null;
    }

    public boolean createRoute(Route route) {
        String sql = "INSERT INTO routes (origin_airport, destination_airport, distance, estimated_duration) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, route.getOriginAirport());
            stmt.setString(2, route.getDestinationAirport());
            stmt.setInt(3, route.getDistance());
            stmt.setInt(4, route.getEstimatedDuration());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    route.setRouteId(generatedKeys.getInt(1));
                }
                logger.info("Created route: {} -> {}", route.getOriginAirport(), route.getDestinationAirport());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating route", e);
        }
        return false;
    }

    public boolean updateRoute(Route route) {
        String sql = "UPDATE routes SET origin_airport = ?, destination_airport = ?, distance = ?, " +
                "estimated_duration = ? WHERE route_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, route.getOriginAirport());
            stmt.setString(2, route.getDestinationAirport());
            stmt.setInt(3, route.getDistance());
            stmt.setInt(4, route.getEstimatedDuration());
            stmt.setInt(5, route.getRouteId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Updated route ID: {}", route.getRouteId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating route", e);
        }
        return false;
    }

    public boolean deleteRoute(int routeId) {
        String sql = "DELETE FROM routes WHERE route_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, routeId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Deleted route ID: {}", routeId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting route", e);
        }
        return false;
    }

    private Route mapResultSetToRoute(ResultSet rs) throws SQLException {
        Route route = new Route();
        route.setRouteId(rs.getInt("route_id"));
        route.setOriginAirport(rs.getString("origin_airport"));
        route.setDestinationAirport(rs.getString("destination_airport"));
        route.setDistance(rs.getInt("distance"));
        route.setEstimatedDuration(rs.getInt("estimated_duration"));
        return route;
    }
}
