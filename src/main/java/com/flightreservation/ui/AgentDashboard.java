package com.flightreservation.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.flightreservation.model.entities.Reservation;

/**
 * Agent Dashboard - Assist customers, manage profiles, modify reservations
 */
public class AgentDashboard extends BaseDashboard {

    @Override
    protected String getDashboardTitle() {
        return "Flight Reservation System - Agent Dashboard";
    }

    @Override
    protected Color getBackgroundColor() {
        return new Color(240, 255, 240);
    }

    @Override
    protected String getRoleDisplayName() {
        return "Agent";
    }

    @Override
    protected void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(76, 175, 80));

        JMenu customerMenu = createStyledMenu("Customers");
        JMenuItem manageCustomersItem = createStyledMenuItem("Manage Customers");
        manageCustomersItem.addActionListener(e -> showCustomerManagement());
        customerMenu.add(manageCustomersItem);
        menuBar.add(customerMenu);

        JMenu reservationsMenu = createStyledMenu("Reservations");
        JMenuItem makeReservationItem = createStyledMenuItem("Make Reservation");
        makeReservationItem.addActionListener(e -> showMakeReservation());
        JMenuItem modifyReservationItem = createStyledMenuItem("Modify Reservation");
        modifyReservationItem.addActionListener(e -> showModifyReservation());
        JMenuItem cancelReservationItem = createStyledMenuItem("Cancel Reservation");
        cancelReservationItem.addActionListener(e -> showCancelReservation());
        JMenuItem viewAllReservationsItem = createStyledMenuItem("View All Reservations");
        viewAllReservationsItem.addActionListener(e -> showAllReservations());
        reservationsMenu.add(makeReservationItem);
        reservationsMenu.add(modifyReservationItem);
        reservationsMenu.add(cancelReservationItem);
        reservationsMenu.addSeparator();
        reservationsMenu.add(viewAllReservationsItem);
        menuBar.add(reservationsMenu);

        JMenu flightsMenu = createStyledMenu("Flights");
        JMenuItem searchFlightsItem = createStyledMenuItem("Search Flights");
        searchFlightsItem.addActionListener(e -> showSearchFlights());
        JMenuItem viewScheduleItem = createStyledMenuItem("View Flight Schedule");
        viewScheduleItem.addActionListener(e -> showFlightSchedule());
        flightsMenu.add(searchFlightsItem);
        flightsMenu.add(viewScheduleItem);
        menuBar.add(flightsMenu);

        JMenu accountMenu = createStyledMenu("Account");
        JMenuItem profileItem = createStyledMenuItem("My Profile");
        profileItem.addActionListener(e -> showProfile());
        JMenuItem logoutItem = createStyledMenuItem("Logout");
        logoutItem.addActionListener(e -> handleLogout());
        accountMenu.add(profileItem);
        accountMenu.addSeparator();
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);

        setJMenuBar(menuBar);
    }

    @Override
    protected JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 255, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel welcomeLabel = new JLabel("Welcome, Agent " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(34, 139, 34));
        panel.add(welcomeLabel, gbc);

        gbc.gridy++;
        JLabel roleLabel = new JLabel("Flight Agent Portal");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        roleLabel.setForeground(new Color(105, 105, 105));
        panel.add(roleLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(30, 10, 10, 10);
        JPanel actionsPanel = createQuickActionsPanel();
        panel.add(actionsPanel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(40, 10, 10, 10);
        JTextArea infoArea = new JTextArea(
                "Agent Responsibilities:\n\n" +
                        "• Assist customers with flight bookings and reservations\n" +
                        "• Manage customer profile\n" +
                        "• Modify / cancel existing reservations\n" +
                        "• Search and view flight schedules\n" +
                        "Use the menu bar to access all agent functions.");
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(240, 255, 240));
        infoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(infoArea, gbc);

        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(240, 255, 240));

        JButton customerBtn = createActionButton("Manage Customers", new Color(76, 175, 80));
        customerBtn.addActionListener(e -> showCustomerManagement());

        JButton reservationBtn = createActionButton("Make Reservation", new Color(33, 147, 176));
        reservationBtn.addActionListener(e -> showMakeReservation());

        JButton scheduleBtn = createActionButton("Flight Schedule", new Color(255, 152, 0));
        scheduleBtn.addActionListener(e -> showFlightSchedule());

        panel.add(customerBtn);
        panel.add(reservationBtn);
        panel.add(scheduleBtn);

        return panel;
    }

    private void showCustomerManagement() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.agent.CustomerManagementPanel panel = new com.flightreservation.ui.panels.agent.CustomerManagementPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showMakeReservation() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.agent.AgentReservationPanel panel = new com.flightreservation.ui.panels.agent.AgentReservationPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showModifyReservation() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.agent.ModifyReservationPanel panel = new com.flightreservation.ui.panels.agent.ModifyReservationPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showCancelReservation() {
        String confirmationNumber = JOptionPane.showInputDialog(this,
                "Enter Confirmation Number:",
                "Cancel Reservation",
                JOptionPane.QUESTION_MESSAGE);

        if (confirmationNumber != null && !confirmationNumber.trim().isEmpty()) {
            com.flightreservation.dao.ReservationDAO reservationDAO = new com.flightreservation.dao.ReservationDAO();
            Reservation reservation = reservationDAO
                    .getReservationByConfirmation(confirmationNumber.trim());

            if (reservation == null) {
                JOptionPane.showMessageDialog(this,
                        "No reservation found with confirmation number: " + confirmationNumber,
                        "Not Found",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("CANCELLED".equals(reservation.getStatus().toString()) ||
                    "COMPLETED".equals(reservation.getStatus().toString())) {
                JOptionPane.showMessageDialog(this,
                        "Cannot cancel a reservation with status: " + reservation.getStatus(),
                        "Invalid Status",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Cancel reservation for:\n" +
                            "Confirmation: " + reservation.getConfirmationNumber() + "\n" +
                            "Flight: "
                            + (reservation.getFlight() != null ? reservation.getFlight().getFlightNumber() : "N/A")
                            + "\n" +
                            "Customer ID: " + reservation.getCustomerId(),
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = reservationDAO.cancelReservation(reservation.getReservationId());
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Reservation cancelled successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to cancel reservation.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showAllReservations() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.agent.AllReservationsPanel panel = new com.flightreservation.ui.panels.agent.AllReservationsPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSearchFlights() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.customer.FlightSearchPanel panel = new com.flightreservation.ui.panels.customer.FlightSearchPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showFlightSchedule() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.agent.FlightSchedulePanel panel = new com.flightreservation.ui.panels.agent.FlightSchedulePanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
