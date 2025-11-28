package com.flightreservation.ui.panels.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import com.flightreservation.controller.AdminController;
import com.flightreservation.model.entities.Aircraft;
import com.flightreservation.model.entities.Airline;
import com.flightreservation.model.entities.Flight;
import com.flightreservation.model.entities.Route;

public class FlightManagementPanel extends JPanel {
    private final AdminController controller;
    private JTable flightsTable;
    private DefaultTableModel tableModel;
    private List<Flight> currentFlights;

    public FlightManagementPanel() {
        this.controller = new AdminController();
        initializeUI();
        loadFlights();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Flight Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = { "Flight #", "Airline", "Route", "Departure", "Arrival", "Status", "Price", "Seats" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightsTable = new JTable(tableModel);
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(flightsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadFlights());
        buttonPanel.add(refreshButton);

        JButton addButton = new JButton("Add Flight");
        addButton.setBackground(new Color(76, 175, 80));
        addButton.addActionListener(e -> addFlight());
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Edit Flight");
        editButton.setBackground(new Color(33, 150, 243));
        editButton.addActionListener(e -> editFlight());
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Flight");
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.addActionListener(e -> deleteFlight());
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFlights() {
        SwingWorker<List<Flight>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Flight> doInBackground() throws Exception {
                return controller.getAllFlights();
            }

            @Override
            protected void done() {
                try {
                    currentFlights = get();
                    displayFlights(currentFlights);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FlightManagementPanel.this,
                            "Error loading flights: " + ex.getMessage(),
                            "Load Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void displayFlights(List<Flight> flights) {
        tableModel.setRowCount(0);

        if (flights.isEmpty()) {
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Flight flight : flights) {
            String airline = flight.getAirline() != null ? flight.getAirline().getAirlineName() : "N/A";
            String route = "N/A";
            if (flight.getRoute() != null) {
                route = flight.getRoute().getOriginAirport() + " → " + flight.getRoute().getDestinationAirport();
            }
            String departure = flight.getDepartureTime() != null ? flight.getDepartureTime().format(formatter) : "N/A";
            String arrival = flight.getArrivalTime() != null ? flight.getArrivalTime().format(formatter) : "N/A";

            Object[] row = {
                    flight.getFlightNumber(),
                    airline,
                    route,
                    departure,
                    arrival,
                    flight.getStatus().name(),
                    String.format("$%.2f", flight.getBasePrice()),
                    flight.getAvailableSeats()
            };
            tableModel.addRow(row);
        }
    }

    private void addFlight() {
        FlightDialog dialog = new FlightDialog(null, controller);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            loadFlights();
        }
    }

    private void editFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a flight to edit",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Flight flight = currentFlights.get(selectedRow);
        FlightDialog dialog = new FlightDialog(flight, controller);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            loadFlights();
        }
    }

    private void deleteFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a flight to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Flight flight = currentFlights.get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete flight " + flight.getFlightNumber() + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return controller.deleteFlight(flight.getFlightId());
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(FlightManagementPanel.this,
                                    "Flight deleted successfully",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            loadFlights();
                        } else {
                            JOptionPane.showMessageDialog(FlightManagementPanel.this,
                                    "Failed to delete flight",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(FlightManagementPanel.this,
                                "Error deleting flight: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    private static class FlightDialog extends JDialog {
        private final AdminController controller;
        private final Flight flight;
        private boolean confirmed = false;

        private JTextField flightNumberField;
        private JComboBox<String> routeComboBox;
        private JComboBox<String> aircraftComboBox;
        private JComboBox<String> airlineComboBox;
        private JTextField departureDateField;
        private JTextField departureTimeField;
        private JTextField arrivalDateField;
        private JTextField arrivalTimeField;
        private JTextField priceField;
        private JComboBox<Flight.FlightStatus> statusComboBox;

        private List<Route> routes;
        private List<Aircraft> aircrafts;
        private List<Airline> airlines;

        public FlightDialog(Flight flight, AdminController controller) {
            this.flight = flight;
            this.controller = controller;

            setTitle(flight == null ? "Add Flight" : "Edit Flight");
            setModal(true);
            setSize(500, 600);
            setLocationRelativeTo(null);

            initializeUI();

            if (flight != null) {
                loadFlightData();
            }
        }

        private void initializeUI() {
            setLayout(new BorderLayout(10, 10));

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;

            formPanel.add(new JLabel("Flight Number:"), gbc);
            gbc.gridx = 1;
            flightNumberField = new JTextField(20);
            formPanel.add(flightNumberField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Route:"), gbc);
            gbc.gridx = 1;
            routeComboBox = new JComboBox<>();
            loadRoutes();
            formPanel.add(routeComboBox, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Aircraft:"), gbc);
            gbc.gridx = 1;
            aircraftComboBox = new JComboBox<>();
            loadAircraft();
            formPanel.add(aircraftComboBox, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Airline:"), gbc);
            gbc.gridx = 1;
            airlineComboBox = new JComboBox<>();
            loadAirlines();
            formPanel.add(airlineComboBox, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Departure Date (YYYY-MM-DD):"), gbc);
            gbc.gridx = 1;
            departureDateField = new JTextField(20);
            formPanel.add(departureDateField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Departure Time (HH:MM):"), gbc);
            gbc.gridx = 1;
            departureTimeField = new JTextField(20);
            formPanel.add(departureTimeField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Arrival Date (YYYY-MM-DD):"), gbc);
            gbc.gridx = 1;
            arrivalDateField = new JTextField(20);
            formPanel.add(arrivalDateField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Arrival Time (HH:MM):"), gbc);
            gbc.gridx = 1;
            arrivalTimeField = new JTextField(20);
            formPanel.add(arrivalTimeField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Base Price:"), gbc);
            gbc.gridx = 1;
            priceField = new JTextField(20);
            formPanel.add(priceField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1;
            statusComboBox = new JComboBox<>(Flight.FlightStatus.values());
            formPanel.add(statusComboBox, gbc);

            add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> saveFlight());
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dispose());
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void loadRoutes() {
            routes = controller.getAllRoutes();
            for (Route route : routes) {
                routeComboBox.addItem(route.toString());
            }
        }

        private void loadAircraft() {
            aircrafts = controller.getAllAircraft();
            for (Aircraft aircraft : aircrafts) {
                aircraftComboBox.addItem(aircraft.toString());
            }
        }

        private void loadAirlines() {
            airlines = controller.getAllAirlines();
            for (Airline airline : airlines) {
                airlineComboBox.addItem(airline.getAirlineName());
            }
        }

        private void loadFlightData() {
            flightNumberField.setText(flight.getFlightNumber());

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            if (flight.getDepartureTime() != null) {
                departureDateField.setText(flight.getDepartureTime().format(dateFormatter));
                departureTimeField.setText(flight.getDepartureTime().format(timeFormatter));
            }

            if (flight.getArrivalTime() != null) {
                arrivalDateField.setText(flight.getArrivalTime().format(dateFormatter));
                arrivalTimeField.setText(flight.getArrivalTime().format(timeFormatter));
            }

            priceField.setText(String.valueOf(flight.getBasePrice()));
            statusComboBox.setSelectedItem(flight.getStatus());

            if (flight.getAirline() != null) {
                for (int i = 0; i < airlines.size(); i++) {
                    if (airlines.get(i).getAirlineId() == flight.getAirlineId()) {
                        airlineComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        private void saveFlight() {
            try {
                String flightNumber = flightNumberField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());

                String depDate = departureDateField.getText().trim() + "T" + departureTimeField.getText().trim();
                String arrDate = arrivalDateField.getText().trim() + "T" + arrivalTimeField.getText().trim();

                LocalDateTime departure = LocalDateTime.parse(depDate);
                LocalDateTime arrival = LocalDateTime.parse(arrDate);

                Flight newFlight = flight != null ? flight : new Flight();
                newFlight.setFlightNumber(flightNumber);
                newFlight.setDepartureTime(departure);
                newFlight.setArrivalTime(arrival);
                newFlight.setBasePrice(price);
                newFlight.setStatus((Flight.FlightStatus) statusComboBox.getSelectedItem());

                int routeIndex = routeComboBox.getSelectedIndex();
                if (routeIndex >= 0 && routeIndex < routes.size()) {
                    newFlight.setRouteId(routes.get(routeIndex).getRouteId());
                }

                int aircraftIndex = aircraftComboBox.getSelectedIndex();
                if (aircraftIndex >= 0 && aircraftIndex < aircrafts.size()) {
                    newFlight.setAircraftId(aircrafts.get(aircraftIndex).getAircraftId());
                    newFlight.setAvailableSeats(aircrafts.get(aircraftIndex).getTotalSeats());
                }

                int airlineIndex = airlineComboBox.getSelectedIndex();
                if (airlineIndex >= 0 && airlineIndex < airlines.size()) {
                    newFlight.setAirlineId(airlines.get(airlineIndex).getAirlineId());
                } else {
                    throw new IllegalArgumentException("Airline must be selected");
                }

                long duration = java.time.Duration.between(departure, arrival).toMinutes();
                newFlight.setDuration((int) duration);

                boolean success;
                if (flight == null) {
                    success = controller.createFlight(newFlight);
                } else {
                    success = controller.updateFlight(newFlight);
                }

                if (success) {
                    confirmed = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to save flight",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid input: " + ex.getMessage(),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isConfirmed() {
            return confirmed;
        }
    }
}
