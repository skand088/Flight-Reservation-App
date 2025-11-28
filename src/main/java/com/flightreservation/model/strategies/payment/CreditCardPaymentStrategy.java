package com.flightreservation.model.strategies.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreditCardPaymentStrategy implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(CreditCardPaymentStrategy.class);

    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;

    public CreditCardPaymentStrategy(String cardNumber, String cardHolderName,
            String expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    @Override
    public boolean processPayment(double amount) {
        logger.info("Credit Card Payment Strategy used for amount: ${}", amount);
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
            logger.warn("Invalid expiry date format. Expected MM/YY");
            return false;
        }

        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            logger.warn("Invalid CVV");
            return false;
        }

        return isValidCardNumber(cardNumber);
    }

    @Override
    public String getPaymentMethodName() {
        return "Credit Card";
    }

    @Override
    public String getPaymentInfo() {
        return String.format("Credit Card ending in %s - %s",
                getMaskedCardNumber(), cardHolderName);
    }

    private String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return cardNumber.substring(cardNumber.length() - 4);
    }

    private boolean isValidCardNumber(String cardNumber) {
        String cleanNumber = cardNumber.replaceAll("\\D", "");

        int sum = 0;
        boolean alternate = false;

        for (int i = cleanNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cleanNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }
}
