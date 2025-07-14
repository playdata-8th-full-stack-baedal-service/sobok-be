package com.sobok.paymentservice.payment.repository;

import com.sobok.paymentservice.payment.entity.CartIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartIngreRepository extends JpaRepository<CartIngredient, Long> {

    List<CartIngredient> findByCartCookId(Long cartCookId);

    void deleteByCartCookId(Long cartCookId);

    /**
     * 특정 cartCookId에 해당하는 기본/추가 식재료 목록을 조회
     */
    List<CartIngredient> findByCartCookIdAndDefaultIngre(Long cartCookId, String defaultIngre);

}
