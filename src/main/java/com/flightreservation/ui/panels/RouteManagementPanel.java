package com.flightreservation.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.flightreservation.dao.RouteDAO;
import com.flightreservation.model.Route;

/**
 * Panel for managing routes (Admin view)
 */
public class RouteManagementPanel extends JPanel {
    private final RouteDAO routeDAO;
    private JTable routesTable;
    private DefaultTableModel tableModel;

    public RouteManagementPanel() {
        this.routeDAO = new RouteDAO();
        initializeUI();
        loadRoutes();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("🗺 Route Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        add(createTablePanel(), BorderLayout.CENTER);

        // Buttons
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("All Routes"));

        String[] columns = { "Route ID", "Origin", "Destination", "Distance (mi)", "Duration (min)" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        routesTable = new JTable(tableModel);
        routesTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(routesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton addButton = new JButton("➕ Add Route");
        addButton.addActionListener(e -> addRoute());
        panel.add(addButton);

        JButton editButton = new JButton("✏️ Edit Route");
        editButton.addActionListener(e -> editSelectedRoute());
        panel.add(editButton);

        JButton deleteButton = new JButton("🗑️ Delete Route");
        deleteButton.addActionListener(e -> deleteSelectedRoute());
        panel.add(deleteButton);

        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> loadRoutes());
        panel.add(refreshButton);

        return panel;
    }

    private void loadRoutes() {
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

    private Route getSelectedRoute() {
        int selectedRow = routesTable.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please select a route from the table.",
                    "No Selection",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int routeId = (int) tableModel.getValueAt(selectedRow, 0);
        return routeDAO.getRouteById(routeId);
    }

    private void addRoute() {
        JPanel addPanel = new JPanel(new java.awt.GridLayout(0, 2, 10, 10));

        javax.swing.JTextField originField = new javax.swing.JTextField();
        javax.swing.JTextField destinationField = new javax.swing.JTextField();
        javax.swing.JTextField distanceField = new javax.swing.JTextField();
        javax.swing.JTextField durationField = new javax.swing.JTextField();

        addPanel.add(new JLabel("Origin Airport Code:"));
        addPanel.add(originField);
        addPanel.add(new JLabel("Destination Airport Code:"));
        addPanel.add(destinationField);
        addPanel.add(new JLabel("Distance (miles):"));
        addPanel.add(distanceField);
        addPanel.add(new JLabel("Estimated Duration (minutes):"));
        addPanel.add(durationField);

        int result = javax.swing.JOptionPane.showConfirmDialog(this, addPanel,
                "Add New Route",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.PLAIN_MESSAGE);

        if (result == javax.swing.JOptionPane.OK_OPTION) {
            try {
                Route route = new Route();
                route.setOriginAirport(originField.getText().trim().toUpperCase());
                route.setDestinationAirport(destinationField.getText().trim().toUpperCase());
                route.setDistance(Integer.parseInt(distanceField.getText().trim()));
                route.setEstimatedDuration(Integer.parseInt(durationField.getText().trim()));

                if (routeDAO.createRoute(route)) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Route added successfully!",
                            "Success",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    loadRoutes();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Failed to add route.",
                            "Error",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Distance and Duration must be valid numbers.",
                        "Input Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedRoute() {
        Route route = getSelectedRoute();
        if (route == null)
            return;

        JPanel editPanel = new JPanel(new java.awt.GridLayout(0, 2, 10, 10));

        javax.swing.JTextField originField = new javax.swing.JTextField(route.getOriginAirport());
        javax.swing.JTextField destinationField = new javax.swing.JTextField(route.getDestinationAirport());
        javax.swing.JTextField distanceField = new javax.swing.JTextField(String.valueOf(route.getDistance()));
        javax.swing.JTextField durationField = new javax.swing.JTextField(String.valueOf(route.getEstimatedDuration()));

        editPanel.add(new JLabel("Origin Airport Code:"));
        editPanel.add(originField);
        editPanel.add(new JLabel("Destination Airport Code:"));
        editPanel.add(destinationField);
        editPanel.add(new JLabel("Distance (miles):"));
        editPanel.add(distanceField);
        editPanel.add(new JLabel("Estimated Duration (minutes):"));
        editPanel.add(durationField);

        int result = javax.swing.JOptionPane.showConfirmDialog(this, editPanel,
                "Edit Route",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.PLAIN_MESSAGE);

        if (result == javax.swing.JOptionPane.OK_OPTION) {
            try {
                route.setOriginAirport(originField.getText().trim().toUpperCase());
                route.setDestinationAirport(destinationField.getText().trim().toUpperCase());
                route.setDistance(Integer.parseInt(distanceField.getText().trim()));
                route.setEstimatedDuration(Integer.parseInt(durationField.getText().trim()));

                if (routeDAO.updateRoute(route)) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Route updated successfully!",
                            "Success",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    loadRoutes();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Failed to update route.",
                            "Error",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Distance and Duration must be valid numbers.",
                        "Input Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedRoute() {
        Route route = getSelectedRoute();
        if (route == null)
            return;

        int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this route?\n" +
                        route.getOriginAirport() + " → " + route.getDestinationAirport(),
                "Confirm Delete",
                javax.swing.JOptionPane.YES_NO_OPTION);

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            if (routeDAO.deleteRoute(route.getRouteId())) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Route deleted successfully!",
                        "Success",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                loadRoutes();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Failed to delete route. It may be used by existing flights.",
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
