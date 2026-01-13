package com.example.payments.infrastructure.rest;

import java.time.LocalDateTime;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.example.payments.application.PaymentService;
import com.example.payments.domain.Payment;
import com.example.payments.domain.PaymentStatus;
import com.example.payments.dto.CreatePaymentRequest;
import com.example.payments.dto.PagedResponse;
import com.example.payments.dto.PaymentResponse;
import com.example.payments.dto.UpdateStatusRequest;
import com.example.payments.exception.ErrorResponse;
import com.example.payments.exception.PaymentNotFoundException;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST controller for payment operations.
 * This is the primary adapter in hexagonal architecture.
 */
@Path("/api/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Payments", description = "Payment management operations")
public class PaymentResource {

    private final PaymentService paymentService;

    @Inject
    public PaymentResource(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @POST
    @Operation(summary = "Create a new payment", description = "Creates a new payment with PENDING status. The status field is ignored if sent.")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Payment created successfully", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @APIResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "409", description = "Duplicate reference", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response createPayment(
            @RequestBody(description = "Payment data", required = true, content = @Content(schema = @Schema(implementation = CreatePaymentRequest.class))) @Valid CreatePaymentRequest request) {

        PaymentResponse response = paymentService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves a payment by its unique identifier")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Payment found", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @APIResponse(responseCode = "404", description = "Payment not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response getPaymentById(
            @Parameter(description = "Payment ID", required = true) @PathParam("id") Long id) {

        Payment payment = paymentService.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        return Response.ok(PaymentResponse.fromEntity(payment)).build();
    }

    @GET
    @Operation(summary = "List payments with filters", description = "Retrieves a paginated list of payments with optional filters")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Paginated list of payments", content = @Content(schema = @Schema(implementation = PagedResponse.class)))
    })
    public Response listPayments(
            @Parameter(description = "Filter by status") @QueryParam("status") PaymentStatus status,

            @Parameter(description = "Filter by customer ID") @QueryParam("customerId") String customerId,

            @Parameter(description = "Filter by creation date from (ISO format)") @QueryParam("from") LocalDateTime from,

            @Parameter(description = "Filter by creation date to (ISO format)") @QueryParam("to") LocalDateTime to,

            @Parameter(description = "Page number (0-indexed)", required = true) @QueryParam("page") @DefaultValue("0") @Min(0) int page,

            @Parameter(description = "Page size", required = true) @QueryParam("size") @DefaultValue("10") @Min(1) int size) {

        PagedResponse<PaymentResponse> response = paymentService.findAll(
                status, customerId, from, to, page, size);

        return Response.ok(response).build();
    }

    @PATCH
    @Path("/{id}/status")
    @Operation(summary = "Update payment status", description = "Updates the status of a payment. Only PENDING payments can be transitioned to APPROVED or REJECTED.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Status updated successfully", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @APIResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Payment not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "409", description = "Invalid status transition", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response updatePaymentStatus(
            @Parameter(description = "Payment ID", required = true) @PathParam("id") Long id,

            @RequestBody(description = "New status", required = true, content = @Content(schema = @Schema(implementation = UpdateStatusRequest.class))) @Valid UpdateStatusRequest request) {

        PaymentResponse response = paymentService.updateStatus(id, request.getStatus());
        return Response.ok(response).build();
    }
}
