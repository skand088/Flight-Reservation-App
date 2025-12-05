package com.flightreservation.ui.panels.admin;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.flightreservation.dao.AirlineDAO;
import com.flightreservation.model.entities.Airline;
import com.flightreservation.ui.panels.common.BaseManagementPanel;

public class AirlineManagementPanel extends BaseManagementPanel<Airline> {
    private final AirlineDAO airlineDAO;

    public AirlineManagementPanel() {
        this.airlineDAO = new AirlineDAO();
        loadData();
    }

    @Override
    protected String getTitle() {
        return "Airline Management";
    }

    @Override
    protected String getTableTitle() {
        return "All Airlines";
    }

    @Override
    protected String getEntityName() {
        return "Airline";
    }

    @Override
    protected JTable createTable() {
        String[] columns = { "Airline ID", "Airline Name", "Code", "Contact Info" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable airlineTable = new JTable(model);
        airlineTable.setRowHeight(25);
        airlineTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        airlineTable.getColumnModel().getColumn(3).setPreferredWidth(200);

        return airlineTable;
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        List<Airline> airlines = airlineDAO.getAllAirlines();

        for (Airline airline : airlines) {
            Object[] row = {
                    airline.getAirlineId(),
                    airline.getAirlineName(),
                    airline.getAirlineCode(),
                    airline.getContactInfo() != null ? airline.getContactInfo() : ""
            };
            tableModel.addRow(row);
        }
    }

    @Override
    protected Airline getItemFromRow(int row) {
        int airlineId = (int) tableModel.getValueAt(row, 0);
        return airlineDAO.getAirlineById(airlineId);
    }

    @Override
    protected void add() {
        JPanel addPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField codeField = new JTextField();
        JTextField contactField = new JTextField();

        addPanel.add(new JLabel("Airline Name:"));
        addPanel.add(nameField);
        addPanel.add(new JLabel("Airline Code (2-3 letters):"));
        addPanel.add(codeField);
        addPanel.add(new JLabel("Contact Info:"));
        addPanel.add(contactField);

        int result = JOptionPane.showConfirmDialog(this, addPanel,
                "Add New Airline",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String code = codeField.getText().trim().toUpperCase();
            String contact = contactField.getText().trim();

            if (name.isEmpty() || code.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Airline name and code are required.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (code.length() < 2 || code.length() > 3) {
                JOptionPane.showMessageDialog(this,
                        "Airline code must be 2-3 letters.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (airlineDAO.isAirlineCodeExists(code)) {
                JOptionPane.showMessageDialog(this,
                        "An airline with code '" + code + "' already exists.",
                        "Duplicate Code",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Airline airline = new Airline();
            airline.setAirlineName(name);
            airline.setAirlineCode(code);
            airline.setContactInfo(contact.isEmpty() ? null : contact);

            if (airlineDAO.createAirline(airline)) {
                JOptionPane.showMessageDialog(this,
                        "Airline added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add airline. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    protected void edit() {
        Airline airline = getSelectedItem();
        if (airline == null) {
            return;
        }

        JPanel editPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField nameField = new JTextField(airline.getAirlineName());
        JTextField codeField = new JTextField(airline.getAirlineCode());
        JTextField contactField = new JTextField(airline.getContactInfo() != null ? airline.getContactInfo() : "");

        editPanel.add(new JLabel("Airline Name:"));
        editPanel.add(nameField);
        editPanel.add(new JLabel("Airline Code:"));
        editPanel.add(codeField);
        editPanel.add(new JLabel("Contact Info:"));
        editPanel.add(contactField);

        int result = JOptionPane.showConfirmDialog(this, editPanel,
                "Edit Airline",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String code = codeField.getText().trim().toUpperCase();
            String contact = contactField.getText().trim();

            if (name.isEmpty() || code.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Airline name and code are required.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            airline.setAirlineName(name);
            airline.setAirlineCode(code);
            airline.setContactInfo(contact.isEmpty() ? null : contact);

            if (airlineDAO.updateAirline(airline)) {
                JOptionPane.showMessageDialog(this,
                        "Airline updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update airline. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    protected void delete() {
        Airline airline = getSelectedItem();
        if (airline == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete airline:\n" +
                        airline.getAirlineName() + " (" + airline.getAirlineCode() + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (airlineDAO.deleteAirline(airline.getAirlineId())) {
                JOptionPane.showMessageDialog(this,
                        "Airline deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete airline. It may be in use by existing flights.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
