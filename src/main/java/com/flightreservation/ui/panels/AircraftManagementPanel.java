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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.flightreservation.dao.AircraftDAO;
import com.flightreservation.model.Aircraft;

/**
 * Panel for managing aircraft (Admin view)
 */
public class AircraftManagementPanel extends JPanel {
    private final AircraftDAO aircraftDAO;
    private JTable aircraftTable;
    private DefaultTableModel tableModel;

    public AircraftManagementPanel() {
        this.aircraftDAO = new AircraftDAO();
        initializeUI();
        loadAircraft();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("🛩 Aircraft Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        add(createTablePanel(), BorderLayout.CENTER);

        // Buttons
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("All Aircraft"));

        String[] columns = { "Aircraft ID", "Tail Number", "Model", "Manufacturer", "Total Seats", "Configuration" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        aircraftTable = new JTable(tableModel);
        aircraftTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(aircraftTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton addButton = new JButton("➕ Add Aircraft");
        addButton.addActionListener(e -> addAircraft());
        panel.add(addButton);

        JButton editButton = new JButton("✏️ Edit Aircraft");
        editButton.addActionListener(e -> editSelectedAircraft());
        panel.add(editButton);

        JButton deleteButton = new JButton("🗑️ Delete Aircraft");
        deleteButton.addActionListener(e -> deleteSelectedAircraft());
        panel.add(deleteButton);

        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> loadAircraft());
        panel.add(refreshButton);

        return panel;
    }

    private void loadAircraft() {
        tableModel.setRowCount(0);
        List<Aircraft> aircraftList = aircraftDAO.getAllAircraft();

        for (Aircraft aircraft : aircraftList) {
            Object[] row = {
                    aircraft.getAircraftId(),
                    aircraft.getTailNumber(),
                    aircraft.getModel(),
                    aircraft.getManufacturer(),
                    aircraft.getTotalSeats(),
                    aircraft.getSeatConfiguration()
            };
            tableModel.addRow(row);
        }
    }

    private Aircraft getSelectedAircraft() {
        int selectedRow = aircraftTable.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please select an aircraft from the table.",
                    "No Selection",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int aircraftId = (int) tableModel.getValueAt(selectedRow, 0);
        return aircraftDAO.getAircraftById(aircraftId);
    }

    private void addAircraft() {
        JPanel addPanel = new JPanel(new java.awt.GridLayout(0, 2, 10, 10));

        javax.swing.JTextField tailNumberField = new javax.swing.JTextField();
        javax.swing.JTextField modelField = new javax.swing.JTextField();
        javax.swing.JTextField manufacturerField = new javax.swing.JTextField();
        javax.swing.JTextField totalSeatsField = new javax.swing.JTextField();
        javax.swing.JTextField configField = new javax.swing.JTextField();

        addPanel.add(new JLabel("Tail Number:"));
        addPanel.add(tailNumberField);
        addPanel.add(new JLabel("Model:"));
        addPanel.add(modelField);
        addPanel.add(new JLabel("Manufacturer:"));
        addPanel.add(manufacturerField);
        addPanel.add(new JLabel("Total Seats:"));
        addPanel.add(totalSeatsField);
        addPanel.add(new JLabel("Seat Configuration:"));
        addPanel.add(configField);

        int result = javax.swing.JOptionPane.showConfirmDialog(this, addPanel,
                "Add New Aircraft",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.PLAIN_MESSAGE);

        if (result == javax.swing.JOptionPane.OK_OPTION) {
            try {
                Aircraft aircraft = new Aircraft();
                aircraft.setTailNumber(tailNumberField.getText().trim());
                aircraft.setModel(modelField.getText().trim());
                aircraft.setManufacturer(manufacturerField.getText().trim());
                aircraft.setTotalSeats(Integer.parseInt(totalSeatsField.getText().trim()));
                aircraft.setSeatConfiguration(configField.getText().trim());

                if (aircraftDAO.createAircraft(aircraft)) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Aircraft added successfully!",
                            "Success",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    loadAircraft();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Failed to add aircraft.",
                            "Error",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Total seats must be a valid number.",
                        "Input Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedAircraft() {
        Aircraft aircraft = getSelectedAircraft();
        if (aircraft == null)
            return;

        JPanel editPanel = new JPanel(new java.awt.GridLayout(0, 2, 10, 10));

        javax.swing.JTextField tailNumberField = new javax.swing.JTextField(aircraft.getTailNumber());
        javax.swing.JTextField modelField = new javax.swing.JTextField(aircraft.getModel());
        javax.swing.JTextField manufacturerField = new javax.swing.JTextField(aircraft.getManufacturer());
        javax.swing.JTextField totalSeatsField = new javax.swing.JTextField(String.valueOf(aircraft.getTotalSeats()));
        javax.swing.JTextField configField = new javax.swing.JTextField(aircraft.getSeatConfiguration());

        editPanel.add(new JLabel("Tail Number:"));
        editPanel.add(tailNumberField);
        editPanel.add(new JLabel("Model:"));
        editPanel.add(modelField);
        editPanel.add(new JLabel("Manufacturer:"));
        editPanel.add(manufacturerField);
        editPanel.add(new JLabel("Total Seats:"));
        editPanel.add(totalSeatsField);
        editPanel.add(new JLabel("Seat Configuration:"));
        editPanel.add(configField);

        int result = javax.swing.JOptionPane.showConfirmDialog(this, editPanel,
                "Edit Aircraft",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.PLAIN_MESSAGE);

        if (result == javax.swing.JOptionPane.OK_OPTION) {
            try {
                aircraft.setTailNumber(tailNumberField.getText().trim());
                aircraft.setModel(modelField.getText().trim());
                aircraft.setManufacturer(manufacturerField.getText().trim());
                aircraft.setTotalSeats(Integer.parseInt(totalSeatsField.getText().trim()));
                aircraft.setSeatConfiguration(configField.getText().trim());

                if (aircraftDAO.updateAircraft(aircraft)) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Aircraft updated successfully!",
                            "Success",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    loadAircraft();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Failed to update aircraft.",
                            "Error",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Total seats must be a valid number.",
                        "Input Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedAircraft() {
        Aircraft aircraft = getSelectedAircraft();
        if (aircraft == null)
            return;

        int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this aircraft?\n" +
                        aircraft.getTailNumber() + " (" + aircraft.getModel() + ")",
                "Confirm Delete",
                javax.swing.JOptionPane.YES_NO_OPTION);

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            if (aircraftDAO.deleteAircraft(aircraft.getAircraftId())) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Aircraft deleted successfully!",
                        "Success",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                loadAircraft();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Failed to delete aircraft. It may be used by existing flights.",
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
