package com.example.payments.application;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.payments.domain.Payment;
import com.example.payments.domain.PaymentStatus;
import com.example.payments.dto.CreatePaymentRequest;
import com.example.payments.dto.PagedResponse;
import com.example.payments.dto.PaymentResponse;

/**
 * Port (interface) defining payment use cases.
 * This is the primary port in hexagonal architecture.
 */
public interface PaymentService {

    /**
     * Creates a new payment with PENDING status.
     * Business rules:
     * - Amount must be positive (validated via Bean Validation)
     * - Reference must be unique
     * - Status is always set to PENDING regardless of input
     * 
     * @param request the payment creation request
     * @return the created payment response
     */
    PaymentResponse create(CreatePaymentRequest request);

    /**
     * Finds a payment by its ID.
     * 
     * @param id the payment ID
     * @return the payment if found
     */
    Optional<Payment> findById(Long id);

    /**
     * Lists payments with optional filters and mandatory pagination.
     * 
     * @param status     filter by status (optional)
     * @param customerId filter by customer ID (optional)
     * @param from       filter by creation date from (optional)
     * @param to         filter by creation date to (optional)
     * @param page       page number (0-indexed)
     * @param size       page size
     * @return paginated list of payments
     */
    PagedResponse<PaymentResponse> findAll(
            PaymentStatus status,
            String customerId,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size);

    /**
     * Updates the status of a payment.
     * Business rules:
     * - PENDING can transition to APPROVED or REJECTED
     * - APPROVED and REJECTED are final states
     * 
     * @param id        the payment ID
     * @param newStatus the target status
     * @return the updated payment response
     */
    PaymentResponse updateStatus(Long id, PaymentStatus newStatus);
}
