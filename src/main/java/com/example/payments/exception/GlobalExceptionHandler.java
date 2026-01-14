package com.example.payments.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.stream.Collectors;

/**
 * Global exception handler for REST API errors.
 * Maps application exceptions to appropriate HTTP responses.
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof PaymentNotFoundException) {
            return buildResponse(Response.Status.NOT_FOUND, "PAYMENT_NOT_FOUND", exception.getMessage());
        }

        if (exception instanceof DuplicateReferenceException) {
            return buildResponse(Response.Status.CONFLICT, "DUPLICATE_REFERENCE", exception.getMessage());
        }

        if (exception instanceof InvalidStatusTransitionException) {
            return buildResponse(Response.Status.CONFLICT, "INVALID_STATUS_TRANSITION", exception.getMessage());
        }

        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            String message = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            return buildResponse(Response.Status.BAD_REQUEST, "VALIDATION_ERROR", message);
        }

        // Generic internal server error
        return buildResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                exception.getMessage());
    }

    private Response buildResponse(Response.Status status, String code, String message) {
        ErrorResponse errorResponse = new ErrorResponse(code, message);
        return Response.status(status).entity(errorResponse).build();
    }
}
