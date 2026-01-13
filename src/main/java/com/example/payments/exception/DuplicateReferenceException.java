package com.example.payments.exception;

/**
 * Exception thrown when attempting to create a payment with a duplicate
 * reference.
 * Results in HTTP 409 Conflict.
 */
public class DuplicateReferenceException extends RuntimeException {

    public DuplicateReferenceException(String reference) {
        super(String.format("Payment with reference '%s' already exists", reference));
    }
}
