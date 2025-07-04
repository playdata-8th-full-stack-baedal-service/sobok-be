package com.sobok.paymentservice.payment.repository;

import com.sobok.paymentservice.payment.entity.CartCook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartCookRepository extends JpaRepository<CartCook, Long> {
}
