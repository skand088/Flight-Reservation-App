package com.flightreservation.model.strategies.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebitCardPaymentStrategy implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(DebitCardPaymentStrategy.class);

    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String pin;

    public DebitCardPaymentStrategy(String cardNumber, String cardHolderName,
            String expiryDate, String pin) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.pin = pin;
    }

    @Override
    public boolean processPayment(double amount) {
        logger.info("Debit Card Payment Strategy used for amount: ${}", amount);
        return true;
    }

    @Override
    public boolean validatePaymentDetails() {
        if (cardNumber == null || cardNumber.length() < 13 || cardNumber.length() > 19) {
            return false;
        }

        if (cardHolderName == null || cardHolderName.trim().isEmpty()) {
            logger.warn("Card holder name is required");
            return false;
        }

        if (expiryDate == null || !expiryDate.matches("\\d{2}/\\d{2}")) {
            logger.warn("Invalid expiry date format");
            return false;
        }

        if (pin == null || !pin.matches("\\d{4}")) {
            logger.warn("Invalid PIN. Must be 4 digits");
            return false;
        }

        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Debit Card";
    }

    @Override
    public String getPaymentInfo() {
        return String.format("Debit Card ending in %s - %s",
                getMaskedCardNumber(), cardHolderName);
    }

    private String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return cardNumber.substring(cardNumber.length() - 4);
    }
}
