package com.flightreservation.ui.panels.admin;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.flightreservation.dao.RouteDAO;
import com.flightreservation.ui.panels.common.BaseManagementPanel;
import com.flightreservation.model.entities.Route;
import com.flightreservation.ui.util.FormBuilder;
import com.flightreservation.ui.util.TableFactory;

public class RouteManagementPanel extends BaseManagementPanel<Route> {
    private final RouteDAO routeDAO;

    public RouteManagementPanel() {
        this.routeDAO = new RouteDAO();
    }

    @Override
    protected String getTitle() {
        return "Route Management";
    }

    @Override
    protected String getTableTitle() {
        return "All Routes";
    }

    @Override
    protected String getEntityName() {
        return "Route";
    }

    @Override
    protected JTable createTable() {
        String[] columns = { "Route ID", "Origin", "Destination", "Distance (mi)", "Duration (min)" };
        return TableFactory.createReadOnlyTable(columns);
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        List<Route> routes = routeDAO.getAllRoutes();

        for (Route route : routes) {
            Object[] row = {
                    route.getRouteId(),
                    route.getOriginAirport(),
                    route.getDestinationAirport(),
                    route.getDistance(),
                    route.getEstimatedDuration()
            };
            tableModel.addRow(row);
        }
    }

    @Override
    protected Route getItemFromRow(int row) {
        int routeId = (int) tableModel.getValueAt(row, 0);
        return routeDAO.getRouteById(routeId);
    }

    @Override
    protected void add() {
        FormBuilder formBuilder = new FormBuilder()
                .addTextField("Origin Airport Code", "origin")
                .addTextField("Destination Airport Code", "destination")
                .addTextField("Distance (miles)", "distance")
                .addTextField("Estimated Duration (minutes)", "duration");

        JPanel addPanel = formBuilder.build();

        int result = JOptionPane.showConfirmDialog(this, addPanel,
                "Add New Route",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Route route = new Route();
                route.setOriginAirport(formBuilder.getTextValue("origin").toUpperCase());
                route.setDestinationAirport(formBuilder.getTextValue("destination").toUpperCase());
                route.setDistance(Integer.parseInt(formBuilder.getTextValue("distance")));
                route.setEstimatedDuration(Integer.parseInt(formBuilder.getTextValue("duration")));

                if (routeDAO.createRoute(route)) {
                    JOptionPane.showMessageDialog(this,
                            "Route added successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to add route.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Distance and Duration must be valid numbers.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    protected void edit() {
        Route route = getSelectedItem();
        if (route == null)
            return;

        FormBuilder formBuilder = new FormBuilder()
                .addTextField("Origin Airport Code", "origin", route.getOriginAirport())
                .addTextField("Destination Airport Code", "destination", route.getDestinationAirport())
                .addTextField("Distance (miles)", "distance", String.valueOf(route.getDistance()))
                .addTextField("Estimated Duration (minutes)", "duration", String.valueOf(route.getEstimatedDuration()));

        JPanel editPanel = formBuilder.build();

        int result = JOptionPane.showConfirmDialog(this, editPanel,
                "Edit Route",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                route.setOriginAirport(formBuilder.getTextValue("origin").toUpperCase());
                route.setDestinationAirport(formBuilder.getTextValue("destination").toUpperCase());
                route.setDistance(Integer.parseInt(formBuilder.getTextValue("distance")));
                route.setEstimatedDuration(Integer.parseInt(formBuilder.getTextValue("duration")));

                if (routeDAO.updateRoute(route)) {
                    JOptionPane.showMessageDialog(this,
                            "Route updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update route.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Distance and Duration must be valid numbers.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    protected void delete() {
        Route route = getSelectedItem();
        if (route == null)
            return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this route?\n" +
                        route.getOriginAirport() + " → " + route.getDestinationAirport(),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (routeDAO.deleteRoute(route.getRouteId())) {
                JOptionPane.showMessageDialog(this,
                        "Route deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete route. It may be used by existing flights.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
