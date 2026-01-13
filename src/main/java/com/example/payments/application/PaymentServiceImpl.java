package com.example.payments.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.payments.domain.Payment;
import com.example.payments.domain.PaymentStatus;
import com.example.payments.dto.CreatePaymentRequest;
import com.example.payments.dto.PagedResponse;
import com.example.payments.dto.PaymentResponse;
import com.example.payments.exception.DuplicateReferenceException;
import com.example.payments.exception.PaymentNotFoundException;
import com.example.payments.infrastructure.repository.PaymentRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Implementation of payment use cases.
 * Orchestrates domain logic and persistence.
 */
@ApplicationScoped
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Inject
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public PaymentResponse create(CreatePaymentRequest request) {
        // Check for duplicate reference
        if (paymentRepository.existsByReference(request.getReference())) {
            throw new DuplicateReferenceException(request.getReference());
        }

        // Create new payment with PENDING status (ignoring any status from frontend)
        Payment payment = new Payment(
                request.getReference(),
                request.getCustomerId(),
                request.getAmount(),
                request.getCurrency(),
                request.getMethod());

        // Persist and return response
        Payment savedPayment = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(savedPayment);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findPaymentById(id);
    }

    @Override
    public PagedResponse<PaymentResponse> findAll(
            PaymentStatus status,
            String customerId,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size) {

        // Get paginated payments with filters
        List<Payment> payments = paymentRepository.findByFilters(
                status, customerId, from, to, page, size);

        // Get total count for pagination
        long totalElements = paymentRepository.countByFilters(
                status, customerId, from, to);

        // Convert to response DTOs
        List<PaymentResponse> content = payments.stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, page, size, totalElements);
    }

    @Override
    @Transactional
    public PaymentResponse updateStatus(Long id, PaymentStatus newStatus) {
        // Find payment or throw 404
        Payment payment = paymentRepository.findPaymentById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        // Apply domain logic for status transition (may throw
        // InvalidStatusTransitionException)
        payment.transitionTo(newStatus);

        // Persist and return updated payment
        Payment updatedPayment = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(updatedPayment);
    }
}
