package com.flightreservation.ui;

import com.flightreservation.dao.CustomerDAO;
import com.flightreservation.model.Customer;
import com.flightreservation.model.User;
import com.flightreservation.ui.panels.FlightSearchPanel;
import com.flightreservation.ui.panels.ReservationsPanel;
import com.flightreservation.util.SessionManager;
import com.flightreservation.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Customer Dashboard - Search flights, make/cancel reservations, view booking
 * history
 */
public class CustomerDashboard extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(CustomerDashboard.class);
    private User currentUser;
    private JPanel contentPanel;

    public CustomerDashboard() {
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Flight Reservation System - Customer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create menu bar
        createMenuBar();

        // Main content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Welcome panel
        JPanel welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel, BorderLayout.CENTER);

        add(contentPanel);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(33, 147, 176));

        // Flights Menu
        JMenu flightsMenu = createStyledMenu("Flights");
        JMenuItem searchFlightsItem = createStyledMenuItem("Search Flights");
        searchFlightsItem.addActionListener(e -> showSearchFlights());
        flightsMenu.add(searchFlightsItem);
        menuBar.add(flightsMenu);

        // Reservations Menu
        JMenu reservationsMenu = createStyledMenu("My Reservations");
        JMenuItem viewReservationsItem = createStyledMenuItem("View All Reservations");
        viewReservationsItem.addActionListener(e -> showMyReservations());
        JMenuItem cancelReservationItem = createStyledMenuItem("Cancel Reservation");
        cancelReservationItem.addActionListener(e -> showCancelReservation());
        reservationsMenu.add(viewReservationsItem);
        reservationsMenu.add(cancelReservationItem);
        menuBar.add(reservationsMenu);

        // Newsletters Menu
        JMenu newslettersMenu = createStyledMenu("Newsletters");
        JMenuItem viewNewslettersItem = createStyledMenuItem("View Newsletters");
        viewNewslettersItem.addActionListener(e -> showNewsletters());
        newslettersMenu.add(viewNewslettersItem);
        menuBar.add(newslettersMenu);

        // Profile Menu
        JMenu profileMenu = createStyledMenu("Profile");
        JMenuItem viewProfileItem = createStyledMenuItem("View Profile");
        viewProfileItem.addActionListener(e -> showProfile());
        JMenuItem updateProfileItem = createStyledMenuItem("Update Profile");
        updateProfileItem.addActionListener(e -> showUpdateProfile());
        profileMenu.add(viewProfileItem);
        profileMenu.add(updateProfileItem);
        menuBar.add(profileMenu);

        // Account Menu
        JMenu accountMenu = createStyledMenu("Account");
        JMenuItem logoutItem = createStyledMenuItem("Logout");
        logoutItem.addActionListener(e -> handleLogout());
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
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(25, 25, 112));
        panel.add(welcomeLabel, gbc);

        // Role label
        gbc.gridy++;
        JLabel roleLabel = new JLabel("Customer Portal");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        roleLabel.setForeground(new Color(105, 105, 105));
        panel.add(roleLabel, gbc);

        // Quick actions panel
        gbc.gridy++;
        gbc.insets = new Insets(30, 10, 10, 10);
        JPanel actionsPanel = createQuickActionsPanel();
        panel.add(actionsPanel, gbc);

        // Info text
        gbc.gridy++;
        gbc.insets = new Insets(40, 10, 10, 10);
        JTextArea infoArea = new JTextArea(
                "Quick Actions:\n\n" +
                        "• Search for available flights by origin, destination, and date\n" +
                        "• Make new reservations with seat selection\n" +
                        "• View your booking history and upcoming trips\n" +
                        "• Cancel or modify existing reservations\n" +
                        "• Update your profile and preferences\n\n" +
                        "Use the menu bar above to access all features.");
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(240, 248, 255));
        infoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(infoArea, gbc);

        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(240, 248, 255));

        JButton searchBtn = createActionButton("🔍 Search Flights", new Color(33, 147, 176));
        searchBtn.addActionListener(e -> showSearchFlights());

        JButton reservationsBtn = createActionButton("📋 My Reservations", new Color(76, 175, 80));
        reservationsBtn.addActionListener(e -> showMyReservations());

        JButton newslettersBtn = createActionButton("📬 Newsletters", new Color(255, 152, 0));
        newslettersBtn.addActionListener(e -> showNewsletters());

        panel.add(searchBtn);
        panel.add(reservationsBtn);
        panel.add(newslettersBtn);

        return panel;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showSearchFlights() {
        contentPanel.removeAll();
        FlightSearchPanel searchPanel = new FlightSearchPanel();
        contentPanel.add(searchPanel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showMyReservations() {
        contentPanel.removeAll();

        // Get customer ID for the current user
        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = customerDAO.getCustomerByUserId(currentUser.getUserId());

        if (customer != null) {
            ReservationsPanel reservationsPanel = new ReservationsPanel(customer.getCustomerId());
            contentPanel.add(reservationsPanel, BorderLayout.CENTER);
        } else {
            JLabel label = new JLabel("Customer information not found", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 24));
            contentPanel.add(label, BorderLayout.CENTER);
        }

        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showCancelReservation() {
        JOptionPane.showMessageDialog(this,
                "Cancel Reservation feature will be implemented soon.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showProfile() {
        String profileInfo = String.format(
                "User Profile\n\n" +
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
                "My Profile",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showUpdateProfile() {
        JOptionPane.showMessageDialog(this,
                "Update Profile feature will be implemented soon.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(240, 248, 255));

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

    private void showNewsletters() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.NewslettersPanel newslettersPanel = new com.flightreservation.ui.panels.NewslettersPanel();
        contentPanel.add(newslettersPanel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showHome() {
        contentPanel.removeAll();
        JPanel welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // End session
            UserDAO userDAO = new UserDAO();
            userDAO.endSession(SessionManager.getInstance().getSessionId());
            SessionManager.getInstance().endSession();

            logger.info("User logged out");

            // Close this window and show login
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
