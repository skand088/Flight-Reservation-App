package com.flightreservation.ui.panels;

import com.flightreservation.controller.ReservationController;
import com.flightreservation.model.Reservation;
import com.flightreservation.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for viewing and managing reservations
 */
public class ReservationsPanel extends JPanel {
    private final ReservationController controller;
    private final int customerId;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;
    private List<Reservation> currentReservations;

    public ReservationsPanel(int customerId) {
        this.controller = new ReservationController();
        this.customerId = customerId;
        initializeUI();
        loadReservations();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("My Reservations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Confirmation #", "Flight", "Route", "Date", "Status", "Total Fare", "Passengers" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationsTable = new JTable(tableModel);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadReservations());
        buttonPanel.add(refreshButton);

        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.addActionListener(e -> viewReservationDetails());
        buttonPanel.add(viewDetailsButton);

        JButton cancelButton = new JButton("Cancel Reservation");
        cancelButton.addActionListener(e -> cancelReservation());
        cancelButton.setForeground(Color.RED);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadReservations() {
        SwingWorker<List<Reservation>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Reservation> doInBackground() throws Exception {
                return controller.getCustomerReservations(customerId);
            }

            @Override
            protected void done() {
                try {
                    currentReservations = get();
                    displayReservations(currentReservations);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ReservationsPanel.this,
                            "Error loading reservations: " + ex.getMessage(),
                            "Load Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void displayReservations(List<Reservation> reservations) {
        tableModel.setRowCount(0);

        if (reservations.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "You don't have any reservations yet",
                    "No Reservations",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Reservation reservation : reservations) {
            String route = "N/A";
            String flightNumber = "N/A";
            String departureTime = "N/A";

            if (reservation.getFlight() != null) {
                flightNumber = reservation.getFlight().getFlightNumber();
                if (reservation.getFlight().getRoute() != null) {
                    route = reservation.getFlight().getRoute().getOriginAirport() +
                            " → " + reservation.getFlight().getRoute().getDestinationAirport();
                }
                if (reservation.getFlight().getDepartureTime() != null) {
                    departureTime = reservation.getFlight().getDepartureTime().format(dateFormatter);
                }
            }

            Object[] row = {
                    reservation.getConfirmationNumber(),
                    flightNumber,
                    route,
                    departureTime,
                    reservation.getStatus().name(),
                    String.format("$%.2f", reservation.getTotalFare()),
                    reservation.getPassengers().size()
            };
            tableModel.addRow(row);
        }
    }

    private void viewReservationDetails() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a reservation to view",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Reservation reservation = currentReservations.get(selectedRow);
        showReservationDetails(reservation);
    }

    private void showReservationDetails(Reservation reservation) {
        StringBuilder details = new StringBuilder();
        details.append("Confirmation Number: ").append(reservation.getConfirmationNumber()).append("\n");
        details.append("Status: ").append(reservation.getStatus()).append("\n");
        details.append("Total Fare: $").append(String.format("%.2f", reservation.getTotalFare())).append("\n\n");

        if (reservation.getFlight() != null) {
            details.append("Flight: ").append(reservation.getFlight().getFlightNumber()).append("\n");
            if (reservation.getFlight().getRoute() != null) {
                details.append("Route: ")
                        .append(reservation.getFlight().getRoute().getOriginAirport())
                        .append(" → ")
                        .append(reservation.getFlight().getRoute().getDestinationAirport())
                        .append("\n");
            }
            if (reservation.getFlight().getDepartureTime() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                details.append("Departure: ").append(reservation.getFlight().getDepartureTime().format(formatter))
                        .append("\n");
                details.append("Arrival: ").append(reservation.getFlight().getArrivalTime().format(formatter))
                        .append("\n");
            }
        }

        details.append("\nPassengers:\n");
        for (int i = 0; i < reservation.getPassengers().size(); i++) {
            details.append((i + 1)).append(". ").append(reservation.getPassengers().get(i).getFullName()).append("\n");
        }

        JOptionPane.showMessageDialog(this,
                details.toString(),
                "Reservation Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a reservation to cancel",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Reservation reservation = currentReservations.get(selectedRow);

        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            JOptionPane.showMessageDialog(this,
                    "This reservation is already cancelled",
                    "Already Cancelled",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel reservation " + reservation.getConfirmationNumber() + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return controller.cancelReservation(reservation.getReservationId());
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(ReservationsPanel.this,
                                    "Reservation cancelled successfully",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            loadReservations();
                        } else {
                            JOptionPane.showMessageDialog(ReservationsPanel.this,
                                    "Failed to cancel reservation",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ReservationsPanel.this,
                                "Error cancelling reservation: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}
