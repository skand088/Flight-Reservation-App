package com.flightreservation.ui.panels.admin;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.flightreservation.dao.AircraftDAO;
import com.flightreservation.model.entities.Aircraft;
import com.flightreservation.ui.panels.common.BaseManagementPanel;
import com.flightreservation.ui.util.TableFactory;

public class AircraftManagementPanel extends BaseManagementPanel<Aircraft> {
    private final AircraftDAO aircraftDAO;

    public AircraftManagementPanel() {
        this.aircraftDAO = new AircraftDAO();
        loadData();
    }

    @Override
    protected String getTitle() {
        return "Aircraft Management";
    }

    @Override
    protected String getTableTitle() {
        return "All Aircraft";
    }

    @Override
    protected String getEntityName() {
        return "Aircraft";
    }

    @Override
    protected JTable createTable() {
        String[] columns = { "Aircraft ID", "Tail Number", "Model", "Manufacturer", "Total Seats", "Configuration" };
        return TableFactory.createReadOnlyTable(columns);
    }

    @Override
    protected void loadData() {
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

    @Override
    protected Aircraft getItemFromRow(int row) {
        int aircraftId = (int) tableModel.getValueAt(row, 0);
        return aircraftDAO.getAircraftById(aircraftId);
    }

    @Override
    protected void add() {
        JPanel addPanel = new JPanel(new java.awt.GridLayout(0, 2, 10, 10));

        JTextField tailNumberField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField manufacturerField = new JTextField();
        JTextField totalSeatsField = new JTextField();
        JTextField configField = new JTextField();

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

        int result = JOptionPane.showConfirmDialog(this, addPanel,
                "Add New Aircraft",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Aircraft aircraft = new Aircraft();
                aircraft.setTailNumber(tailNumberField.getText().trim());
                aircraft.setModel(modelField.getText().trim());
                aircraft.setManufacturer(manufacturerField.getText().trim());
                aircraft.setTotalSeats(Integer.parseInt(totalSeatsField.getText().trim()));
                aircraft.setSeatConfiguration(configField.getText().trim());

                if (aircraftDAO.createAircraft(aircraft)) {
                    JOptionPane.showMessageDialog(this,
                            "Aircraft added successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to add aircraft.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Total seats must be a valid number.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    protected void edit() {
        Aircraft aircraft = getSelectedItem();
        if (aircraft == null)
            return;

        JPanel editPanel = new JPanel(new java.awt.GridLayout(0, 2, 10, 10));

        JTextField tailNumberField = new JTextField(aircraft.getTailNumber());
        JTextField modelField = new JTextField(aircraft.getModel());
        JTextField manufacturerField = new JTextField(aircraft.getManufacturer());
        JTextField totalSeatsField = new JTextField(String.valueOf(aircraft.getTotalSeats()));
        JTextField configField = new JTextField(aircraft.getSeatConfiguration());

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

        int result = JOptionPane.showConfirmDialog(this, editPanel,
                "Edit Aircraft",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                aircraft.setTailNumber(tailNumberField.getText().trim());
                aircraft.setModel(modelField.getText().trim());
                aircraft.setManufacturer(manufacturerField.getText().trim());
                aircraft.setTotalSeats(Integer.parseInt(totalSeatsField.getText().trim()));
                aircraft.setSeatConfiguration(configField.getText().trim());

                if (aircraftDAO.updateAircraft(aircraft)) {
                    JOptionPane.showMessageDialog(this,
                            "Aircraft updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update aircraft.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Total seats must be a valid number.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    protected void delete() {
        Aircraft aircraft = getSelectedItem();
        if (aircraft == null)
            return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this aircraft?\n" +
                        aircraft.getTailNumber() + " (" + aircraft.getModel() + ")",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (aircraftDAO.deleteAircraft(aircraft.getAircraftId())) {
                JOptionPane.showMessageDialog(this,
                        "Aircraft deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete aircraft. It may be used by existing flights.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
