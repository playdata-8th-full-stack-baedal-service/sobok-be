package com.sobok.shopservice.shop.repository;

import com.sobok.shopservice.shop.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Modifying
    @Query("UPDATE Stock s SET s.quantity = s.quantity - :deduction " +
            "WHERE s.shopId = :shopId AND s.ingredientId IN :ingredientIds")
    int bulkDeductStock(
            @Param("shopId") Long shopId,
            @Param("ingredientIds") List<Long> ingredientIds,
            @Param("deduction") int deduction
    );

    List<Stock> findByShopIdAndIngredientIdIn(Long shopId, Collection<Long> ingredientIds);
}
