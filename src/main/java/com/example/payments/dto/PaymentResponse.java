package com.example.payments.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.example.payments.domain.Currency;
import com.example.payments.domain.Payment;
import com.example.payments.domain.PaymentMethod;
import com.example.payments.domain.PaymentStatus;

/**
 * Response DTO for payment data.
 */
@Schema(description = "Payment response data")
public class PaymentResponse {

    @Schema(description = "Payment ID", examples = { "1" })
    private Long id;

    @Schema(description = "Unique payment reference", examples = { "PAY-2024-001" })
    private String reference;

    @Schema(description = "Customer identifier", examples = { "CUST-12345" })
    private String customerId;

    @Schema(description = "Payment amount", examples = { "150000.00" })
    private BigDecimal amount;

    @Schema(description = "Payment currency", examples = { "COP" })
    private Currency currency;

    @Schema(description = "Payment method", examples = { "CARD" })
    private PaymentMethod method;

    @Schema(description = "Payment status", examples = { "PENDING" })
    private PaymentStatus status;

    @Schema(description = "Payment creation timestamp")
    private LocalDateTime createdAt;

    public PaymentResponse() {
    }

    /**
     * Creates a response DTO from a Payment entity.
     */
    public static PaymentResponse fromEntity(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setReference(payment.getReference());
        response.setCustomerId(payment.getCustomerId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setMethod(payment.getMethod());
        response.setStatus(payment.getStatus());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
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
