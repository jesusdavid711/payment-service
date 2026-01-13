package com.example.payments.dto;

import java.math.BigDecimal;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.example.payments.domain.Currency;
import com.example.payments.domain.PaymentMethod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for creating a new payment.
 */
@Schema(description = "Request payload for creating a new payment")
public class CreatePaymentRequest {

    @NotBlank(message = "Reference is required")
    @Schema(description = "Unique payment reference", example = "PAY-2024-001")
    private String reference;

    @NotBlank(message = "Customer ID is required")
    @Schema(description = "Customer identifier", example = "CUST-12345")
    private String customerId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Schema(description = "Payment amount (must be positive)", example = "150000.00")
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    @Schema(description = "Payment currency", example = "COP")
    private Currency currency;

    @NotNull(message = "Payment method is required")
    @Schema(description = "Payment method", example = "CARD")
    private PaymentMethod method;

    // Getters and Setters

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
}
