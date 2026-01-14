package com.example.payments.infrastructure.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

/**
 * Integration tests for Payment REST endpoints.
 * Uses H2 in-memory database via test profile.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaymentResourceIT {

    private static Long createdPaymentId;

    @Test
    @Order(1)
    @DisplayName("POST /api/payments - should create payment with PENDING status")
    void shouldCreatePayment() {
        String requestBody = """
                {
                    "reference": "TEST-REF-001",
                    "customerId": "CUST-123",
                    "amount": 150000.00,
                    "currency": "COP",
                    "method": "CARD"
                }
                """;

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/payments")
                .then()
                .statusCode(201)
                .body("reference", equalTo("TEST-REF-001"))
                .body("customerId", equalTo("CUST-123"))
                .body("amount", equalTo(150000.00f))
                .body("currency", equalTo("COP"))
                .body("method", equalTo("CARD"))
                .body("status", equalTo("PENDING"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue())
                .extract().path("id");

        createdPaymentId = id.longValue();
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/payments - should return 400 for invalid amount")
    void shouldReturn400ForInvalidAmount() {
        String requestBody = """
                {
                    "reference": "TEST-REF-002",
                    "customerId": "CUST-123",
                    "amount": -100.00,
                    "currency": "COP",
                    "method": "CARD"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/payments")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/payments - should return 400 for missing required fields")
    void shouldReturn400ForMissingFields() {
        String requestBody = """
                {
                    "reference": "TEST-REF-003"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/payments")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/payments - should return 409 for duplicate reference")
    void shouldReturn409ForDuplicateReference() {
        String requestBody = """
                {
                    "reference": "TEST-REF-001",
                    "customerId": "CUST-456",
                    "amount": 200.00,
                    "currency": "USD",
                    "method": "PSE"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/payments")
                .then()
                .statusCode(409)
                .body("code", equalTo("DUPLICATE_REFERENCE"))
                .body("message", containsString("already exists"));
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/payments/{id} - should return payment by ID")
    void shouldGetPaymentById() {
        given()
                .when()
                .get("/api/payments/{id}", createdPaymentId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdPaymentId.intValue()))
                .body("reference", equalTo("TEST-REF-001"))
                .body("status", equalTo("PENDING"));
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/payments/{id} - should return 404 for non-existent payment")
    void shouldReturn404ForNonExistentPayment() {
        given()
                .when()
                .get("/api/payments/{id}", 99999)
                .then()
                .statusCode(404)
                .body("code", equalTo("PAYMENT_NOT_FOUND"))
                .body("message", containsString("not found"));
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/payments - should return paginated list")
    void shouldReturnPaginatedList() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/payments")
                .then()
                .statusCode(200)
                .body("content", hasSize(greaterThanOrEqualTo(1)))
                .body("page", equalTo(0))
                .body("size", equalTo(10))
                .body("totalElements", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(8)
    @DisplayName("GET /api/payments - should filter by status")
    void shouldFilterByStatus() {
        given()
                .queryParam("status", "PENDING")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/payments")
                .then()
                .statusCode(200)
                .body("content.status", everyItem(equalTo("PENDING")));
    }

    @Test
    @Order(9)
    @DisplayName("PATCH /api/payments/{id}/status - should transition to APPROVED")
    void shouldTransitionToApproved() {
        String requestBody = """
                {
                    "status": "APPROVED"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/api/payments/{id}/status", createdPaymentId)
                .then()
                .statusCode(200)
                .body("status", equalTo("APPROVED"));
    }

    @Test
    @Order(10)
    @DisplayName("PATCH /api/payments/{id}/status - should return 409 for invalid transition")
    void shouldReturn409ForInvalidTransition() {
        String requestBody = """
                {
                    "status": "REJECTED"
                }
                """;

        // Already APPROVED, cannot transition to REJECTED
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/api/payments/{id}/status", createdPaymentId)
                .then()
                .statusCode(409)
                .body("code", equalTo("INVALID_STATUS_TRANSITION"))
                .body("message", containsString("Cannot transition"));
    }

    @Test
    @Order(11)
    @DisplayName("PATCH /api/payments/{id}/status - should return 404 for non-existent payment")
    void shouldReturn404WhenUpdatingNonExistentPayment() {
        String requestBody = """
                {
                    "status": "APPROVED"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/api/payments/{id}/status", 99999)
                .then()
                .statusCode(404)
                .body("code", equalTo("PAYMENT_NOT_FOUND"));
    }
}
