package com.sobok.paymentservice.payment.repository;

import com.sobok.paymentservice.payment.entity.CartIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartIngreRepository extends JpaRepository<CartIngredient, Long> {

    List<CartIngredient> findByCartCookId(Long cartCookId);

    @Modifying
    @Query("DELETE FROM CartIngredient c WHERE c.cartCookId = :id")
    void deleteByUnpaidCartCookId(@Param("id") Long cartCookId);

    /**
     * 특정 cartCookId에 해당하는 기본/추가 식재료 목록을 조회
     */
    List<CartIngredient> findByCartCookIdAndDefaultIngre(Long cartCookId, String defaultIngre);

    List<CartIngredient> findByCartCookIdIn(List<Long> cartCookIds);

    @Modifying
    @Query("DELETE FROM CartIngredient c WHERE c.cartCookId IN :ids")
    void deleteByUnpaidCartCookIdList(@Param("ids") List<Long> cartCookList);
}
