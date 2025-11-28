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
import javax.swing.SwingConstants;

import com.flightreservation.dao.CustomerDAO;
import com.flightreservation.model.entities.Customer;
import com.flightreservation.ui.panels.customer.FlightSearchPanel;
import com.flightreservation.ui.panels.customer.ReservationsPanel;

/**
 * Customer Dashboard - Search flights, make/cancel reservations, view booking
 * history
 */
public class CustomerDashboard extends BaseDashboard {

    @Override
    protected String getDashboardTitle() {
        return "Flight Reservation System - Customer Dashboard";
    }

    @Override
    protected Color getBackgroundColor() {
        return new Color(240, 248, 255);
    }

    @Override
    protected String getRoleDisplayName() {
        return "Customer";
    }

    @Override
    protected void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(33, 147, 176));

        JMenu flightsMenu = createStyledMenu("Flights");
        JMenuItem searchFlightsItem = createStyledMenuItem("Search Flights");
        searchFlightsItem.addActionListener(e -> showSearchFlights());
        flightsMenu.add(searchFlightsItem);
        menuBar.add(flightsMenu);

        JMenu reservationsMenu = createStyledMenu("My Reservations");
        JMenuItem viewReservationsItem = createStyledMenuItem("View All Reservations");
        viewReservationsItem.addActionListener(e -> showMyReservations());
        reservationsMenu.add(viewReservationsItem);
        menuBar.add(reservationsMenu);

        JMenu newslettersMenu = createStyledMenu("Newsletters");
        JMenuItem viewNewslettersItem = createStyledMenuItem("View Newsletters");
        viewNewslettersItem.addActionListener(e -> showNewsletters());
        newslettersMenu.add(viewNewslettersItem);
        menuBar.add(newslettersMenu);

        JMenu profileMenu = createStyledMenu("Profile");
        JMenuItem viewProfileItem = createStyledMenuItem("View Profile");
        viewProfileItem.addActionListener(e -> showProfile());
        profileMenu.add(viewProfileItem);
        menuBar.add(profileMenu);

        JMenu accountMenu = createStyledMenu("Account");
        JMenuItem logoutItem = createStyledMenuItem("Logout");
        logoutItem.addActionListener(e -> handleLogout());
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);

        setJMenuBar(menuBar);
    }

    @Override
    protected JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(25, 25, 112));
        panel.add(welcomeLabel, gbc);

        gbc.gridy++;
        JLabel roleLabel = new JLabel("Customer Portal");
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
                "Quick Actions:\n\n" +
                        "• Search for available flights\n" +
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

        JButton searchBtn = createActionButton("Search Flights", new Color(33, 147, 176));
        searchBtn.addActionListener(e -> showSearchFlights());

        JButton reservationsBtn = createActionButton("My Reservations", new Color(76, 175, 80));
        reservationsBtn.addActionListener(e -> showMyReservations());

        JButton newslettersBtn = createActionButton("Newsletters", new Color(255, 152, 0));
        newslettersBtn.addActionListener(e -> showNewsletters());

        panel.add(searchBtn);
        panel.add(reservationsBtn);
        panel.add(newslettersBtn);

        return panel;
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

    private void showNewsletters() {
        contentPanel.removeAll();
        com.flightreservation.ui.panels.customer.NewslettersPanel newslettersPanel = new com.flightreservation.ui.panels.customer.NewslettersPanel();
        contentPanel.add(newslettersPanel, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
