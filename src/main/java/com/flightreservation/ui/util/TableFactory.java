package com.flightreservation.ui.util;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Factory for creating standardized JTable components.
 * Eliminates duplication of table setup code across panels.
 */
public class TableFactory {

    /**
     * Creates a read-only table with standard configuration.
     * 
     * @param columns Column names for the table
     * @return Configured JTable with non-editable cells
     */
    public static JTable createReadOnlyTable(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        return table;
    }

    /**
     * Creates a read-only table with auto-sorting enabled.
     * 
     * @param columns Column names for the table
     * @return Configured JTable with sorting capability
     */
    public static JTable createSortableTable(String[] columns) {
        JTable table = createReadOnlyTable(columns);
        table.setAutoCreateRowSorter(true);
        return table;
    }

    /**
     * Creates a read-only table wrapped in a JScrollPane.
     * 
     * @param columns Column names for the table
     * @return JScrollPane containing the configured table
     */
    public static JScrollPane createScrollableTable(String[] columns) {
        JTable table = createReadOnlyTable(columns);
        return new JScrollPane(table);
    }

    /**
     * Creates a sortable read-only table wrapped in a JScrollPane.
     * 
     * @param columns Column names for the table
     * @return JScrollPane containing the configured sortable table
     */
    public static JScrollPane createScrollableSortableTable(String[] columns) {
        JTable table = createSortableTable(columns);
        return new JScrollPane(table);
    }

    /**
     * Gets the table model from a JTable.
     * 
     * @param table The JTable to extract the model from
     * @return DefaultTableModel from the table
     */
    public static DefaultTableModel getTableModel(JTable table) {
        return (DefaultTableModel) table.getModel();
    }
}
