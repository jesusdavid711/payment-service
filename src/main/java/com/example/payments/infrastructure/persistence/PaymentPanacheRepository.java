package com.example.payments.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.payments.domain.Payment;
import com.example.payments.domain.PaymentStatus;
import com.example.payments.infrastructure.repository.PaymentRepository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Panache implementation of the payment repository.
 * This is the secondary adapter in hexagonal architecture.
 */
@ApplicationScoped
public class PaymentPanacheRepository implements PaymentRepository, PanacheRepository<Payment> {

    @Override
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            persist(payment);
        } else {
            payment = getEntityManager().merge(payment);
        }
        return payment;
    }

    @Override
    public Optional<Payment> findPaymentById(Long id) {
        return find("id", id).firstResultOptional();
    }

    @Override
    public boolean existsByReference(String reference) {
        return count("reference", reference) > 0;
    }

    @Override
    public List<Payment> findByFilters(
            PaymentStatus status,
            String customerId,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size) {

        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (status != null) {
            query.append(" AND status = :status");
            params.put("status", status);
        }
        if (customerId != null && !customerId.isBlank()) {
            query.append(" AND customerId = :customerId");
            params.put("customerId", customerId);
        }
        if (from != null) {
            query.append(" AND createdAt >= :from");
            params.put("from", from);
        }
        if (to != null) {
            query.append(" AND createdAt <= :to");
            params.put("to", to);
        }

        return find(query.toString(), params)
                .page(page, size)
                .list();
    }

    @Override
    public long countByFilters(
            PaymentStatus status,
            String customerId,
            LocalDateTime from,
            LocalDateTime to) {

        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (status != null) {
            query.append(" AND status = :status");
            params.put("status", status);
        }
        if (customerId != null && !customerId.isBlank()) {
            query.append(" AND customerId = :customerId");
            params.put("customerId", customerId);
        }
        if (from != null) {
            query.append(" AND createdAt >= :from");
            params.put("from", from);
        }
        if (to != null) {
            query.append(" AND createdAt <= :to");
            params.put("to", to);
        }

        return count(query.toString(), params);
    }
}
