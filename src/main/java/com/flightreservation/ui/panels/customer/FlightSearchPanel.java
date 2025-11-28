package com.flightreservation.ui.panels.customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.flightreservation.controller.FlightSearchController;
import com.flightreservation.dao.CustomerDAO;
import com.flightreservation.model.entities.Customer;
import com.flightreservation.model.entities.Flight;
import com.flightreservation.model.entities.User;
import com.flightreservation.ui.dialogs.BookingDialog;
import com.flightreservation.util.SessionManager;

public class FlightSearchPanel extends JPanel {
    private final FlightSearchController controller;
    private JTextField originField;
    private JTextField destinationField;
    private JTextField dateField;
    private JLabel originErrorLabel;
    private JLabel destinationErrorLabel;
    private JLabel dateErrorLabel;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private FlightSelectionListener listener;
    private List<Flight> currentFlights;

    public interface FlightSelectionListener {
        void onFlightSelected(Flight flight);
    }

    public FlightSearchPanel() {
        this.controller = new FlightSearchController();
        this.currentFlights = new ArrayList<>();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        JPanel resultsPanel = createResultsPanel();
        add(resultsPanel, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Search Flights"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel fromLabel = new JLabel("From:*");
        fromLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(fromLabel, gbc);

        gbc.gridx = 1;
        originField = new JTextField(15);
        originField.setToolTipText("Enter origin airport code (e.g., JFK, LAX)");
        originField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                validateOrigin();
            }
        });
        panel.add(originField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        originErrorLabel = new JLabel(" ");
        originErrorLabel.setForeground(Color.RED);
        originErrorLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        panel.add(originErrorLabel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        JLabel toLabel = new JLabel("To:*");
        toLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(toLabel, gbc);

        gbc.gridx = 3;
        destinationField = new JTextField(15);
        destinationField.setToolTipText("Enter destination airport code (e.g., LAX, ORD)");
        destinationField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                validateDestination();
            }
        });
        panel.add(destinationField, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        destinationErrorLabel = new JLabel(" ");
        destinationErrorLabel.setForeground(Color.RED);
        destinationErrorLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        panel.add(destinationErrorLabel, gbc);

        gbc.gridx = 4;
        gbc.gridy = 0;
        JLabel dateLabel = new JLabel("Date:*");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(dateLabel, gbc);

        gbc.gridx = 5;
        dateField = new JTextField(10);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        dateField.setToolTipText("Format: YYYY-MM-DD (e.g., 2024-12-25)");

        ((AbstractDocument) dateField.getDocument()).setDocumentFilter(new DateDocumentFilter());

        dateField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                validateDate();
            }
        });
        panel.add(dateField, gbc);

        gbc.gridx = 5;
        gbc.gridy = 1;
        dateErrorLabel = new JLabel(" ");
        dateErrorLabel.setForeground(Color.RED);
        dateErrorLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        panel.add(dateErrorLabel, gbc);

        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        JButton searchButton = new JButton("Search Flights");
        searchButton.setBackground(new Color(33, 147, 176));
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> searchFlights());
        panel.add(searchButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 7;
        gbc.gridheight = 1;
        JLabel infoLabel = new JLabel(
                "* Required fields | Date format: YYYY-MM-DD | Airport codes: JFK, LAX, ORD, MIA");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel, gbc);

        return panel;
    }

    private boolean validateOrigin() {
        String origin = originField.getText().trim();
        if (origin.isEmpty()) {
            originErrorLabel.setText("Origin is required");
            originField.setBorder(BorderFactory.createLineBorder(Color.RED));
            return false;
        } else if (origin.length() < 2) {
            originErrorLabel.setText("Enter at least 2 characters");
            originField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
            return false;
        } else {
            originErrorLabel.setText(" ");
            originField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
            return true;
        }
    }

    private boolean validateDestination() {
        String destination = destinationField.getText().trim();
        String origin = originField.getText().trim();

        if (destination.isEmpty()) {
            destinationErrorLabel.setText("Destination is required");
            destinationField.setBorder(BorderFactory.createLineBorder(Color.RED));
            return false;
        } else if (destination.length() < 2) {
            destinationErrorLabel.setText("Enter at least 2 characters");
            destinationField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
            return false;
        } else if (!origin.isEmpty() && destination.equalsIgnoreCase(origin)) {
            destinationErrorLabel.setText("Destination must differ from origin");
            destinationField.setBorder(BorderFactory.createLineBorder(Color.RED));
            return false;
        } else {
            destinationErrorLabel.setText(" ");
            destinationField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
            return true;
        }
    }

    private boolean validateDate() {
        String dateStr = dateField.getText().trim();

        if (dateStr.isEmpty()) {
            dateErrorLabel.setText("Date is required");
            dateField.setBorder(BorderFactory.createLineBorder(Color.RED));
            return false;
        }

        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate today = LocalDate.now();

            if (date.isBefore(today)) {
                dateErrorLabel.setText("Date cannot be in the past");
                dateField.setBorder(BorderFactory.createLineBorder(Color.RED));
                return false;
            } else {
                dateErrorLabel.setText(" ");
                dateField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
                return true;
            }
        } catch (DateTimeParseException e) {
            dateErrorLabel.setText("Invalid format. Use YYYY-MM-DD");
            dateField.setBorder(BorderFactory.createLineBorder(Color.RED));
            return false;
        }
    }

    private class DateDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.insert(offset, string);

            if (isValidPartialDate(sb.toString())) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.replace(offset, offset + length, text);

            if (isValidPartialDate(sb.toString())) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValidPartialDate(String text) {

            if (text.isEmpty())
                return true;

            if (!text.matches("[0-9\\-]*"))
                return false;

            if (text.length() > 10)
                return false;

            return true;
        }
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Search Results"));

        String[] columns = { "Flight #", "Airline", "From", "To", "Departure", "Arrival", "Duration", "Price",
                "Seats" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookButton = new JButton("Book Flight");
        bookButton.addActionListener(e -> bookSelectedFlight());
        buttonPanel.add(bookButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void searchFlights() {

        boolean originValid = validateOrigin();
        boolean destinationValid = validateDestination();
        boolean dateValid = validateDate();

        if (!originValid || !destinationValid || !dateValid) {
            JOptionPane.showMessageDialog(this,
                    "Please correct the errors in the form before searching.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        String dateStr = dateField.getText().trim();

        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDateTime departureDateTime = date.atStartOfDay();

            tableModel.setRowCount(0);

            JLabel loadingLabel = new JLabel("Searching flights...", SwingConstants.CENTER);
            loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));

            SwingWorker<List<Flight>, Void> worker = new SwingWorker<>() {
                @Override
                protected List<Flight> doInBackground() throws Exception {
                    try {
                        return controller.searchFlights(origin, destination, departureDateTime);
                    } catch (Exception e) {
                        throw new Exception("Database error: " + e.getMessage(), e);
                    }
                }

                @Override
                protected void done() {
                    try {
                        List<Flight> flights = get();
                        displayResults(flights);

                        if (flights.isEmpty()) {
                            JOptionPane.showMessageDialog(FlightSearchPanel.this,
                                    "No flights found for:\n" +
                                            "From: " + origin + "\n" +
                                            "To: " + destination + "\n" +
                                            "Date: " + dateStr + "\n\n" +
                                            "Try searching for:\n" +
                                            "• JFK to LAX\n" +
                                            "• LAX to ORD\n" +
                                            "• ORD to MIA",
                                    "No Results",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                        tableModel.setRowCount(0);
                        JOptionPane.showMessageDialog(FlightSearchPanel.this,
                                "Error searching flights:\n" + ex.getMessage() +
                                        "\n\nPlease check:\n" +
                                        "1. Database connection is active\n" +
                                        "2. Search terms are valid\n" +
                                        "3. Date format is YYYY-MM-DD",
                                "Search Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();

        } catch (DateTimeParseException ex) {
            dateErrorLabel.setText("Invalid date format");
            dateField.setBorder(BorderFactory.createLineBorder(Color.RED));
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD\n" +
                            "Examples:\n" +
                            "• 2024-12-25\n" +
                            "• 2025-01-15",
                    "Date Format Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void displayResults(List<Flight> flights) {
        tableModel.setRowCount(0);
        currentFlights = flights;

        if (flights == null || flights.isEmpty()) {
            return;
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm");

        for (Flight flight : flights) {
            String flightNumber = flight.getFlightNumber() != null ? flight.getFlightNumber() : "N/A";
            String airline = flight.getAirline() != null ? flight.getAirline().getAirlineName() : "Unknown";
            String from = flight.getRoute() != null ? flight.getRoute().getOriginAirport() : "N/A";
            String to = flight.getRoute() != null ? flight.getRoute().getDestinationAirport() : "N/A";
            String departure = flight.getDepartureTime() != null ? flight.getDepartureTime().format(dateTimeFormatter)
                    : "N/A";
            String arrival = flight.getArrivalTime() != null ? flight.getArrivalTime().format(dateTimeFormatter)
                    : "N/A";
            String duration = flight.getDuration() + " min";
            String price = String.format("$%.2f", flight.getBasePrice());
            String seats = String.valueOf(flight.getAvailableSeats());

            Object[] row = { flightNumber, airline, from, to, departure, arrival, duration, price, seats };
            tableModel.addRow(row);
        }
    }

    private void bookSelectedFlight() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a flight from the table to book.",
                    "No Flight Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentFlights == null || currentFlights.isEmpty() || selectedRow >= currentFlights.size()) {
            JOptionPane.showMessageDialog(this,
                    "Unable to retrieve flight information.\nPlease search again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Flight selectedFlight = currentFlights.get(selectedRow);

        SessionManager sessionManager = SessionManager.getInstance();
        User currentUser = sessionManager.getCurrentUser();

        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "Please log in to book a flight.",
                    "Login Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = customerDAO.getCustomerByUserId(currentUser.getUserId());

        if (customer == null) {
            JOptionPane.showMessageDialog(this,
                    "Customer profile not found. Please contact support.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        BookingDialog bookingDialog = new BookingDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                selectedFlight,
                customer);
        bookingDialog.setVisible(true);

        if (listener != null && bookingDialog.isBookingConfirmed()) {
            listener.onFlightSelected(selectedFlight);
        }
    }

    public void setFlightSelectionListener(FlightSelectionListener listener) {
        this.listener = listener;
    }

    public List<Flight> getCurrentSearchResults() {
        return new ArrayList<>(currentFlights);
    }
}
