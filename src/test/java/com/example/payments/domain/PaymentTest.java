package com.example.payments.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.example.payments.exception.InvalidStatusTransitionException;

/**
 * Unit tests for Payment entity domain logic.
 * Tests status transition business rules.
 */
class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment(
                "REF-001",
                "CUST-123",
                new BigDecimal("100.00"),
                Currency.COP,
                PaymentMethod.CARD);
    }

    @Nested
    @DisplayName("Payment Creation")
    class PaymentCreation {

        @Test
        @DisplayName("New payment should have PENDING status")
        void newPaymentShouldHavePendingStatus() {
            assertEquals(PaymentStatus.PENDING, payment.getStatus());
        }

        @Test
        @DisplayName("New payment should have createdAt timestamp")
        void newPaymentShouldHaveCreatedAt() {
            assertNotNull(payment.getCreatedAt());
        }

        @Test
        @DisplayName("New payment should have all fields set correctly")
        void newPaymentShouldHaveAllFieldsSet() {
            assertEquals("REF-001", payment.getReference());
            assertEquals("CUST-123", payment.getCustomerId());
            assertEquals(new BigDecimal("100.00"), payment.getAmount());
            assertEquals(Currency.COP, payment.getCurrency());
            assertEquals(PaymentMethod.CARD, payment.getMethod());
        }
    }

    @Nested
    @DisplayName("Status Transitions")
    class StatusTransitions {

        @Test
        @DisplayName("PENDING can transition to APPROVED")
        void pendingCanTransitionToApproved() {
            payment.transitionTo(PaymentStatus.APPROVED);
            assertEquals(PaymentStatus.APPROVED, payment.getStatus());
        }

        @Test
        @DisplayName("PENDING can transition to REJECTED")
        void pendingCanTransitionToRejected() {
            payment.transitionTo(PaymentStatus.REJECTED);
            assertEquals(PaymentStatus.REJECTED, payment.getStatus());
        }

        @Test
        @DisplayName("PENDING cannot transition to PENDING")
        void pendingCannotTransitionToPending() {
            assertThrows(InvalidStatusTransitionException.class, () -> {
                payment.transitionTo(PaymentStatus.PENDING);
            });
        }

        @Test
        @DisplayName("APPROVED cannot transition to any status")
        void approvedCannotTransition() {
            payment.transitionTo(PaymentStatus.APPROVED);

            assertThrows(InvalidStatusTransitionException.class, () -> {
                payment.transitionTo(PaymentStatus.PENDING);
            });
            assertThrows(InvalidStatusTransitionException.class, () -> {
                payment.transitionTo(PaymentStatus.REJECTED);
            });
            assertThrows(InvalidStatusTransitionException.class, () -> {
                payment.transitionTo(PaymentStatus.APPROVED);
            });
        }

        @Test
        @DisplayName("REJECTED cannot transition to any status")
        void rejectedCannotTransition() {
            payment.transitionTo(PaymentStatus.REJECTED);

            assertThrows(InvalidStatusTransitionException.class, () -> {
                payment.transitionTo(PaymentStatus.PENDING);
            });
            assertThrows(InvalidStatusTransitionException.class, () -> {
                payment.transitionTo(PaymentStatus.APPROVED);
            });
            assertThrows(InvalidStatusTransitionException.class, () -> {
                payment.transitionTo(PaymentStatus.REJECTED);
            });
        }
    }

    @Nested
    @DisplayName("canTransitionTo method")
    class CanTransitionTo {

        @Test
        @DisplayName("PENDING can transition to APPROVED or REJECTED")
        void pendingCanTransitionToApprovedOrRejected() {
            assertTrue(payment.canTransitionTo(PaymentStatus.APPROVED));
            assertTrue(payment.canTransitionTo(PaymentStatus.REJECTED));
            assertFalse(payment.canTransitionTo(PaymentStatus.PENDING));
        }

        @Test
        @DisplayName("APPROVED cannot transition to any status")
        void approvedCannotTransitionToAny() {
            payment.transitionTo(PaymentStatus.APPROVED);

            assertFalse(payment.canTransitionTo(PaymentStatus.PENDING));
            assertFalse(payment.canTransitionTo(PaymentStatus.APPROVED));
            assertFalse(payment.canTransitionTo(PaymentStatus.REJECTED));
        }

        @Test
        @DisplayName("REJECTED cannot transition to any status")
        void rejectedCannotTransitionToAny() {
            payment.transitionTo(PaymentStatus.REJECTED);

            assertFalse(payment.canTransitionTo(PaymentStatus.PENDING));
            assertFalse(payment.canTransitionTo(PaymentStatus.APPROVED));
            assertFalse(payment.canTransitionTo(PaymentStatus.REJECTED));
        }
    }
}
