package com.sobok.shopservice.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.shopservice.shop.dto.stock.CartIngredientStock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sobok.shopservice.shop.entity.QStock.stock;

@Repository
@Slf4j
@RequiredArgsConstructor
public class StockQueryRepository {
    private final JPAQueryFactory factory;

    public Map<Long, Map<Long, Integer>> getStockByIngreIdList(List<Long> shopIdList, Set<Long> ingreIdList) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(stock.shopId.in(shopIdList));
        builder.and(stock.ingredientId.in(ingreIdList));

        return factory.select(
                        Projections.constructor(
                                CartIngredientStock.class,
                                stock.shopId,
                                stock.ingredientId,
                                stock.quantity
                        )
                ).from(stock)
                .where(builder)
                .fetch()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                CartIngredientStock::getShopId,
                                Collectors.toMap(
                                        CartIngredientStock::getIngredientId,
                                        CartIngredientStock::getQuantity
                                )
                        )
                );
    }
}
