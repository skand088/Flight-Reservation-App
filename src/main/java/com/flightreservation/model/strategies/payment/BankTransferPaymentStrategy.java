package com.flightreservation.model.strategies.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankTransferPaymentStrategy implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BankTransferPaymentStrategy.class);

    private String bankName;
    private String accountNumber;
    private String routingNumber;
    private String accountHolderName;

    public BankTransferPaymentStrategy(String bankName, String accountNumber,
            String routingNumber, String accountHolderName) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.routingNumber = routingNumber;
        this.accountHolderName = accountHolderName;
    }

    @Override
    public boolean processPayment(double amount) {
        logger.info("Bank Transfer Payment Strategy used for amount: ${}", amount);
        return true;
    }

    @Override
    public boolean validatePaymentDetails() {
        if (bankName == null || bankName.trim().isEmpty()) {
            return false;
        }

        if (accountNumber == null || !accountNumber.matches("\\d{8,17}")) {
            logger.warn("Invalid account number. Must be 8-17 digits");
            return false;
        }

        if (routingNumber == null || !routingNumber.matches("\\d{9}")) {
            logger.warn("Invalid routing number. Must be 9 digits");
            return false;
        }

        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            logger.warn("Account holder name is required");
            return false;
        }

        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Bank Transfer";
    }

    @Override
    public String getPaymentInfo() {
        return String.format("Bank Transfer from %s - %s (Account ending in %s)",
                bankName, accountHolderName, getMaskedAccountNumber());
    }

    private String getMaskedAccountNumber() {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        return accountNumber.substring(accountNumber.length() - 4);
    }
}
