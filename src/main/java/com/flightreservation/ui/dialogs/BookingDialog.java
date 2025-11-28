package com.flightreservation.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flightreservation.controller.ReservationController;
import com.flightreservation.dao.SeatDAO;
import com.flightreservation.model.strategies.payment.BankTransferPaymentStrategy;
import com.flightreservation.model.strategies.payment.CreditCardPaymentStrategy;
import com.flightreservation.model.entities.Customer;
import com.flightreservation.model.strategies.payment.DebitCardPaymentStrategy;
import com.flightreservation.model.entities.Flight;
import com.flightreservation.model.entities.Passenger;
import com.flightreservation.model.strategies.payment.PayPalPaymentStrategy;
import com.flightreservation.model.strategies.payment.PaymentStrategy;
import com.flightreservation.model.entities.Reservation;
import com.flightreservation.model.entities.Seat;

public class BookingDialog extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(BookingDialog.class);

    private final Flight flight;
    private final Customer customer;
    private final SeatDAO seatDAO;
    private final ReservationController reservationController;

    private JTable seatsTable;
    private DefaultTableModel seatsTableModel;
    private List<Seat> availableSeats;
    private Seat selectedSeat;

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField ageField;
    private JTextField idNumberField;
    private JComboBox<String> idTypeCombo;
    private JTextField emailField;
    private JTextField phoneField;

    private JComboBox<String> paymentMethodCombo;
    private JLabel totalLabel;

    private JTextField cardNumberField;
    private JTextField cardHolderField;
    private JTextField expiryDateField;
    private JTextField cvvField;
    private JTextField accountNumberField;
    private JTextField routingNumberField;
    private JTextField bankNameField;
    private JTextField paypalEmailField;
    private JTextField paypalPasswordField;
    private JPanel paymentDetailsPanel;

    private boolean bookingConfirmed = false;

    public BookingDialog(Frame parent, Flight flight, Customer customer) {
        super(parent, "Book Flight - " + flight.getFlightNumber(), true);
        this.flight = flight;
        this.customer = customer;
        this.seatDAO = new SeatDAO();
        this.reservationController = new ReservationController();

        setSize(900, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadAvailableSeats();
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel flightSeatPanel = createFlightSeatPanel();
        tabbedPane.addTab("Flight & Seat Selection", flightSeatPanel);

        JPanel passengerPanel = createPassengerPanel();
        tabbedPane.addTab("Passenger Information", passengerPanel);

        JPanel paymentPanel = createPaymentPanel();
        tabbedPane.addTab("Payment", paymentPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFlightSeatPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel flightInfoPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        flightInfoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Flight Details"));

        flightInfoPanel.add(new JLabel("Flight Number:"));
        flightInfoPanel.add(new JLabel(flight.getFlightNumber()));

        if (flight.getAirline() != null) {
            flightInfoPanel.add(new JLabel("Airline:"));
            flightInfoPanel.add(new JLabel(flight.getAirline().getAirlineName()));
        }

        if (flight.getRoute() != null) {
            flightInfoPanel.add(new JLabel("Route:"));
            flightInfoPanel.add(new JLabel(
                    flight.getRoute().getOriginAirport() + " → " + flight.getRoute().getDestinationAirport()));
        }

        flightInfoPanel.add(new JLabel("Departure:"));
        flightInfoPanel.add(new JLabel(
                flight.getDepartureTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));

        flightInfoPanel.add(new JLabel("Arrival:"));
        flightInfoPanel.add(new JLabel(
                flight.getArrivalTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));

        flightInfoPanel.add(new JLabel("Base Price:"));
        flightInfoPanel.add(new JLabel("$" + String.format("%.2f", flight.getBasePrice())));

        JPanel seatPanel = new JPanel(new BorderLayout(10, 10));
        seatPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Select Your Seat"));

        String[] columnNames = { "Seat #", "Class", "Type", "Price", "Status" };
        seatsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        seatsTable = new JTable(seatsTableModel);
        seatsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        seatsTable.setRowHeight(25);
        seatsTable.getTableHeader().setReorderingAllowed(false);

        seatsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = seatsTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < availableSeats.size()) {
                    selectedSeat = availableSeats.get(selectedRow);
                    updateTotalPrice();
                }
            }
        });

        JScrollPane seatsScrollPane = new JScrollPane(seatsTable);
        seatsScrollPane.setPreferredSize(new Dimension(0, 200));
        seatPanel.add(seatsScrollPane, BorderLayout.CENTER);

        JButton refreshSeatsButton = new JButton("Refresh Seats");
        refreshSeatsButton.setBackground(new Color(0, 123, 255));
        refreshSeatsButton.setFocusPainted(false);
        refreshSeatsButton.addActionListener(e -> loadAvailableSeats());

        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshPanel.add(refreshSeatsButton);
        seatPanel.add(refreshPanel, BorderLayout.SOUTH);

        panel.add(flightInfoPanel, BorderLayout.NORTH);
        panel.add(seatPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPassengerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Passenger Details"));

        firstNameField = new JTextField();
        lastNameField = new JTextField();
        ageField = new JTextField();
        idNumberField = new JTextField();
        idTypeCombo = new JComboBox<>(new String[] { "PASSPORT", "NATIONAL_ID", "DRIVERS_LICENSE" });
        emailField = new JTextField(customer.getUser().getEmail());
        phoneField = new JTextField(
                customer.getUser().getPhoneNumber() != null ? customer.getUser().getPhoneNumber() : "");

        formPanel.add(createBoldLabel("First Name: *"));
        formPanel.add(firstNameField);
        formPanel.add(createBoldLabel("Last Name: *"));
        formPanel.add(lastNameField);
        formPanel.add(createBoldLabel("Age: *"));
        formPanel.add(ageField);
        formPanel.add(createBoldLabel("ID Type: *"));
        formPanel.add(idTypeCombo);
        formPanel.add(createBoldLabel("ID Number: *"));
        formPanel.add(idNumberField);
        formPanel.add(createBoldLabel("Contact Email:"));
        formPanel.add(emailField);
        formPanel.add(createBoldLabel("Contact Phone:"));
        formPanel.add(phoneField);

        panel.add(formPanel, BorderLayout.NORTH);

        JLabel infoLabel = new JLabel("<html><i>* Required fields<br><br>" +
                "Please ensure all passenger information matches official travel documents.</i></html>");
        infoLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        panel.add(infoLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Payment Information"));

        paymentMethodCombo = new JComboBox<>(new String[] { "CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER" });
        paymentMethodCombo.addActionListener(e -> updatePaymentFields());

        totalLabel = new JLabel("$" + String.format("%.2f", flight.getBasePrice()));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(new Color(40, 167, 69));

        formPanel.add(createBoldLabel("Payment Method: *"));
        formPanel.add(paymentMethodCombo);
        formPanel.add(createBoldLabel("Total Amount:"));
        formPanel.add(totalLabel);

        panel.add(formPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        paymentDetailsPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        paymentDetailsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Payment Details"));

        cardNumberField = new JTextField();
        cardHolderField = new JTextField();
        expiryDateField = new JTextField();
        cvvField = new JTextField();
        accountNumberField = new JTextField();
        routingNumberField = new JTextField();
        bankNameField = new JTextField();
        paypalEmailField = new JTextField();
        paypalPasswordField = new JTextField();

        updatePaymentFields();
        centerPanel.add(paymentDetailsPanel, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Booking Summary"));

        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryArea.setMargin(new Insets(10, 10, 10, 10));
        updateSummary(summaryArea);

        summaryPanel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        centerPanel.add(summaryPanel, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBorder(new EmptyBorder(5, 10, 10, 10));

        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.setBackground(new Color(40, 167, 69));
        confirmButton.setFocusPainted(false);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.setPreferredSize(new Dimension(150, 35));
        confirmButton.addActionListener(e -> processBooking());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setFocusPainted(false);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> dispose());

        panel.add(confirmButton);
        panel.add(cancelButton);

        return panel;
    }

    private void loadAvailableSeats() {
        try {
            logger.info("Loading seats for flight ID: {}", flight.getFlightId());
            availableSeats = seatDAO.getAvailableSeats(flight.getFlightId());
            seatsTableModel.setRowCount(0);

            if (availableSeats.isEmpty()) {
                logger.warn("No available seats found for flight {} ({})", flight.getFlightId(),
                        flight.getFlightNumber());
                JOptionPane.showMessageDialog(this,
                        "No seats are currently available for this flight.\n" +
                                "Flight ID: " + flight.getFlightId() + "\n" +
                                "Please try another flight or contact support.",
                        "No Seats Available",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (Seat seat : availableSeats) {
                seatsTableModel.addRow(new Object[] {
                        seat.getSeatNumber(),
                        seat.getSeatClass(),
                        seat.getSeatType(),
                        "$" + String.format("%.2f", seat.getPrice()),
                        seat.getStatus()
                });
            }

            logger.info("Loaded {} available seats for flight {}", availableSeats.size(), flight.getFlightId());
        } catch (Exception e) {
            logger.error("Error loading seats", e);
            JOptionPane.showMessageDialog(this,
                    "Error loading seat information: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processBooking() {
        if (selectedSeat == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a seat from the 'Flight & Seat Selection' tab.",
                    "No Seat Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (firstNameField.getText().trim().isEmpty() ||
                lastNameField.getText().trim().isEmpty() ||
                ageField.getText().trim().isEmpty() ||
                idNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required passenger fields (*).",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int age = Integer.parseInt(ageField.getText().trim());
            if (age < 1 || age > 120) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid age between 1 and 120.",
                        "Invalid Age",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Passenger passenger = new Passenger();
            passenger.setFirstName(firstNameField.getText().trim());
            passenger.setLastName(lastNameField.getText().trim());
            passenger.setAge(age);
            passenger.setIdNumber(idNumberField.getText().trim());
            passenger.setIdType(Passenger.IdType.valueOf((String) idTypeCombo.getSelectedItem()));
            passenger.setContactEmail(emailField.getText().trim());
            passenger.setContactPhone(phoneField.getText().trim());
            passenger.setSeatId(selectedSeat.getSeatId());

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirm booking?\n\n" +
                            "Passenger: " + passenger.getFirstName() + " " + passenger.getLastName() + "\n" +
                            "Seat: " + selectedSeat.getSeatNumber() + " (" + selectedSeat.getSeatClass() + ")\n" +
                            "Total: $" + String.format("%.2f", selectedSeat.getPrice()) + "\n" +
                            "Payment: " + paymentMethodCombo.getSelectedItem(),
                    "Confirm Booking",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            PaymentStrategy paymentStrategy = createPaymentStrategy();
            if (paymentStrategy == null) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all required payment fields.",
                        "Missing Payment Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            reservationController.setPaymentStrategy(paymentStrategy);

            List<Passenger> passengers = new ArrayList<>();
            passengers.add(passenger);

            Reservation reservation = reservationController.createReservation(
                    customer.getCustomerId(),
                    flight.getFlightId(),
                    passengers);

            reservationController.confirmReservation(reservation.getReservationId());

            bookingConfirmed = true;
            dispose();

            JOptionPane.showMessageDialog(getParent(),
                    "Booking Successful!\n\n" +
                            "Confirmation Number: " + reservation.getConfirmationNumber() + "\n" +
                            "Flight: " + flight.getFlightNumber() + "\n" +
                            "Seat: " + selectedSeat.getSeatNumber() + "\n" +
                            "Total Paid: $" + String.format("%.2f", reservation.getTotalFare()) + "\n\n" +
                            "A confirmation email has been sent to " + passenger.getContactEmail() + "\n" +
                            "You can view your reservation in 'My Reservations'.",
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Age must be a valid number.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            logger.error("Booking failed", ex);
            JOptionPane.showMessageDialog(this,
                    "Booking failed: " + ex.getMessage() +
                            "\n\nPlease try again or contact support.",
                    "Booking Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePaymentFields() {
        paymentDetailsPanel.removeAll();
        String selectedMethod = (String) paymentMethodCombo.getSelectedItem();

        switch (selectedMethod) {
            case "CREDIT_CARD":
            case "DEBIT_CARD":
                paymentDetailsPanel.add(createBoldLabel("Card Number: *"));
                paymentDetailsPanel.add(cardNumberField);
                paymentDetailsPanel.add(createBoldLabel("Card Holder: *"));
                paymentDetailsPanel.add(cardHolderField);
                paymentDetailsPanel.add(createBoldLabel("Expiry (MM/YY): *"));
                paymentDetailsPanel.add(expiryDateField);
                paymentDetailsPanel.add(createBoldLabel(selectedMethod.equals("CREDIT_CARD") ? "CVV: *" : "PIN: *"));
                paymentDetailsPanel.add(cvvField);
                break;
            case "PAYPAL":
                paymentDetailsPanel.add(createBoldLabel("PayPal Email: *"));
                paymentDetailsPanel.add(paypalEmailField);
                paymentDetailsPanel.add(createBoldLabel("PayPal Password: *"));
                paymentDetailsPanel.add(paypalPasswordField);
                break;
            case "BANK_TRANSFER":
                paymentDetailsPanel.add(createBoldLabel("Bank Name: *"));
                paymentDetailsPanel.add(bankNameField);
                paymentDetailsPanel.add(createBoldLabel("Account Number: *"));
                paymentDetailsPanel.add(accountNumberField);
                paymentDetailsPanel.add(createBoldLabel("Routing Number: *"));
                paymentDetailsPanel.add(routingNumberField);
                paymentDetailsPanel.add(createBoldLabel("Account Holder: *"));
                paymentDetailsPanel.add(cardHolderField);
                break;
        }

        paymentDetailsPanel.revalidate();
        paymentDetailsPanel.repaint();
    }

    private PaymentStrategy createPaymentStrategy() {
        String selectedMethod = (String) paymentMethodCombo.getSelectedItem();

        try {
            switch (selectedMethod) {
                case "CREDIT_CARD":
                    if (cardNumberField.getText().trim().isEmpty() ||
                            cardHolderField.getText().trim().isEmpty() ||
                            expiryDateField.getText().trim().isEmpty() ||
                            cvvField.getText().trim().isEmpty()) {
                        return null;
                    }
                    return new CreditCardPaymentStrategy(
                            cardNumberField.getText().trim(),
                            cardHolderField.getText().trim(),
                            expiryDateField.getText().trim(),
                            cvvField.getText().trim());

                case "DEBIT_CARD":
                    if (cardNumberField.getText().trim().isEmpty() ||
                            cardHolderField.getText().trim().isEmpty() ||
                            expiryDateField.getText().trim().isEmpty() ||
                            cvvField.getText().trim().isEmpty()) {
                        return null;
                    }
                    return new DebitCardPaymentStrategy(
                            cardNumberField.getText().trim(),
                            cardHolderField.getText().trim(),
                            expiryDateField.getText().trim(),
                            cvvField.getText().trim());

                case "PAYPAL":
                    if (paypalEmailField.getText().trim().isEmpty() ||
                            paypalPasswordField.getText().trim().isEmpty()) {
                        return null;
                    }
                    return new PayPalPaymentStrategy(
                            paypalEmailField.getText().trim(),
                            paypalPasswordField.getText().trim());

                case "BANK_TRANSFER":
                    if (bankNameField.getText().trim().isEmpty() ||
                            accountNumberField.getText().trim().isEmpty() ||
                            routingNumberField.getText().trim().isEmpty() ||
                            cardHolderField.getText().trim().isEmpty()) {
                        return null;
                    }
                    return new BankTransferPaymentStrategy(
                            bankNameField.getText().trim(),
                            accountNumberField.getText().trim(),
                            routingNumberField.getText().trim(),
                            cardHolderField.getText().trim());

                default:
                    return null;
            }
        } catch (Exception e) {
            logger.error("Error creating payment strategy", e);
            return null;
        }
    }

    private void updateTotalPrice() {
        if (selectedSeat != null) {
            totalLabel.setText("$" + String.format("%.2f", selectedSeat.getPrice()));
        }
    }

    private void updateSummary(JTextArea summaryArea) {
        StringBuilder summary = new StringBuilder();
        summary.append("BOOKING SUMMARY\n");
        summary.append("═".repeat(50)).append("\n\n");
        summary.append("Flight: ").append(flight.getFlightNumber()).append("\n");
        if (flight.getRoute() != null) {
            summary.append("Route: ").append(flight.getRoute().getOriginAirport())
                    .append(" → ").append(flight.getRoute().getDestinationAirport()).append("\n");
        }
        summary.append("Departure: ")
                .append(flight.getDepartureTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")))
                .append("\n\n");

        if (selectedSeat != null) {
            summary.append("Selected Seat: ").append(selectedSeat.getSeatNumber()).append("\n");
            summary.append("Class: ").append(selectedSeat.getSeatClass()).append("\n");
            summary.append("Type: ").append(selectedSeat.getSeatType()).append("\n");
            summary.append("Price: $").append(String.format("%.2f", selectedSeat.getPrice())).append("\n\n");
        } else {
            summary.append("Selected Seat: Not yet selected\n\n");
        }

        summary.append("─".repeat(50)).append("\n");
        summary.append("TOTAL: $").append(
                selectedSeat != null ? String.format("%.2f", selectedSeat.getPrice())
                        : String.format("%.2f", flight.getBasePrice()));

        summaryArea.setText(summary.toString());
    }

    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }

    public boolean isBookingConfirmed() {
        return bookingConfirmed;
    }
}
