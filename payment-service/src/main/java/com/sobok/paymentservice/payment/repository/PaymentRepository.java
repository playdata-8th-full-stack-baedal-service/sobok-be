package com.sobok.paymentservice.payment.repository;

import com.sobok.paymentservice.common.enums.OrderState;
import com.sobok.paymentservice.payment.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 비관적 락 방식 도입
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId AND p.orderState = :state")
    Optional<Payment> getPendingPaymentByOrderId(@Param("orderId") String orderId, @Param("state") OrderState orderState);

    Page<Payment> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
