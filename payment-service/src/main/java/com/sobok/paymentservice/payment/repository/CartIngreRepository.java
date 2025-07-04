package com.sobok.paymentservice.payment.repository;

import com.sobok.paymentservice.payment.entity.CartIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartIngreRepository extends JpaRepository<CartIngredient, Long> {
    List<CartIngredient> findByCartCookId(Long cartCookId);
}
