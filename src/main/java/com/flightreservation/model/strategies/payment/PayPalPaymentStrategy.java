package com.flightreservation.model.strategies.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayPalPaymentStrategy implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(PayPalPaymentStrategy.class);

    private String email;
    private String password;

    public PayPalPaymentStrategy(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean processPayment(double amount) {
        logger.info("PayPal Payment Strategy used for amount: ${}", amount);
        return true;
    }

    @Override
    public boolean validatePaymentDetails() {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }

        if (password == null || password.length() < 6) {
            return false;
        }

        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "PayPal";
    }

    @Override
    public String getPaymentInfo() {
        return "PayPal account: " + email;
    }
}
