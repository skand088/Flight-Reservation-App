package com.flightreservation.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.flightreservation.controller.CustomerManagementController;
import com.flightreservation.model.Customer;

/**
 * Panel for managing customers (Agent view)
 */
public class CustomerManagementPanel extends JPanel {
    private final CustomerManagementController controller;
    private JTable customersTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public CustomerManagementPanel() {
        this.controller = new CustomerManagementController();
        initializeUI();
        loadAllCustomers();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and search
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("👥 Customer Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(createSearchPanel(), BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Table
        add(createTablePanel(), BorderLayout.CENTER);

        // Action buttons
        add(createActionPanel(), BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Search Customers"));

        panel.add(new JLabel("Search:"));
        searchField = new JTextField(30);
        panel.add(searchField);

        JButton searchButton = new JButton("🔍 Search");
        searchButton.addActionListener(e -> searchCustomers());
        panel.add(searchButton);

        JButton refreshButton = new JButton("🔄 Show All");
        refreshButton.addActionListener(e -> loadAllCustomers());
        panel.add(refreshButton);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton viewButton = new JButton("👁️ View Details");
        viewButton.addActionListener(e -> viewSelectedCustomer());
        panel.add(viewButton);

        JButton editButton = new JButton("✏️ Edit Customer");
        editButton.addActionListener(e -> editSelectedCustomer());
        panel.add(editButton);

        JButton suspendButton = new JButton("🚫 Suspend Account");
        suspendButton.addActionListener(e -> suspendSelectedCustomer());
        panel.add(suspendButton);

        JButton activateButton = new JButton("✅ Activate Account");
        activateButton.addActionListener(e -> activateSelectedCustomer());
        panel.add(activateButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = { "Customer ID", "Username", "Email", "Phone", "Frequent Flyer", "Loyalty Points",
                "Account Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customersTable = new JTable(tableModel);
        customersTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(customersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadAllCustomers() {
        tableModel.setRowCount(0);
        List<Customer> customers = controller.getAllCustomers();

        for (Customer customer : customers) {
            Object[] row = {
                    customer.getCustomerId(),
                    customer.getUser().getUsername(),
                    customer.getUser().getEmail(),
                    customer.getUser().getPhoneNumber() != null ? customer.getUser().getPhoneNumber() : "N/A",
                    customer.getFrequentFlyerNumber() != null ? customer.getFrequentFlyerNumber() : "N/A",
                    customer.getLoyaltyPoints(),
                    customer.getUser().getAccountStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void searchCustomers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllCustomers();
            return;
        }

        tableModel.setRowCount(0);
        List<Customer> customers = controller.searchCustomers(searchTerm);

        for (Customer customer : customers) {
            Object[] row = {
                    customer.getCustomerId(),
                    customer.getUser().getUsername(),
                    customer.getUser().getEmail(),
                    customer.getUser().getPhoneNumber() != null ? customer.getUser().getPhoneNumber() : "N/A",
                    customer.getFrequentFlyerNumber() != null ? customer.getFrequentFlyerNumber() : "N/A",
                    customer.getLoyaltyPoints(),
                    customer.getUser().getAccountStatus()
            };
            tableModel.addRow(row);
        }
    }

    private Customer getSelectedCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please select a customer from the table.",
                    "No Selection",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
        return controller.getCustomerById(customerId);
    }

    private void viewSelectedCustomer() {
        Customer customer = getSelectedCustomer();
        if (customer == null)
            return;

        String details = String.format(
                "Customer Details:\n\n" +
                        "Customer ID: %d\n" +
                        "Username: %s\n" +
                        "Email: %s\n" +
                        "Phone: %s\n" +
                        "Address: %s\n" +
                        "Frequent Flyer Number: %s\n" +
                        "Loyalty Points: %d\n" +
                        "Preferred Airline: %s\n" +
                        "Account Status: %s\n" +
                        "Role: %s",
                customer.getCustomerId(),
                customer.getUser().getUsername(),
                customer.getUser().getEmail(),
                customer.getUser().getPhoneNumber() != null ? customer.getUser().getPhoneNumber() : "N/A",
                customer.getAddress() != null ? customer.getAddress() : "N/A",
                customer.getFrequentFlyerNumber() != null ? customer.getFrequentFlyerNumber() : "N/A",
                customer.getLoyaltyPoints(),
                customer.getPreferredAirline() != null ? customer.getPreferredAirline() : "N/A",
                customer.getUser().getAccountStatus(),
                customer.getUser().getRole());

        javax.swing.JOptionPane.showMessageDialog(this, details, "Customer Details",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private void editSelectedCustomer() {
        Customer customer = getSelectedCustomer();
        if (customer == null)
            return;

        // Create edit dialog
        JPanel editPanel = new JPanel(new java.awt.GridLayout(0, 2, 10, 10));

        JTextField emailField = new JTextField(customer.getUser().getEmail());
        JTextField phoneField = new JTextField(
                customer.getUser().getPhoneNumber() != null ? customer.getUser().getPhoneNumber() : "");
        JTextField addressField = new JTextField(customer.getAddress() != null ? customer.getAddress() : "");
        JTextField ffNumberField = new JTextField(
                customer.getFrequentFlyerNumber() != null ? customer.getFrequentFlyerNumber() : "");
        JTextField loyaltyPointsField = new JTextField(String.valueOf(customer.getLoyaltyPoints()));
        JTextField preferredAirlineField = new JTextField(
                customer.getPreferredAirline() != null ? customer.getPreferredAirline() : "");

        editPanel.add(new JLabel("Email:"));
        editPanel.add(emailField);
        editPanel.add(new JLabel("Phone:"));
        editPanel.add(phoneField);
        editPanel.add(new JLabel("Address:"));
        editPanel.add(addressField);
        editPanel.add(new JLabel("Frequent Flyer Number:"));
        editPanel.add(ffNumberField);
        editPanel.add(new JLabel("Loyalty Points:"));
        editPanel.add(loyaltyPointsField);
        editPanel.add(new JLabel("Preferred Airline:"));
        editPanel.add(preferredAirlineField);

        int result = javax.swing.JOptionPane.showConfirmDialog(this, editPanel,
                "Edit Customer: " + customer.getUser().getUsername(),
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.PLAIN_MESSAGE);

        if (result == javax.swing.JOptionPane.OK_OPTION) {
            try {
                // Update user fields
                customer.getUser().setEmail(emailField.getText().trim());
                customer.getUser().setPhoneNumber(phoneField.getText().trim());

                // Update customer fields
                customer.setAddress(addressField.getText().trim());
                customer.setFrequentFlyerNumber(ffNumberField.getText().trim());
                customer.setLoyaltyPoints(Integer.parseInt(loyaltyPointsField.getText().trim()));
                customer.setPreferredAirline(preferredAirlineField.getText().trim());

                if (controller.updateCustomer(customer, customer.getUser())) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Customer updated successfully!",
                            "Success",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    loadAllCustomers();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Failed to update customer.",
                            "Error",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Invalid loyalty points value. Please enter a number.",
                        "Input Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Error updating customer: " + ex.getMessage(),
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void suspendSelectedCustomer() {
        Customer customer = getSelectedCustomer();
        if (customer == null)
            return;

        int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
                "Are you sure you want to suspend " + customer.getUser().getUsername() + "'s account?",
                "Confirm Suspension",
                javax.swing.JOptionPane.YES_NO_OPTION);

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            customer.getUser().setAccountStatus(com.flightreservation.model.User.AccountStatus.SUSPENDED);
            if (controller.updateCustomer(customer, customer.getUser())) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Account suspended successfully!",
                        "Success",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                loadAllCustomers();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Failed to suspend account.",
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void activateSelectedCustomer() {
        Customer customer = getSelectedCustomer();
        if (customer == null)
            return;

        customer.getUser().setAccountStatus(com.flightreservation.model.User.AccountStatus.ACTIVE);
        if (controller.updateCustomer(customer, customer.getUser())) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Account activated successfully!",
                    "Success",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            loadAllCustomers();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Failed to activate account.",
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}
