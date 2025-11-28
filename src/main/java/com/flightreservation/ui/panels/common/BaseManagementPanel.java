package com.flightreservation.ui.panels.common;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public abstract class BaseManagementPanel<T> extends JPanel {
    protected JTable table;
    protected DefaultTableModel tableModel;

    public BaseManagementPanel() {
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createTitleLabel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel(getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        return titleLabel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(getTableTitle()));

        table = createTable();
        tableModel = (DefaultTableModel) table.getModel();

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton addButton = new JButton("Add " + getEntityName());
        addButton.addActionListener(e -> add());
        panel.add(addButton);

        JButton editButton = new JButton("Edit " + getEntityName());
        editButton.addActionListener(e -> edit());
        panel.add(editButton);

        JButton deleteButton = new JButton("Delete " + getEntityName());
        deleteButton.addActionListener(e -> delete());
        panel.add(deleteButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadData());
        panel.add(refreshButton);

        return panel;
    }

    protected T getSelectedItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a " + getEntityName().toLowerCase() + " from the table.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return getItemFromRow(selectedRow);
    }

    protected abstract String getTitle();
    protected abstract String getTableTitle();
    protected abstract String getEntityName();
    protected abstract JTable createTable();
    protected abstract void loadData();
    protected abstract T getItemFromRow(int row);
    protected abstract void add();
    protected abstract void edit();
    protected abstract void delete();
}
