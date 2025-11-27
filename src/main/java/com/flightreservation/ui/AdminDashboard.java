package com.flightreservation.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flightreservation.dao.UserDAO;
import com.flightreservation.model.User;
import com.flightreservation.ui.panels.FlightManagementPanel;
import com.flightreservation.util.SessionManager;

/**
 * Admin Dashboard - Manage flights, routes, aircraft, schedules
 */
public class AdminDashboard extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboard.class);
    private User currentUser;
    private JPanel contentPanel;

    public AdminDashboard() {
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Flight Reservation System - Administrator Dashboard");
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
        menuBar.setBackground(new Color(220, 20, 60));

        // Flights Management Menu
        JMenu flightsMenu = createStyledMenu("Flights");
        JMenuItem addFlightItem = createStyledMenuItem("Add New Flight");
        addFlightItem.addActionListener(e -> showAddFlight());
        JMenuItem updateFlightItem = createStyledMenuItem("Update Flight");
        updateFlightItem.addActionListener(e -> showUpdateFlight());
        JMenuItem removeFlightItem = createStyledMenuItem("Remove Flight");
        removeFlightItem.addActionListener(e -> showRemoveFlight());
        JMenuItem viewAllFlightsItem = createStyledMenuItem("View All Flights");
        viewAllFlightsItem.addActionListener(e -> showAllFlights());
        flightsMenu.add(addFlightItem);
        flightsMenu.add(updateFlightItem);
        flightsMenu.add(removeFlightItem);
        flightsMenu.addSeparator();
        flightsMenu.add(viewAllFlightsItem);
        menuBar.add(flightsMenu);

        // Routes Management Menu
        JMenu routesMenu = createStyledMenu("Routes");
        JMenuItem addRouteItem = createStyledMenuItem("Add New Route");
        addRouteItem.addActionListener(e -> showAddRoute());
        JMenuItem updateRouteItem = createStyledMenuItem("Update Route");
        updateRouteItem.addActionListener(e -> showUpdateRoute());
        JMenuItem removeRouteItem = createStyledMenuItem("Remove Route");
        removeRouteItem.addActionListener(e -> showRemoveRoute());
        JMenuItem viewAllRoutesItem = createStyledMenuItem("View All Routes");
        viewAllRoutesItem.addActionListener(e -> showAllRoutes());
        routesMenu.add(addRouteItem);
        routesMenu.add(updateRouteItem);
        routesMenu.add(removeRouteItem);
        routesMenu.addSeparator();
        routesMenu.add(viewAllRoutesItem);
        menuBar.add(routesMenu);

        // Aircraft Management Menu
        JMenu aircraftMenu = createStyledMenu("Aircraft");
        JMenuItem addAircraftItem = createStyledMenuItem("Add New Aircraft");
        addAircraftItem.addActionListener(e -> showAddAircraft());
        JMenuItem updateAircraftItem = createStyledMenuItem("Update Aircraft");
        updateAircraftItem.addActionListener(e -> showUpdateAircraft());
        JMenuItem removeAircraftItem = createStyledMenuItem("Remove Aircraft");
        removeAircraftItem.addActionListener(e -> showRemoveAircraft());
        JMenuItem viewAllAircraftItem = createStyledMenuItem("View All Aircraft");
        viewAllAircraftItem.addActionListener(e -> showAllAircraft());
        aircraftMenu.add(addAircraftItem);
        aircraftMenu.add(updateAircraftItem);
        aircraftMenu.add(removeAircraftItem);
        aircraftMenu.addSeparator();
        aircraftMenu.add(viewAllAircraftItem);
        menuBar.add(aircraftMenu);

        // Airlines Management Menu
        JMenu airlinesMenu = createStyledMenu("Airlines");
        JMenuItem addAirlineItem = createStyledMenuItem("Add New Airline");
        addAirlineItem.addActionListener(e -> showAddAirline());
        JMenuItem updateAirlineItem = createStyledMenuItem("Update Airline");
        updateAirlineItem.addActionListener(e -> showUpdateAirline());
        JMenuItem viewAllAirlinesItem = createStyledMenuItem("View All Airlines");
        viewAllAirlinesItem.addActionListener(e -> showAllAirlines());
        airlinesMenu.add(addAirlineItem);
        airlinesMenu.add(updateAirlineItem);
        airlinesMenu.addSeparator();
        airlinesMenu.add(viewAllAirlinesItem);
        menuBar.add(airlinesMenu);

        // Notifications Menu
        JMenu notificationsMenu = createStyledMenu("Notifications");
        JMenuItem sendNewsletterItem = createStyledMenuItem("Send Newsletter");
        sendNewsletterItem.addActionListener(e -> showSendNewsletter());
        JMenuItem sendPromoItem = createStyledMenuItem("Send Promotion");
        sendPromoItem.addActionListener(e -> showSendPromotion());
        JMenuItem viewNotificationsItem = createStyledMenuItem("View Sent Notifications");
        viewNotificationsItem.addActionListener(e -> showNotificationHistory());
        notificationsMenu.add(sendNewsletterItem);
        notificationsMenu.add(sendPromoItem);
        notificationsMenu.addSeparator();
        notificationsMenu.add(viewNotificationsItem);
        menuBar.add(notificationsMenu);

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
        panel.setBackground(new Color(255, 240, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel welcomeLabel = new JLabel("Administrator Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(139, 0, 0));
        panel.add(welcomeLabel, gbc);

        gbc.gridy++;
        JLabel roleLabel = new JLabel("Welcome, " + currentUser.getUsername());
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
                "Administrator Capabilities:\n\n" +
                        "• Add, update, or remove flights from the system\n" +
                        "• Manage routes between airports (origin/destination pairs)\n" +
                        "• Manage aircraft fleet and configurations\n" +
                        "• Update flight schedules and ensure no conflicts\n" +
                        "• Manage airline information\n" +
                        "• Send newsletters and promotional notifications to customers\n" +
                        "• View notification history and customer engagement\n\n" +
                        "Use the menu bar to access all administrative functions.");
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(255, 240, 245));
        infoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(infoArea, gbc);

        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(255, 240, 245));

        JButton flightBtn = createActionButton("✈ Manage Flights", new Color(220, 20, 60));
        flightBtn.addActionListener(e -> showAllFlights());

        JButton routeBtn = createActionButton("🗺 Manage Routes", new Color(255, 140, 0));
        routeBtn.addActionListener(e -> showAllRoutes());

        JButton aircraftBtn = createActionButton("🛩 Manage Aircraft", new Color(70, 130, 180));
        aircraftBtn.addActionListener(e -> showAllAircraft());

        JButton notificationsBtn = createActionButton("📧 Send Notifications", new Color(106, 90, 205));
        notificationsBtn.addActionListener(e -> showSendNewsletter());

        panel.add(flightBtn);
        panel.add(routeBtn);
        panel.add(aircraftBtn);
        panel.add(notificationsBtn);

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

    // Flight Management Methods
    private void showAddFlight() {
        showFlightManagement();
    }

    private void showUpdateFlight() {
        showFlightManagement();
    }

    private void showRemoveFlight() {
        showFlightManagement();
    }

    private void showAllFlights() {
        showFlightManagement();
    }

    private void showFlightManagement() {
        contentPanel.removeAll();
        FlightManagementPanel panel = new FlightManagementPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Route Management Methods
    private void showAddRoute() {
        showRouteManagement();
    }

    private void showUpdateRoute() {
        showRouteManagement();
    }

    private void showRemoveRoute() {
        showRouteManagement();
    }

    private void showAllRoutes() {
        showRouteManagement();
    }

    private void showRouteManagement() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.RouteManagementPanel panel = new com.flightreservation.ui.panels.RouteManagementPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Aircraft Management Methods
    private void showAddAircraft() {
        showAircraftManagement();
    }

    private void showUpdateAircraft() {
        showAircraftManagement();
    }

    private void showRemoveAircraft() {
        showAircraftManagement();
    }

    private void showAllAircraft() {
        showAircraftManagement();
    }

    private void showAircraftManagement() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.AircraftManagementPanel panel = new com.flightreservation.ui.panels.AircraftManagementPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Airlines Management Methods
    private void showAddAirline() {
        switchContent("Add Airline Feature - Coming Soon");
    }

    private void showUpdateAirline() {
        switchContent("Update Airline Feature - Coming Soon");
    }

    private void showAllAirlines() {
        switchContent("View All Airlines Feature - Coming Soon");
    }

    // Notification Methods
    private void showSendNewsletter() {
        showNotifications();
    }

    private void showSendPromotion() {
        showNotifications();
    }

    private void showNotificationHistory() {
        showNotifications();
    }

    private void showNotifications() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.NotificationPanel panel = new com.flightreservation.ui.panels.NotificationPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
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
        panel.setBackground(new Color(255, 240, 245));

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
                "Administrator Profile\n\n" +
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
                "Administrator Profile",
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

            logger.info("Administrator logged out");

            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
