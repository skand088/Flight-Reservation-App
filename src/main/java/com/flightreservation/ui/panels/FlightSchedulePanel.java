package com.flightreservation.ui.panels;

import com.flightreservation.dao.FlightDAO;
import com.flightreservation.model.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for viewing flight schedules with filtering options
 */
public class FlightSchedulePanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(FlightSchedulePanel.class);

    private final FlightDAO flightDAO;

    private JTextField originField;
    private JTextField destinationField;
    private JTextField dateField;
    private JComboBox<String> statusCombo;
    private JTable flightsTable;
    private DefaultTableModel tableModel;
    private List<Flight> allFlights;

    public FlightSchedulePanel() {
        this.flightDAO = new FlightDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        loadAllFlights();
    }

    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel("Flight Schedule", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(10, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // Filter panel
        JPanel filterPanel = createFilterPanel();
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // Flights table
        String[] columnNames = {"Flight #", "Airline", "Origin", "Destination",
                "Departure", "Arrival", "Duration", "Base Price", "Available Seats", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightsTable = new JTable(tableModel);
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightsTable.setRowHeight(25);
        flightsTable.getTableHeader().setReorderingAllowed(false);
        flightsTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(flightsTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Action buttons
        JPanel buttonPanel = createButtonPanel();
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));

        // First row
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.add(new JLabel("Origin:"));
        originField = new JTextField(10);
        row1.add(originField);

        row1.add(new JLabel("Destination:"));
        destinationField = new JTextField(10);
        row1.add(destinationField);

        row1.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(12);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        row1.add(dateField);

        panel.add(row1);

        // Second row
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{
                "ALL", "SCHEDULED", "BOARDING", "DEPARTED", "ARRIVED", "DELAYED", "CANCELLED"
        });
        row2.add(statusCombo);

        JButton searchBtn = new JButton("Search Flights");
        searchBtn.setBackground(new Color(40, 167, 69));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> searchFlights());
        row2.add(searchBtn);

        JButton clearBtn = new JButton("Clear Filters");
        clearBtn.setBackground(new Color(108, 117, 125));
        clearBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> clearFilters());
        row2.add(clearBtn);

        JButton refreshBtn = new JButton("Refresh All");
        refreshBtn.setBackground(new Color(0, 123, 255));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadAllFlights());
        row2.add(refreshBtn);

        panel.add(row2);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton viewDetailsBtn = new JButton("View Flight Details");
        viewDetailsBtn.setBackground(new Color(0, 123, 255));
        viewDetailsBtn.setForeground(Color.WHITE);
        viewDetailsBtn.setFocusPainted(false);
        viewDetailsBtn.addActionListener(e -> viewFlightDetails());
        panel.add(viewDetailsBtn);

        return panel;
    }

    private void loadAllFlights() {
        tableModel.setRowCount(0);
        allFlights = flightDAO.getAllFlights();

        if (allFlights.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No flights found in the system.",
                    "No Data",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        displayFlights(allFlights);
        logger.info("Loaded {} flights", allFlights.size());
    }

    private void searchFlights() {
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        String dateStr = dateField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        try {
            tableModel.setRowCount(0);
            List<Flight> results;

            if (!origin.isEmpty() && !destination.isEmpty() && !dateStr.isEmpty()) {
                LocalDate date = LocalDate.parse(dateStr);
                results = flightDAO.searchFlights(origin, destination, date.atStartOfDay());
            } else {
                results = flightDAO.getAllFlights();
            }

            // Filter by status if not ALL
            if (!"ALL".equals(status)) {
                results.removeIf(f -> !f.getStatus().toString().equals(status));
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No flights found matching the search criteria.",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            displayFlights(results);
            logger.info("Search returned {} flights", results.size());
        } catch (Exception e) {
            logger.error("Error searching flights", e);
            JOptionPane.showMessageDialog(this,
                    "Error searching flights: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFilters() {
        originField.setText("");
        destinationField.setText("");
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        statusCombo.setSelectedIndex(0);
        loadAllFlights();
    }

    private void displayFlights(List<Flight> flights) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd HH:mm");

        for (Flight flight : flights) {
            tableModel.addRow(new Object[]{
                    flight.getFlightNumber(),
                    flight.getAirline() != null ? flight.getAirline().getAirlineName() : "N/A",
                    flight.getRoute() != null ? flight.getRoute().getOriginAirport() : "N/A",
                    flight.getRoute() != null ? flight.getRoute().getDestinationAirport() : "N/A",
                    flight.getDepartureTime().format(formatter),
                    flight.getArrivalTime().format(formatter),
                    flight.getDuration() + " min",
                    "$" + String.format("%.2f", flight.getBasePrice()),
                    flight.getAvailableSeats(),
                    flight.getStatus()
            });
        }
    }

    private void viewFlightDetails() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a flight to view details.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedIndex = selectedRow;
        Flight flight = (selectedIndex >= 0 && selectedIndex < allFlights.size()) ? allFlights.get(selectedIndex) : null;

        if (flight == null) {
            JOptionPane.showMessageDialog(this,
                    "Error loading flight details.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        StringBuilder details = new StringBuilder();
        details.append("FLIGHT DETAILS\n");
        details.append("═".repeat(50)).append("\n\n");
        details.append("Flight Number: ").append(flight.getFlightNumber()).append("\n");
        details.append("Status: ").append(flight.getStatus()).append("\n\n");

        if (flight.getAirline() != null) {
            details.append("Airline: ").append(flight.getAirline().getAirlineName()).append("\n");
            details.append("Airline Code: ").append(flight.getAirline().getAirlineCode()).append("\n\n");
        }

        if (flight.getRoute() != null) {
            details.append("ROUTE INFORMATION\n");
            details.append("-".repeat(50)).append("\n");
            details.append("Origin: ").append(flight.getRoute().getOriginAirport()).append("\n");
            details.append("Destination: ").append(flight.getRoute().getDestinationAirport()).append("\n");
            details.append("Distance: ").append(flight.getRoute().getDistance()).append(" km\n\n");
        }

        details.append("SCHEDULE\n");
        details.append("-".repeat(50)).append("\n");
        details.append("Departure: ").append(flight.getDepartureTime().format(formatter)).append("\n");
        details.append("Arrival: ").append(flight.getArrivalTime().format(formatter)).append("\n");
        details.append("Duration: ").append(flight.getDuration()).append(" minutes\n\n");

        if (flight.getAircraft() != null) {
            details.append("AIRCRAFT\n");
            details.append("-".repeat(50)).append("\n");
            details.append("Model: ").append(flight.getAircraft().getModel()).append("\n");
            details.append("Manufacturer: ").append(flight.getAircraft().getManufacturer()).append("\n");
            details.append("Total Seats: ").append(flight.getAircraft().getTotalSeats()).append("\n\n");
        }

        details.append("PRICING & AVAILABILITY\n");
        details.append("-".repeat(50)).append("\n");
        details.append("Base Price: $").append(String.format("%.2f", flight.getBasePrice())).append("\n");
        details.append("Available Seats: ").append(flight.getAvailableSeats()).append("\n");

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 500));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "Flight Details - " + flight.getFlightNumber(),
                JOptionPane.INFORMATION_MESSAGE);
    }
}
