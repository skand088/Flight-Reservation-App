package com.flightreservation.ui;

import com.flightreservation.model.User;
import com.flightreservation.util.SessionManager;
import com.flightreservation.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Agent Dashboard - Assist customers, manage profiles, modify reservations
 */
public class AgentDashboard extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(AgentDashboard.class);
    private User currentUser;
    private JPanel contentPanel;

    public AgentDashboard() {
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Flight Reservation System - Agent Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        createMenuBar();

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel, BorderLayout.CENTER);

        add(contentPanel);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(76, 175, 80));

        // Customer Management Menu
        JMenu customerMenu = createStyledMenu("Customers");
        JMenuItem searchCustomerItem = createStyledMenuItem("Search Customer");
        searchCustomerItem.addActionListener(e -> showSearchCustomer());
        JMenuItem addCustomerItem = createStyledMenuItem("Add New Customer");
        addCustomerItem.addActionListener(e -> showAddCustomer());
        JMenuItem editCustomerItem = createStyledMenuItem("Edit Customer");
        editCustomerItem.addActionListener(e -> showEditCustomer());
        customerMenu.add(searchCustomerItem);
        customerMenu.add(addCustomerItem);
        customerMenu.add(editCustomerItem);
        menuBar.add(customerMenu);

        // Reservations Menu
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

        // Flights Menu
        JMenu flightsMenu = createStyledMenu("Flights");
        JMenuItem searchFlightsItem = createStyledMenuItem("Search Flights");
        searchFlightsItem.addActionListener(e -> showSearchFlights());
        JMenuItem viewScheduleItem = createStyledMenuItem("View Flight Schedule");
        viewScheduleItem.addActionListener(e -> showFlightSchedule());
        flightsMenu.add(searchFlightsItem);
        flightsMenu.add(viewScheduleItem);
        menuBar.add(flightsMenu);

        // Account Menu
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

    private JMenu createStyledMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Arial", Font.BOLD, 14));
        return menu;
    }

    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Arial", Font.PLAIN, 12));
        return item;
    }

    private JPanel createWelcomePanel() {
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
                        "• Manage customer profiles (add, edit, view)\n" +
                        "• Modify or cancel existing reservations\n" +
                        "• Search and view flight schedules\n" +
                        "• Provide customer service and support\n\n" +
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

        JButton customerBtn = createActionButton("👥 Manage Customers", new Color(76, 175, 80));
        customerBtn.addActionListener(e -> showSearchCustomer());

        JButton reservationBtn = createActionButton("✈ Make Reservation", new Color(33, 147, 176));
        reservationBtn.addActionListener(e -> showMakeReservation());

        JButton scheduleBtn = createActionButton("📅 Flight Schedule", new Color(255, 152, 0));
        scheduleBtn.addActionListener(e -> showFlightSchedule());

        panel.add(customerBtn);
        panel.add(reservationBtn);
        panel.add(scheduleBtn);

        return panel;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showSearchCustomer() {
        switchContent("Search Customer Feature - Coming Soon");
    }

    private void showAddCustomer() {
        switchContent("Add Customer Feature - Coming Soon");
    }

    private void showEditCustomer() {
        switchContent("Edit Customer Feature - Coming Soon");
    }

    private void showMakeReservation() {
        switchContent("Make Reservation Feature - Coming Soon");
    }

    private void showModifyReservation() {
        switchContent("Modify Reservation Feature - Coming Soon");
    }

    private void showCancelReservation() {
        switchContent("Cancel Reservation Feature - Coming Soon");
    }

    private void showAllReservations() {
        switchContent("View All Reservations Feature - Coming Soon");
    }

    private void showSearchFlights() {
        switchContent("Search Flights Feature - Coming Soon");
    }

    private void showFlightSchedule() {
        switchContent("Flight Schedule Feature - Coming Soon");
    }

    private void switchContent(String message) {
        contentPanel.removeAll();
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(label, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(240, 255, 240));

        JButton backButton = new JButton("← Back to Home");
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> showHome());

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> handleLogout());

        panel.add(backButton);
        panel.add(logoutButton);

        return panel;
    }

    private void showHome() {
        contentPanel.removeAll();
        JPanel welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfile() {
        String profileInfo = String.format(
                "Agent Profile\n\n" +
                        "Username: %s\n" +
                        "Email: %s\n" +
                        "Phone: %s\n" +
                        "Role: %s\n" +
                        "Account Status: %s",
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Not set",
                currentUser.getRole(),
                currentUser.getAccountStatus());

        JOptionPane.showMessageDialog(this,
                profileInfo,
                "Agent Profile",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UserDAO userDAO = new UserDAO();
            userDAO.endSession(SessionManager.getInstance().getSessionId());
            SessionManager.getInstance().endSession();

            logger.info("Agent logged out");

            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
