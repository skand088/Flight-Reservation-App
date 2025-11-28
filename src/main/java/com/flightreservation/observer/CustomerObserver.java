package com.flightreservation.observer;

import com.flightreservation.model.entities.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer implementation for customers
 * receives and process notifs
 * not real emails for our implementation
 */
public class CustomerObserver implements NotificationObserver {
    private static final Logger logger = LoggerFactory.getLogger(CustomerObserver.class);
    private final Customer customer;

    public CustomerObserver(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void update(String subject, String message, NotificationType type) {
        // should send an email in real life - mimicked here

        logger.info("Notification sent to {}", customer.getUser().getEmail());
        logger.info("   Type: {}", type.getDisplayName());
        logger.info("   Subject: {}", subject);
        logger.info("   Recipient: {}", customer.getUser().getUsername());

        simulateSendEmail(subject, message, type);
    }

    private void simulateSendEmail(String subject, String message, NotificationType type) {
        // dummy implementation - just creates a formatted notification
        StringBuilder email = new StringBuilder();
        email.append("═══════════════════════════════════════════\n");
        email.append("  FLIGHT RESERVATION SYSTEM NOTIFICATION\n");
        email.append("═══════════════════════════════════════════\n\n");
        email.append("To: ").append(customer.getUser().getEmail()).append("\n");
        email.append("Customer: ").append(customer.getUser().getUsername()).append("\n");
        email.append("Type: ").append(type.getDisplayName()).append("\n\n");
        email.append("Subject: ").append(subject).append("\n\n");
        email.append("Message:\n");
        email.append(message).append("\n\n");
        email.append("═══════════════════════════════════════════\n");

        logger.debug("Email content:\n{}", email.toString());
    }

    @Override
    public String getEmail() {
        return customer.getUser().getEmail();
    }

    @Override
    public String getName() {
        return customer.getUser().getUsername();
    }

    public Customer getCustomer() {
        return customer;
    }
}
