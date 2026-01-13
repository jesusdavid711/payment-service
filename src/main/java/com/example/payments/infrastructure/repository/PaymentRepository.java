package com.example.payments.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.payments.domain.Payment;
import com.example.payments.domain.PaymentStatus;

/**
 * Port (interface) for payment persistence operations.
 * This abstracts the persistence mechanism from the domain.
 */
public interface PaymentRepository {

        /**
         * Saves a payment entity.
         * 
         * @param payment the payment to save
         * @return the saved payment with generated ID
         */
        Payment save(Payment payment);

        /**
         * Finds a payment by its ID.
         * 
         * @param id the payment ID
         * @return the payment if found
         */
        Optional<Payment> findPaymentById(Long id);

        /**
         * Checks if a payment with the given reference exists.
         * 
         * @param reference the payment reference
         * @return true if exists, false otherwise
         */
        boolean existsByReference(String reference);

        /**
         * Finds payments with optional filters and pagination.
         * 
         * @param status     filter by status (nullable)
         * @param customerId filter by customer ID (nullable)
         * @param from       filter by creation date from (nullable)
         * @param to         filter by creation date to (nullable)
         * @param page       page number (0-indexed)
         * @param size       page size
         * @return list of matching payments
         */
        List<Payment> findByFilters(
                        PaymentStatus status,
                        String customerId,
                        LocalDateTime from,
                        LocalDateTime to,
                        int page,
                        int size);

        /**
         * Counts payments matching the filters (for pagination).
         * 
         * @param status     filter by status (nullable)
         * @param customerId filter by customer ID (nullable)
         * @param from       filter by creation date from (nullable)
         * @param to         filter by creation date to (nullable)
         * @return total count of matching payments
         */
        long countByFilters(
                        PaymentStatus status,
                        String customerId,
                        LocalDateTime from,
                        LocalDateTime to);
}
