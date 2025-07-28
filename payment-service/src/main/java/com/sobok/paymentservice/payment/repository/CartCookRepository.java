package com.sobok.paymentservice.payment.repository;

import com.sobok.paymentservice.payment.entity.CartCook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import java.util.List;

public interface CartCookRepository extends JpaRepository<CartCook, Long> {

    List<CartCook> findByUserIdAndPaymentIdIsNull(Long userId);

    @Query("SELECT c FROM CartCook c WHERE c.id = :id AND c.paymentId IS NULL")
    Optional<CartCook> findUnpaidCartById(@Param("id") Long id);

    List<CartCook> findByPaymentId(Long paymentId);

    List<CartCook> findByUserId(Long userId);

    Optional<CartCook> getCartCooksByUserIdAndId(Long userId, Long id);

    @Modifying
    @Query("DELETE FROM CartCook c WHERE c.userId = :id AND c.paymentId IS NULL")
    void deleteUnpaidCartCooksByUserId(@Param("id") Long userId);

    @Modifying
    @Query("DELETE FROM CartCook c WHERE c.id = :id")
    void deleteUnpaidById(@Param("id") Long id);

    List<CartCook> findByIdInAndUserId(Collection<Long> ids, Long userId);

    void deleteByIdIn(Collection<Long> ids);
}
