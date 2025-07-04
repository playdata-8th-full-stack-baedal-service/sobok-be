package com.sobok.paymentservice.payment.repository;

import com.sobok.paymentservice.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
