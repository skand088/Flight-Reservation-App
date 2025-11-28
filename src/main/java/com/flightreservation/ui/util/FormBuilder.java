package com.flightreservation.ui.util;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Builder for creating form panels with standardized layout.
 * Eliminates duplication of form creation code across dialogs.
 */
public class FormBuilder {
    private final JPanel panel;
    private final Map<String, JComponent> fields;
    private int rows;

    /**
     * Creates a new FormBuilder with default settings.
     * Uses GridLayout with 2 columns and 10px gaps.
     */
    public FormBuilder() {
        this.panel = new JPanel();
        this.fields = new HashMap<>();
        this.rows = 0;
        updateLayout();
    }

    /**
     * Adds a text field to the form.
     * 
     * @param label     Label text (will add colon automatically)
     * @param fieldName Internal name to retrieve the field later
     * @return This builder for method chaining
     */
    public FormBuilder addTextField(String label, String fieldName) {
        return addTextField(label, fieldName, "");
    }

    /**
     * Adds a text field with initial value to the form.
     * 
     * @param label        Label text
     * @param fieldName    Internal name to retrieve the field later
     * @param initialValue Initial text value
     * @return This builder for method chaining
     */
    public FormBuilder addTextField(String label, String fieldName, String initialValue) {
        JTextField textField = new JTextField(initialValue);
        addFieldToPanel(label, fieldName, textField);
        return this;
    }

    /**
     * Adds a combo box to the form.
     * 
     * @param label     Label text
     * @param fieldName Internal name to retrieve the field later
     * @param items     Items for the combo box
     * @return This builder for method chaining
     */
    public FormBuilder addComboBox(String label, String fieldName, Object[] items) {
        JComboBox<Object> comboBox = new JComboBox<>(items);
        addFieldToPanel(label, fieldName, comboBox);
        return this;
    }

    /**
     * Adds a combo box with typed items to the form.
     * 
     * @param <T>       Type of items in combo box
     * @param label     Label text
     * @param fieldName Internal name to retrieve the field later
     * @param items     Items for the combo box
     * @return This builder for method chaining
     */
    public <T> FormBuilder addTypedComboBox(String label, String fieldName, T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        addFieldToPanel(label, fieldName, comboBox);
        return this;
    }

    /**
     * Adds a text area to the form.
     * 
     * @param label     Label text
     * @param fieldName Internal name to retrieve the field later
     * @param rows      Number of rows for text area
     * @param cols      Number of columns for text area
     * @return This builder for method chaining
     */
    public FormBuilder addTextArea(String label, String fieldName, int rows, int cols) {
        JTextArea textArea = new JTextArea(rows, cols);
        addFieldToPanel(label, fieldName, textArea);
        return this;
    }

    /**
     * Adds a custom component to the form.
     * 
     * @param label     Label text
     * @param fieldName Internal name to retrieve the field later
     * @param component Custom JComponent
     * @return This builder for method chaining
     */
    public FormBuilder addCustomField(String label, String fieldName, JComponent component) {
        addFieldToPanel(label, fieldName, component);
        return this;
    }

    /**
     * Internal method to add field with label to panel.
     */
    private void addFieldToPanel(String label, String fieldName, JComponent component) {
        panel.add(new JLabel(label + ":"));
        panel.add(component);
        fields.put(fieldName, component);
        rows++;
        updateLayout();
    }

    /**
     * Updates the panel layout based on current number of rows.
     */
    private void updateLayout() {
        panel.setLayout(new GridLayout(rows, 2, 10, 10));
    }

    /**
     * Gets a field by its name.
     * 
     * @param fieldName Name of the field
     * @return JComponent or null if not found
     */
    public JComponent getField(String fieldName) {
        return fields.get(fieldName);
    }

    /**
     * Gets a text field value by field name.
     * 
     * @param fieldName Name of the field
     * @return Text value or empty string if not found or wrong type
     */
    public String getTextValue(String fieldName) {
        JComponent component = fields.get(fieldName);
        if (component instanceof JTextField) {
            return ((JTextField) component).getText().trim();
        } else if (component instanceof JTextArea) {
            return ((JTextArea) component).getText().trim();
        }
        return "";
    }

    /**
     * Gets a combo box selected item by field name.
     * 
     * @param fieldName Name of the field
     * @return Selected item or null if not found or wrong type
     */
    public Object getSelectedItem(String fieldName) {
        JComponent component = fields.get(fieldName);
        if (component instanceof JComboBox) {
            return ((JComboBox<?>) component).getSelectedItem();
        }
        return null;
    }

    /**
     * Sets a text field value.
     * 
     * @param fieldName Name of the field
     * @param value     Value to set
     * @return This builder for method chaining
     */
    public FormBuilder setTextValue(String fieldName, String value) {
        JComponent component = fields.get(fieldName);
        if (component instanceof JTextField) {
            ((JTextField) component).setText(value);
        } else if (component instanceof JTextArea) {
            ((JTextArea) component).setText(value);
        }
        return this;
    }

    /**
     * Sets a combo box selected item.
     * 
     * @param fieldName Name of the field
     * @param item      Item to select
     * @return This builder for method chaining
     */
    public FormBuilder setSelectedItem(String fieldName, Object item) {
        JComponent component = fields.get(fieldName);
        if (component instanceof JComboBox) {
            ((JComboBox<?>) component).setSelectedItem(item);
        }
        return this;
    }

    /**
     * Builds and returns the completed form panel.
     * 
     * @return JPanel containing all form fields
     */
    public JPanel build() {
        return panel;
    }

    /**
     * Gets all field names in the form.
     * 
     * @return Map of field names to components
     */
    public Map<String, JComponent> getAllFields() {
        return new HashMap<>(fields);
    }
}
