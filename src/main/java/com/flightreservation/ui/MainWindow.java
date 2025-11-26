package com.flightreservation.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Main application window for the Flight Reservation System.
 */
public class MainWindow extends JFrame {
    private JTable flightTable;
    private DefaultTableModel tableModel;
    private JTextField originField;
    private JTextField destinationField;
    private JButton searchButton;
    private JButton bookButton;

    public MainWindow() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Flight Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Flight table
        JScrollPane tableScrollPane = createFlightTable();
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Flights"));

        searchPanel.add(new JLabel("Origin:"));
        originField = new JTextField(15);
        searchPanel.add(originField);

        searchPanel.add(new JLabel("Destination:"));
        destinationField = new JTextField(15);
        searchPanel.add(destinationField);

        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchFlights());
        searchPanel.add(searchButton);

        return searchPanel;
    }

    private JScrollPane createFlightTable() {
        String[] columnNames = {"Flight #", "Origin", "Destination", "Departure", "Arrival", "Seats", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightTable = new JTable(tableModel);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightTable.getTableHeader().setReorderingAllowed(false);

        // Add sample data for demonstration
        addSampleData();

        return new JScrollPane(flightTable);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        bookButton = new JButton("Book Selected Flight");
        bookButton.addActionListener(e -> bookFlight());
        buttonPanel.add(bookButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshFlights());
        buttonPanel.add(refreshButton);

        return buttonPanel;
    }

    private void addSampleData() {
        // Sample flight data for demonstration purposes
        // In production, this data would be loaded from the database
        Object[][] sampleData = {
            {"FL001", "New York", "Los Angeles", "2024-01-15 08:00", "2024-01-15 11:30", 150, "$299.99"},
            {"FL002", "Chicago", "Miami", "2024-01-15 10:00", "2024-01-15 14:00", 120, "$249.99"},
            {"FL003", "Seattle", "Denver", "2024-01-15 12:00", "2024-01-15 15:00", 100, "$199.99"},
            {"FL004", "Boston", "San Francisco", "2024-01-15 14:00", "2024-01-15 18:30", 80, "$349.99"},
            {"FL005", "Dallas", "Atlanta", "2024-01-15 16:00", "2024-01-15 19:00", 200, "$179.99"}
        };

        for (Object[] row : sampleData) {
            tableModel.addRow(row);
        }
    }

    private void searchFlights() {
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();

        if (origin.isEmpty() && destination.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter origin or destination to search.",
                "Search",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // In a real application, this would query the database
        JOptionPane.showMessageDialog(this,
            "Searching for flights from " + (origin.isEmpty() ? "any" : origin) + 
            " to " + (destination.isEmpty() ? "any" : destination),
            "Search",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void bookFlight() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a flight to book.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String flightNumber = (String) tableModel.getValueAt(selectedRow, 0);
        String origin = (String) tableModel.getValueAt(selectedRow, 1);
        String destination = (String) tableModel.getValueAt(selectedRow, 2);
        String price = (String) tableModel.getValueAt(selectedRow, 6);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Book flight " + flightNumber + " from " + origin + " to " + destination + " for " + price + "?",
            "Confirm Booking",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                "Flight " + flightNumber + " booked successfully!",
                "Booking Confirmed",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshFlights() {
        // In a real application, this would reload data from the database
        JOptionPane.showMessageDialog(this,
            "Flight list refreshed.",
            "Refresh",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
