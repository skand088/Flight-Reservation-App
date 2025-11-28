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
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.flightreservation.ui.panels.admin.FlightManagementPanel;

/**
 * Admin Dashboard - Manage flights, routes, aircraft, schedules
 */
public class AdminDashboard extends BaseDashboard {

    @Override
    protected String getDashboardTitle() {
        return "Flight Reservation System - Administrator Dashboard";
    }

    @Override
    protected Color getBackgroundColor() {
        return new Color(255, 240, 245);
    }

    @Override
    protected String getRoleDisplayName() {
        return "Administrator";
    }

    @Override
    protected void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(220, 20, 60));

        JMenu flightsMenu = createStyledMenu("Flights");
        JMenuItem manageFlightsItem = createStyledMenuItem("Manage Flights");
        manageFlightsItem.addActionListener(e -> showFlightManagement());
        flightsMenu.add(manageFlightsItem);
        menuBar.add(flightsMenu);

        JMenu routesMenu = createStyledMenu("Routes");
        JMenuItem manageRoutesItem = createStyledMenuItem("Manage Routes");
        manageRoutesItem.addActionListener(e -> showRouteManagement());
        routesMenu.add(manageRoutesItem);
        menuBar.add(routesMenu);

        JMenu aircraftMenu = createStyledMenu("Aircraft");
        JMenuItem manageAircraftItem = createStyledMenuItem("Manage Aircraft");
        manageAircraftItem.addActionListener(e -> showAircraftManagement());
        aircraftMenu.add(manageAircraftItem);
        menuBar.add(aircraftMenu);

        JMenu airlinesMenu = createStyledMenu("Airlines");
        JMenuItem manageAirlinesItem = createStyledMenuItem("Manage Airlines");
        manageAirlinesItem.addActionListener(e -> showAirlineManagement());
        airlinesMenu.add(manageAirlinesItem);
        menuBar.add(airlinesMenu);

        JMenu notificationsMenu = createStyledMenu("Notifications");
        JMenuItem manageNotificationsItem = createStyledMenuItem("Manage Notifications");
        manageNotificationsItem.addActionListener(e -> showNotifications());
        notificationsMenu.add(manageNotificationsItem);
        menuBar.add(notificationsMenu);

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
                        "• Add, update, remove flights from the system\n" +
                        "• Manage routes\n" +
                        "• Manage aircrafts\n" +
                        "• Update flight schedules\n" +
                        "• Manage airline information\n" +
                        "• Send newsletters and promotions to customers\n");
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(255, 240, 245));
        infoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(infoArea, gbc);

        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(255, 240, 245));

        JButton flightBtn = createActionButton("Manage Flights", new Color(220, 20, 60));
        flightBtn.addActionListener(e -> showFlightManagement());

        JButton routeBtn = createActionButton("Manage Routes", new Color(255, 140, 0));
        routeBtn.addActionListener(e -> showRouteManagement());

        JButton aircraftBtn = createActionButton("Manage Aircraft", new Color(70, 130, 180));
        aircraftBtn.addActionListener(e -> showAircraftManagement());

        JButton notificationsBtn = createActionButton("Send Notifications", new Color(106, 90, 205));
        notificationsBtn.addActionListener(e -> showNotifications());

        panel.add(flightBtn);
        panel.add(routeBtn);
        panel.add(aircraftBtn);
        panel.add(notificationsBtn);

        return panel;
    }

    private void showFlightManagement() {
        contentPanel.removeAll();
        FlightManagementPanel panel = new FlightManagementPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showRouteManagement() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.admin.RouteManagementPanel panel = new com.flightreservation.ui.panels.admin.RouteManagementPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAircraftManagement() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.admin.AircraftManagementPanel panel = new com.flightreservation.ui.panels.admin.AircraftManagementPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAirlineManagement() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.admin.AirlineManagementPanel panel = new com.flightreservation.ui.panels.admin.AirlineManagementPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showNotifications() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.admin.NotificationPanel panel = new com.flightreservation.ui.panels.admin.NotificationPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
