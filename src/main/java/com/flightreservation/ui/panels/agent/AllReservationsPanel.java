package com.flightreservation.ui.panels.agent;

import com.flightreservation.dao.ReservationDAO;
import com.flightreservation.model.entities.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AllReservationsPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(AllReservationsPanel.class);

    private final ReservationDAO reservationDAO;

    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;
    private List<Reservation> allReservations;

    public AllReservationsPanel() {
        this.reservationDAO = new ReservationDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        loadAllReservations();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("All Reservations", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(10, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        JPanel searchPanel = createSearchPanel();
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        String[] columnNames = { "Reservation ID", "Confirmation #", "Customer", "Flight #",
                "Departure", "Total Fare", "Status", "Booking Date" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationsTable = new JTable(tableModel);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationsTable.setRowHeight(25);
        reservationsTable.getTableHeader().setReorderingAllowed(false);
        reservationsTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));

        panel.add(new JLabel("Search (Confirmation #):"));
        searchField = new JTextField(20);
        panel.add(searchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(0, 123, 255));
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> searchReservations());
        panel.add(searchBtn);

        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Status:"));
        statusFilterCombo = new JComboBox<>(new String[] {
                "ALL", "PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"
        });
        statusFilterCombo.addActionListener(e -> filterByStatus());
        panel.add(statusFilterCombo);

        JButton refreshBtn = new JButton("Refresh All");
        refreshBtn.setBackground(new Color(108, 117, 125));
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadAllReservations());
        panel.add(refreshBtn);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.setBackground(new Color(0, 123, 255));
        viewDetailsBtn.setFocusPainted(false);
        viewDetailsBtn.addActionListener(e -> viewReservationDetails());
        panel.add(viewDetailsBtn);

        JButton cancelBtn = new JButton("Cancel Reservation");
        cancelBtn.setBackground(new Color(220, 53, 69));
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> cancelReservation());
        panel.add(cancelBtn);

        return panel;
    }

    private void loadAllReservations() {
        tableModel.setRowCount(0);
        allReservations = reservationDAO.getAllReservations();

        if (allReservations.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No reservations found in the system.",
                    "No Data",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        displayReservations(allReservations);
        logger.info("Loaded {} reservations", allReservations.size());
    }

    private void searchReservations() {
        String confirmationNumber = searchField.getText().trim();
        if (confirmationNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a confirmation number.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Reservation reservation = reservationDAO.getReservationByConfirmation(confirmationNumber);
        tableModel.setRowCount(0);

        if (reservation == null) {
            JOptionPane.showMessageDialog(this,
                    "No reservation found with confirmation number: " + confirmationNumber,
                    "Not Found",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        addReservationToTable(reservation);
    }

    private void filterByStatus() {
        String status = (String) statusFilterCombo.getSelectedItem();

        if ("ALL".equals(status)) {
            loadAllReservations();
            return;
        }

        tableModel.setRowCount(0);
        List<Reservation> filteredReservations = reservationDAO.getReservationsByStatus(
                Reservation.ReservationStatus.valueOf(status));

        if (filteredReservations.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No reservations found with status: " + status,
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        displayReservations(filteredReservations);
        logger.info("Filtered {} reservations with status {}", filteredReservations.size(), status);
    }

    private void displayReservations(List<Reservation> reservations) {

        for (Reservation reservation : reservations) {
            addReservationToTable(reservation);
        }
    }

    private void addReservationToTable(Reservation reservation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

        tableModel.addRow(new Object[] {
                reservation.getReservationId(),
                reservation.getConfirmationNumber(),
                reservation.getCustomer() != null ? reservation.getCustomer().getUser().getUsername() : "N/A",
                reservation.getFlight() != null ? reservation.getFlight().getFlightNumber() : "N/A",
                reservation.getFlight() != null ? reservation.getFlight().getDepartureTime().format(formatter) : "N/A",
                "$" + String.format("%.2f", reservation.getTotalFare()),
                reservation.getStatus(),
                reservation.getReservationDate().format(formatter)
        });
    }

    private void viewReservationDetails() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a reservation to view details.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
        Reservation reservation = reservationDAO.getReservationById(reservationId);

        if (reservation == null) {
            JOptionPane.showMessageDialog(this,
                    "Error loading reservation details.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        StringBuilder details = new StringBuilder();
        details.append("RESERVATION DETAILS\n");
        details.append("═".repeat(50)).append("\n\n");
        details.append("Confirmation Number: ").append(reservation.getConfirmationNumber()).append("\n");
        details.append("Status: ").append(reservation.getStatus()).append("\n");
        details.append("Booking Date: ").append(reservation.getReservationDate().format(formatter)).append("\n\n");

        if (reservation.getCustomer() != null) {
            details.append("CUSTOMER INFORMATION\n");
            details.append("-".repeat(50)).append("\n");
            details.append("Name: ").append(reservation.getCustomer().getUser().getUsername()).append("\n");
            details.append("Email: ").append(reservation.getCustomer().getUser().getEmail()).append("\n");
            if (reservation.getCustomer().getUser().getPhoneNumber() != null) {
                details.append("Phone: ").append(reservation.getCustomer().getUser().getPhoneNumber()).append("\n");
            }
            details.append("\n");
        }

        if (reservation.getFlight() != null) {
            details.append("FLIGHT INFORMATION\n");
            details.append("-".repeat(50)).append("\n");
            details.append("Flight Number: ").append(reservation.getFlight().getFlightNumber()).append("\n");
            if (reservation.getFlight().getAirline() != null) {
                details.append("Airline: ").append(reservation.getFlight().getAirline().getAirlineName()).append("\n");
            }
            if (reservation.getFlight().getRoute() != null) {
                details.append("Route: ").append(reservation.getFlight().getRoute().getOriginAirport())
                        .append(" → ").append(reservation.getFlight().getRoute().getDestinationAirport()).append("\n");
            }
            details.append("Departure: ").append(reservation.getFlight().getDepartureTime().format(formatter))
                    .append("\n");
            details.append("Arrival: ").append(reservation.getFlight().getArrivalTime().format(formatter)).append("\n");
            details.append("\n");
        }

        details.append("PAYMENT INFORMATION\n");
        details.append("-".repeat(50)).append("\n");
        details.append("Total Fare: $").append(String.format("%.2f", reservation.getTotalFare())).append("\n");

        if (reservation.getPassengers() != null && !reservation.getPassengers().isEmpty()) {
            details.append("\nPASSENGERS\n");
            details.append("-".repeat(50)).append("\n");
            for (int i = 0; i < reservation.getPassengers().size(); i++) {
                var passenger = reservation.getPassengers().get(i);
                details.append(i + 1).append(". ")
                        .append(passenger.getFirstName()).append(" ")
                        .append(passenger.getLastName())
                        .append(" (").append(passenger.getIdType()).append(": ")
                        .append(passenger.getIdNumber()).append(")\n");
            }
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "Reservation Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a reservation to cancel.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
        String confirmationNumber = (String) tableModel.getValueAt(selectedRow, 1);
        String status = (String) tableModel.getValueAt(selectedRow, 6);

        if ("CANCELLED".equals(status) || "COMPLETED".equals(status)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot cancel a reservation with status: " + status,
                    "Invalid Operation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?\n" +
                        "Confirmation Number: " + confirmationNumber,
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = reservationDAO.cancelReservation(reservationId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Reservation cancelled successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                String filterStatus = (String) statusFilterCombo.getSelectedItem();
                if ("ALL".equals(filterStatus)) {
                    loadAllReservations();
                } else {
                    filterByStatus();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to cancel reservation. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
