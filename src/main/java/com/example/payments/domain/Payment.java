package com.example.payments.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.payments.exception.InvalidStatusTransitionException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Domain entity representing a payment transaction.
 * Contains business logic for status transitions.
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Default constructor required by JPA
    public Payment() {
    }

    /**
     * Creates a new payment with PENDING status.
     */
    public Payment(String reference, String customerId, BigDecimal amount,
            Currency currency, PaymentMethod method) {
        this.reference = reference;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.method = method;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Transitions the payment to a new status following business rules:
     * - PENDING can transition to APPROVED or REJECTED
     * - APPROVED and REJECTED are final states
     * 
     * @param newStatus the target status
     * @throws InvalidStatusTransitionException if transition is not allowed
     */
    public void transitionTo(PaymentStatus newStatus) {
        if (this.status == PaymentStatus.PENDING) {
            if (newStatus == PaymentStatus.APPROVED || newStatus == PaymentStatus.REJECTED) {
                this.status = newStatus;
                return;
            }
        }
        throw new InvalidStatusTransitionException(
                String.format("Cannot transition from %s to %s", this.status, newStatus));
    }

    /**
     * Checks if transition to the given status is allowed.
     */
    public boolean canTransitionTo(PaymentStatus newStatus) {
        if (this.status == PaymentStatus.PENDING) {
            return newStatus == PaymentStatus.APPROVED || newStatus == PaymentStatus.REJECTED;
        }
        return false;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
