package com.sobok.cookservice.cook.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.cookservice.cook.dto.display.BasicCookDisplay;
import com.sobok.cookservice.cook.dto.display.DisplayParamDto;
import com.sobok.cookservice.cook.dto.response.CartMonthlyHotDto;
import com.sobok.cookservice.cook.entity.Cook;
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

    public List<BasicCookDisplay> getCookDisplaysByCondition(DisplayParamDto params, BooleanBuilder builder) {
        long offset = (params.getPageNo() - 1) * params.getNumOfRows();
        long limit = params.getNumOfRows();

        return factory.select(
                        Projections.constructor(
                                BasicCookDisplay.class,
                                cook.id,
                                cook.name,
                                cook.thumbnail
                        )
                )
                .from(cook)
                .where(builder)
                .orderBy(cook.updatedAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }
}
