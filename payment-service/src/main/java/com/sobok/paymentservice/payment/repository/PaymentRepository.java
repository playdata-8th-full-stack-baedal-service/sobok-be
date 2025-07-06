package com.sobok.paymentservice.payment.repository;

import com.sobok.paymentservice.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> getPaymentByOrderId(String orderId);
}
