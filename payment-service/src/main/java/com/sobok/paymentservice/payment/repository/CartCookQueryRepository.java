package com.sobok.paymentservice.payment.repository;

import com.querydsl.core.Query;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.paymentservice.payment.dto.cart.MonthlyHot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.sobok.paymentservice.payment.entity.QCartCook.cartCook;
import static com.sobok.paymentservice.payment.entity.QPayment.payment;

@RequiredArgsConstructor
@Repository
@Slf4j
public class CartCookQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<MonthlyHot> getMonthlyHotCartCook(LocalDateTime monthToMillis) {
        // 주문량 순 요리 조회
        return queryFactory
                .select(
                        Projections.constructor(
                                MonthlyHot.class,
                                cartCook.cookId,
                                cartCook.count.sum()
                        )
                )
                .from(cartCook)
                .where(payment.createdAt.goe(monthToMillis))
                .join(payment)
                .on(payment.id.eq(cartCook.paymentId))
                .groupBy(cartCook.cookId)
                .orderBy(cartCook.count.sum().desc())
                .fetch();
    }


}
