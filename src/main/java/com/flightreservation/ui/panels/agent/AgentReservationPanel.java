package com.flightreservation.ui.panels.agent;

import com.flightreservation.dao.CustomerDAO;
import com.flightreservation.dao.FlightDAO;
import com.flightreservation.model.entities.Customer;
import com.flightreservation.model.entities.Flight;
import com.flightreservation.ui.dialogs.BookingDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AgentReservationPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(AgentReservationPanel.class);

    private final CustomerDAO customerDAO;
    private final FlightDAO flightDAO;

    private JTextField customerSearchField;
    private JTable customersTable;
    private DefaultTableModel customersTableModel;
    private Customer selectedCustomer;

    private JTextField originField;
    private JTextField destinationField;
    private JTextField departureDateField;
    private JTable flightsTable;
    private DefaultTableModel flightsTableModel;
    private List<Flight> searchResults;

    public AgentReservationPanel() {
        this.customerDAO = new CustomerDAO();
        this.flightDAO = new FlightDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
    }

    private void initComponents() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.3);

        JPanel customerPanel = createCustomerSelectionPanel();
        splitPane.setTopComponent(customerPanel);

        JPanel flightPanel = createFlightSearchPanel();
        splitPane.setBottomComponent(flightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createCustomerSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                "Step 1: Select Customer"));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Search Customer (Username/Email):"));
        customerSearchField = new JTextField(25);
        searchPanel.add(customerSearchField);

        JButton searchCustomerBtn = new JButton("Search");
        searchCustomerBtn.setBackground(new Color(0, 123, 255));
        searchCustomerBtn.setFocusPainted(false);
        searchCustomerBtn.addActionListener(e -> searchCustomers());
        searchPanel.add(searchCustomerBtn);

        JButton loadAllBtn = new JButton("Load All Customers");
        loadAllBtn.setBackground(new Color(108, 117, 125));
        loadAllBtn.setFocusPainted(false);
        loadAllBtn.addActionListener(e -> loadAllCustomers());
        searchPanel.add(loadAllBtn);

        panel.add(searchPanel, BorderLayout.NORTH);

        String[] columnNames = { "Customer ID", "Username", "Email", "Phone", "Loyalty Points" };
        customersTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customersTable = new JTable(customersTableModel);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customersTable.setRowHeight(25);

        customersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int customerId = (int) customersTableModel.getValueAt(selectedRow, 0);
                    selectedCustomer = customerDAO.getCustomerById(customerId);
                    logger.info("Selected customer: {}", selectedCustomer.getUser().getUsername());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(customersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFlightSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                "Step 2: Search & Book Flight"));

        JPanel searchForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        searchForm.add(new JLabel("Origin:"));
        originField = new JTextField(10);
        searchForm.add(originField);

        searchForm.add(new JLabel("Destination:"));
        destinationField = new JTextField(10);
        searchForm.add(destinationField);

        searchForm.add(new JLabel("Departure Date (YYYY-MM-DD):"));
        departureDateField = new JTextField(12);
        departureDateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        searchForm.add(departureDateField);

        JButton searchBtn = new JButton("Search Flights");
        searchBtn.setBackground(new Color(40, 167, 69));
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> searchFlights());
        searchForm.add(searchBtn);

        JButton bookBtn = new JButton("Book Selected Flight");
        bookBtn.setBackground(new Color(255, 193, 7));
        bookBtn.setForeground(Color.BLACK);
        bookBtn.setFocusPainted(false);
        bookBtn.addActionListener(e -> bookFlight());
        searchForm.add(bookBtn);

        panel.add(searchForm, BorderLayout.NORTH);

        String[] columnNames = { "Flight #", "Airline", "Route", "Departure", "Arrival", "Price", "Seats", "Status" };
        flightsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(flightsTableModel);
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(flightsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void searchCustomers() {
        String searchTerm = customerSearchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a search term (username or email).",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        customersTableModel.setRowCount(0);
        List<Customer> customers = customerDAO.searchCustomers(searchTerm);

        if (customers.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No customers found matching: " + searchTerm,
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Customer customer : customers) {
            customersTableModel.addRow(new Object[] {
                    customer.getCustomerId(),
                    customer.getUser().getUsername(),
                    customer.getUser().getEmail(),
                    customer.getUser().getPhoneNumber() != null ? customer.getUser().getPhoneNumber() : "N/A",
                    customer.getLoyaltyPoints()
            });
        }

        logger.info("Found {} customers", customers.size());
    }

    private void loadAllCustomers() {
        customersTableModel.setRowCount(0);
        List<Customer> customers = customerDAO.getAllCustomers();

        for (Customer customer : customers) {
            customersTableModel.addRow(new Object[] {
                    customer.getCustomerId(),
                    customer.getUser().getUsername(),
                    customer.getUser().getEmail(),
                    customer.getUser().getPhoneNumber() != null ? customer.getUser().getPhoneNumber() : "N/A",
                    customer.getLoyaltyPoints()
            });
        }

        logger.info("Loaded {} customers", customers.size());
    }

    private void searchFlights() {
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        String dateStr = departureDateField.getText().trim();

        if (origin.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both origin and destination.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate departureDate = LocalDate.parse(dateStr);
            flightsTableModel.setRowCount(0);
            searchResults = flightDAO.searchFlights(origin, destination, departureDate.atStartOfDay());

            if (searchResults.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No flights found for the specified criteria.",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd HH:mm");
            for (Flight flight : searchResults) {
                flightsTableModel.addRow(new Object[] {
                        flight.getFlightNumber(),
                        flight.getAirline() != null ? flight.getAirline().getAirlineName() : "N/A",
                        origin + " → " + destination,
                        flight.getDepartureTime().format(formatter),
                        flight.getArrivalTime().format(formatter),
                        "$" + String.format("%.2f", flight.getBasePrice()),
                        flight.getAvailableSeats(),
                        flight.getStatus()
                });
            }

            logger.info("Found {} flights", searchResults.size());
        } catch (Exception e) {
            logger.error("Error searching flights", e);
            JOptionPane.showMessageDialog(this,
                    "Error searching flights: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookFlight() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer first (Step 1).",
                    "Customer Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a flight to book.",
                    "Flight Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Flight selectedFlight = searchResults.get(selectedRow);

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        BookingDialog dialog = new BookingDialog(parentFrame, selectedFlight, selectedCustomer);
        dialog.setVisible(true);

        if (dialog.isBookingConfirmed()) {
            searchFlights();
            JOptionPane.showMessageDialog(this,
                    "Reservation created successfully for " + selectedCustomer.getUser().getUsername() + "!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
