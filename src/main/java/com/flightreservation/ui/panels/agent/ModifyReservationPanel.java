package com.flightreservation.ui.panels.agent;

import com.flightreservation.dao.ReservationDAO;
import com.flightreservation.dao.SeatDAO;
import com.flightreservation.model.entities.Passenger;
import com.flightreservation.model.entities.Reservation;
import com.flightreservation.model.entities.Seat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ModifyReservationPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(ModifyReservationPanel.class);

    private final ReservationDAO reservationDAO;
    private final SeatDAO seatDAO;

    private JTextField confirmationField;
    private JPanel detailsPanel;
    private Reservation currentReservation;

    public ModifyReservationPanel() {
        this.reservationDAO = new ReservationDAO();
        this.seatDAO = new SeatDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Modify Reservation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(10, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Reservation"));

        searchPanel.add(new JLabel("Confirmation Number:"));
        confirmationField = new JTextField(20);
        searchPanel.add(confirmationField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(0, 123, 255));
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> searchReservation());
        searchPanel.add(searchBtn);

        add(searchPanel, BorderLayout.NORTH);

        detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.add(createEmptyStatePanel(), BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createEmptyStatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel("Enter a confirmation number to search for a reservation");
        label.setFont(new Font("Arial", Font.ITALIC, 16));
        label.setForeground(Color.GRAY);
        panel.add(label);
        return panel;
    }

    private void searchReservation() {
        String confirmationNumber = confirmationField.getText().trim();
        if (confirmationNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a confirmation number.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentReservation = reservationDAO.getReservationByConfirmation(confirmationNumber);

        if (currentReservation == null) {
            JOptionPane.showMessageDialog(this,
                    "No reservation found with confirmation number: " + confirmationNumber,
                    "Not Found",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if ("CANCELLED".equals(currentReservation.getStatus().toString()) ||
                "COMPLETED".equals(currentReservation.getStatus().toString())) {
            JOptionPane.showMessageDialog(this,
                    "Cannot modify a reservation with status: " + currentReservation.getStatus(),
                    "Invalid Status",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        displayReservationDetails();
    }

    private void displayReservationDetails() {
        detailsPanel.removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = createReservationInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        JPanel passengersPanel = createPassengersPanel();
        mainPanel.add(passengersPanel, BorderLayout.CENTER);

        JPanel actionsPanel = createActionsPanel();
        mainPanel.add(actionsPanel, BorderLayout.SOUTH);

        detailsPanel.add(mainPanel, BorderLayout.CENTER);
        detailsPanel.revalidate();
        detailsPanel.repaint();

        logger.info("Displaying reservation details for {}", currentReservation.getConfirmationNumber());
    }

    private JPanel createReservationInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 15, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2), "Reservation Information"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

        panel.add(createBoldLabel("Confirmation Number:"));
        panel.add(new JLabel(currentReservation.getConfirmationNumber()));

        panel.add(createBoldLabel("Status:"));
        panel.add(new JLabel(currentReservation.getStatus().toString()));

        panel.add(createBoldLabel("Booking Date:"));
        panel.add(new JLabel(currentReservation.getReservationDate().format(formatter)));

        panel.add(createBoldLabel("Total Fare:"));
        panel.add(new JLabel("$" + String.format("%.2f", currentReservation.getTotalFare())));

        if (currentReservation.getCustomer() != null) {
            panel.add(createBoldLabel("Customer:"));
            panel.add(new JLabel(currentReservation.getCustomer().getUser().getUsername()));

            panel.add(createBoldLabel("Email:"));
            panel.add(new JLabel(currentReservation.getCustomer().getUser().getEmail()));
        }

        if (currentReservation.getFlight() != null) {
            panel.add(createBoldLabel("Flight Number:"));
            panel.add(new JLabel(currentReservation.getFlight().getFlightNumber()));

            if (currentReservation.getFlight().getRoute() != null) {
                panel.add(createBoldLabel("Route:"));
                panel.add(new JLabel(
                        currentReservation.getFlight().getRoute().getOriginAirport() + " → " +
                                currentReservation.getFlight().getRoute().getDestinationAirport()));
            }

            panel.add(createBoldLabel("Departure:"));
            panel.add(new JLabel(currentReservation.getFlight().getDepartureTime().format(formatter)));

            panel.add(createBoldLabel("Arrival:"));
            panel.add(new JLabel(currentReservation.getFlight().getArrivalTime().format(formatter)));
        }

        return panel;
    }

    private JPanel createPassengersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2), "Passengers"));

        if (currentReservation.getPassengers() == null || currentReservation.getPassengers().isEmpty()) {
            panel.add(new JLabel("No passenger information available", SwingConstants.CENTER), BorderLayout.CENTER);
            return panel;
        }

        JPanel passengersListPanel = new JPanel();
        passengersListPanel.setLayout(new BoxLayout(passengersListPanel, BoxLayout.Y_AXIS));

        for (Passenger passenger : currentReservation.getPassengers()) {
            JPanel passengerPanel = createPassengerCard(passenger);
            passengersListPanel.add(passengerPanel);
            passengersListPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(passengersListPanel);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPassengerCard(Passenger passenger) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)));

        panel.add(createBoldLabel("Name:"));
        panel.add(new JLabel(passenger.getFirstName() + " " + passenger.getLastName()));

        panel.add(createBoldLabel("Age:"));
        panel.add(new JLabel(String.valueOf(passenger.getAge())));

        panel.add(createBoldLabel("ID Type:"));
        panel.add(new JLabel(passenger.getIdType().toString()));

        panel.add(createBoldLabel("ID Number:"));
        panel.add(new JLabel(passenger.getIdNumber()));

        if (passenger.getContactEmail() != null) {
            panel.add(createBoldLabel("Email:"));
            panel.add(new JLabel(passenger.getContactEmail()));
        }

        if (passenger.getContactPhone() != null) {
            panel.add(createBoldLabel("Phone:"));
            panel.add(new JLabel(passenger.getContactPhone()));
        }

        if (passenger.getSeatId() > 0) {
            Seat seat = seatDAO.getSeatById(passenger.getSeatId());
            if (seat != null) {
                panel.add(createBoldLabel("Seat:"));
                panel.add(new JLabel(seat.getSeatNumber() + " (" + seat.getSeatClass() + ")"));
            }
        }

        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton changeSeatBtn = new JButton("Change Seat");
        changeSeatBtn.setBackground(new Color(255, 193, 7));
        changeSeatBtn.setForeground(Color.BLACK);
        changeSeatBtn.setFocusPainted(false);
        changeSeatBtn.addActionListener(e -> changeSeat());
        panel.add(changeSeatBtn);

        JButton updateStatusBtn = new JButton("Update Status");
        updateStatusBtn.setBackground(new Color(0, 123, 255));
        updateStatusBtn.setFocusPainted(false);
        updateStatusBtn.addActionListener(e -> updateStatus());
        panel.add(updateStatusBtn);

        JButton cancelBtn = new JButton("Cancel Reservation");
        cancelBtn.setBackground(new Color(220, 53, 69));
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> cancelReservation());
        panel.add(cancelBtn);

        return panel;
    }

    private void changeSeat() {
        if (currentReservation.getPassengers() == null || currentReservation.getPassengers().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No passengers found for this reservation.",
                    "No Passengers",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Seat> availableSeats = seatDAO.getAvailableSeats(currentReservation.getFlightId());

        if (availableSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No available seats for this flight.",
                    "No Seats",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] seatOptions = availableSeats.stream()
                .map(s -> s.getSeatNumber() + " - " + s.getSeatClass() + " - $" + String.format("%.2f", s.getPrice()))
                .toArray(String[]::new);

        String selectedSeat = (String) JOptionPane.showInputDialog(this,
                "Select a new seat:",
                "Change Seat",
                JOptionPane.QUESTION_MESSAGE,
                null,
                seatOptions,
                seatOptions[0]);

        if (selectedSeat != null) {
            String seatNumber = selectedSeat.split(" - ")[0];
            Seat newSeat = availableSeats.stream()
                    .filter(s -> s.getSeatNumber().equals(seatNumber))
                    .findFirst()
                    .orElse(null);

            if (newSeat != null) {
                Passenger passenger = currentReservation.getPassengers().get(0);

                if (passenger.getSeatId() > 0) {
                    seatDAO.releaseSeat(passenger.getSeatId());
                }

                boolean reserved = seatDAO.reserveSeat(newSeat.getSeatId());

                if (reserved) {
                    JOptionPane.showMessageDialog(this,
                            "Seat changed successfully to " + seatNumber,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    searchReservation();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to reserve new seat.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void updateStatus() {
        String[] statusOptions = { "PENDING", "CONFIRMED", "COMPLETED" };
        String newStatus = (String) JOptionPane.showInputDialog(this,
                "Select new status:",
                "Update Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statusOptions,
                currentReservation.getStatus().toString());

        if (newStatus != null && !newStatus.equals(currentReservation.getStatus().toString())) {
            boolean success = reservationDAO.updateReservationStatus(
                    currentReservation.getReservationId(),
                    Reservation.ReservationStatus.valueOf(newStatus));

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Reservation status updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                searchReservation();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update reservation status.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelReservation() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?\n" +
                        "Confirmation Number: " + currentReservation.getConfirmationNumber(),
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = reservationDAO.cancelReservation(currentReservation.getReservationId());

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Reservation cancelled successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                detailsPanel.removeAll();
                detailsPanel.add(createEmptyStatePanel(), BorderLayout.CENTER);
                detailsPanel.revalidate();
                detailsPanel.repaint();
                confirmationField.setText("");
                currentReservation = null;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to cancel reservation.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }
}
