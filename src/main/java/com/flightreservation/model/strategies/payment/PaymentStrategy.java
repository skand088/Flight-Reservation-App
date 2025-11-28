package com.flightreservation.model.strategies.payment;

/**
 * strategy pattern for payment methods
 */
public interface PaymentStrategy {

    /**
     * process payment using the specific payment strategy
     * 
     * @param amount
     * @return true if payment successful
     */
    boolean processPayment(double amount);

    /**
     * validate payment details before processing
     * 
     * @return true if payment details valid, false otherwise
     */
    boolean validatePaymentDetails();

    /**
     * name of this payment method
     * 
     * @return
     */
    String getPaymentMethodName();

    /**
     *
     * @return payment information string
     */
    String getPaymentInfo();
}
