package com.flightreservation.observer;

import com.flightreservation.dao.CustomerDAO;
import com.flightreservation.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject in the Observer pattern
 * Manages notification observers (customers) and sends notifications
 */
public class NotificationSubject {
    private static final Logger logger = LoggerFactory.getLogger(NotificationSubject.class);
    private static NotificationSubject instance;
    private final List<NotificationObserver> observers;
    private final CustomerDAO customerDAO;

    private NotificationSubject() {
        this.observers = new ArrayList<>();
        this.customerDAO = new CustomerDAO();
    }

    public static synchronized NotificationSubject getInstance() {
        if (instance == null) {
            instance = new NotificationSubject();
        }
        return instance;
    }

    /**
     * Subscribe an observer to notifications
     */
    public void attach(NotificationObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            logger.info("Observer attached: {}", observer.getEmail());
        }
    }

    /**
     * Unsubscribe an observer from notifications
     */
    public void detach(NotificationObserver observer) {
        if (observers.remove(observer)) {
            logger.info("Observer detached: {}", observer.getEmail());
        }
    }

    /**
     * Load all customers as observers from database
     */
    public void loadAllCustomers() {
        observers.clear();
        List<Customer> customers = customerDAO.getAllCustomers();

        for (Customer customer : customers) {
            CustomerObserver observer = new CustomerObserver(customer);
            observers.add(observer);
        }

        logger.info("Loaded {} customers as observers", observers.size());
    }

    /**
     * Send notification to all subscribed observers
     */
    public void notifyObservers(String subject, String message, NotificationType type) {
        logger.info("Sending {} notification to {} observers: {}",
                type.getDisplayName(), observers.size(), subject);

        int successCount = 0;
        for (NotificationObserver observer : observers) {
            try {
                observer.update(subject, message, type);
                successCount++;
            } catch (Exception e) {
                logger.error("Failed to notify observer: {}", observer.getEmail(), e);
            }
        }

        logger.info("Successfully notified {}/{} observers", successCount, observers.size());
    }

    /**
     * Send notification to a specific observer by email
     */
    public void notifyObserver(String email, String subject, String message, NotificationType type) {
        for (NotificationObserver observer : observers) {
            if (observer.getEmail().equalsIgnoreCase(email)) {
                observer.update(subject, message, type);
                logger.info("Notification sent to: {}", email);
                return;
            }
        }
        logger.warn("Observer not found: {}", email);
    }

    /**
     * Get count of subscribed observers
     */
    public int getObserverCount() {
        return observers.size();
    }

    /**
     * Get list of all observers
     */
    public List<NotificationObserver> getObservers() {
        return new ArrayList<>(observers);
    }
}
