package com.sobok.paymentservice.payment.repository;

import com.sobok.paymentservice.payment.entity.CartCook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartCookRepository extends JpaRepository<CartCook, Long> {
    @Query("SELECT c FROM CartCook c WHERE c.id = :id AND c.paymentId IS NULL")
    Optional<CartCook> findUnpaidCartById(@Param("id") Long id);

    List<CartCook> findByPaymentId(Long paymentId);
}
