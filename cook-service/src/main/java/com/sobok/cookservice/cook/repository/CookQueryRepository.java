package com.sobok.cookservice.cook.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.cookservice.cook.dto.response.CartMonthlyHotDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sobok.cookservice.cook.entity.QCook.cook;

@RequiredArgsConstructor
@Repository
@Slf4j
public class CookQueryRepository {
    private final JPAQueryFactory factory;

    public List<CartMonthlyHotDto.MonthlyHot> getNotOrderCookIdList(List<Long> cookIdList) {
        return factory.select(cook.id)
                .from(cook)
                .where(cook.id.notIn(cookIdList))
                .orderBy(cook.updatedAt.desc())
                .fetch()
                .stream()
                .map(id -> new CartMonthlyHotDto.MonthlyHot(id, 0))
                .toList();
    }
}
