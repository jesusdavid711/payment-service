package com.example.payments.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.example.payments.domain.PaymentStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating payment status.
 */
@Schema(description = "Request payload for updating payment status")
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    @Schema(description = "New payment status (APPROVED or REJECTED)", example = "APPROVED")
    private PaymentStatus status;

    public UpdateStatusRequest() {
    }

    public UpdateStatusRequest(PaymentStatus status) {
        this.status = status;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
