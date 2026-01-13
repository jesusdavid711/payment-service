package com.example.payments.exception;

/**
 * Exception thrown when a payment is not found.
 * Results in HTTP 404 Not Found.
 */
public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(Long id) {
        super(String.format("Payment not found with id: %d", id));
    }
}
