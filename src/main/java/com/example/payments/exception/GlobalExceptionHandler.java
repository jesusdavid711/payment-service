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
        String path = uriInfo != null ? uriInfo.getPath() : "";

        if (exception instanceof PaymentNotFoundException) {
            return buildResponse(Response.Status.NOT_FOUND, "Not Found", exception.getMessage(), path);
        }

        if (exception instanceof DuplicateReferenceException) {
            return buildResponse(Response.Status.CONFLICT, "Conflict", exception.getMessage(), path);
        }

        if (exception instanceof InvalidStatusTransitionException) {
            return buildResponse(Response.Status.CONFLICT, "Conflict", exception.getMessage(), path);
        }

        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            String message = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            return buildResponse(Response.Status.BAD_REQUEST, "Validation Error", message, path);
        }

        // Generic internal server error
        return buildResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                exception.getMessage(),
                path);
    }

    private Response buildResponse(Response.Status status, String error, String message, String path) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.getStatusCode(),
                error,
                message,
                path);
        return Response.status(status).entity(errorResponse).build();
    }
}
