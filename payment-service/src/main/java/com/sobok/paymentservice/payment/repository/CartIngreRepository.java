package com.sobok.paymentservice.payment.repository;

import com.sobok.paymentservice.payment.entity.CartCook;
import com.sobok.paymentservice.payment.entity.CartIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartIngreRepository extends JpaRepository<CartIngredient, Long> {
}
