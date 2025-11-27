package com.flightreservation.observer;

import com.flightreservation.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete Observer implementation for customers
 * Receives and processes notifications
 */
public class CustomerObserver implements NotificationObserver {
    private static final Logger logger = LoggerFactory.getLogger(CustomerObserver.class);
    private final Customer customer;

    public CustomerObserver(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void update(String subject, String message, NotificationType type) {
        // In a real system, this would send email, SMS, or push notification
        // For this dummy implementation, we just log it

        logger.info("📧 Notification sent to {}", customer.getUser().getEmail());
        logger.info("   Type: {}", type.getDisplayName());
        logger.info("   Subject: {}", subject);
        logger.info("   Recipient: {}", customer.getUser().getUsername());

        // Simulate email sending
        simulateSendEmail(subject, message, type);
    }

    private void simulateSendEmail(String subject, String message, NotificationType type) {
        // Dummy implementation - just creates a formatted notification
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

        // In production, this would use JavaMail API or similar
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
