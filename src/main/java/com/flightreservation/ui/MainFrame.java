package com.flightreservation.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window for the Flight Reservation System
 */
public class MainFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

    private JPanel mainPanel;
    private JMenuBar menuBar;

    public MainFrame() {
        initializeUI();
        logger.info("Main frame initialized");
    }

    private void initializeUI() {
        setTitle("Flight Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create menu bar
        createMenuBar();

        // Create main panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add welcome panel
        JPanel welcomePanel = createWelcomePanel();
        mainPanel.add(welcomePanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Reservations Menu
        JMenu reservationsMenu = new JMenu("Reservations");
        JMenuItem newReservationItem = new JMenuItem("New Reservation");
        JMenuItem viewReservationsItem = new JMenuItem("View Reservations");
        JMenuItem searchFlightsItem = new JMenuItem("Search Flights");

        newReservationItem.addActionListener(
                e -> JOptionPane.showMessageDialog(this, "New Reservation feature - To be implemented"));
        viewReservationsItem.addActionListener(
                e -> JOptionPane.showMessageDialog(this, "View Reservations feature - To be implemented"));
        searchFlightsItem.addActionListener(
                e -> JOptionPane.showMessageDialog(this, "Search Flights feature - To be implemented"));

        reservationsMenu.add(newReservationItem);
        reservationsMenu.add(viewReservationsItem);
        reservationsMenu.add(searchFlightsItem);
        menuBar.add(reservationsMenu);

        // Customers Menu
        JMenu customersMenu = new JMenu("Customers");
        JMenuItem manageCustomersItem = new JMenuItem("Manage Customers");
        manageCustomersItem.addActionListener(
                e -> JOptionPane.showMessageDialog(this, "Manage Customers feature - To be implemented"));
        customersMenu.add(manageCustomersItem);
        menuBar.add(customersMenu);

        // Flights Menu
        JMenu flightsMenu = new JMenu("Flights");
        JMenuItem manageFlightsItem = new JMenuItem("Manage Flights");
        manageFlightsItem.addActionListener(
                e -> JOptionPane.showMessageDialog(this, "Manage Flights feature - To be implemented"));
        flightsMenu.add(manageFlightsItem);
        menuBar.add(flightsMenu);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Flight Reservation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(25, 25, 112));
        panel.add(titleLabel, gbc);

        // Subtitle
        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Welcome to your flight booking portal");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(105, 105, 105));
        panel.add(subtitleLabel, gbc);

        // Status label
        gbc.gridy++;
        gbc.insets = new Insets(30, 10, 10, 10);
        JLabel statusLabel = new JLabel("✓ Database Connected");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(34, 139, 34));
        panel.add(statusLabel, gbc);

        // Instructions
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        JTextArea instructionsArea = new JTextArea(
                "Getting Started:\n\n" +
                        "• Use the Reservations menu to book flights\n" +
                        "• Manage customer information from the Customers menu\n" +
                        "• Add and update flights from the Flights menu\n" +
                        "• Search for available flights and make reservations\n\n" +
                        "This is a team collaboration project. Use Git for version control.");
        instructionsArea.setEditable(false);
        instructionsArea.setBackground(new Color(240, 248, 255));
        instructionsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionsArea.setBorder(BorderFactory.createEmptyBorder());
        panel.add(instructionsArea, gbc);

        return panel;
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Flight Reservation System\n" +
                        "Version 1.0.0\n\n" +
                        "A Java Swing application for managing flight reservations\n" +
                        "with MySQL database backend.\n\n" +
                        "Built for team collaboration using Maven and Git.",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
