package com.example.payments.exception;

/**
 * Exception thrown when a payment status transition is not allowed.
 * Results in HTTP 409 Conflict.
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
