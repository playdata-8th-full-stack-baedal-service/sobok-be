package com.sobok.cookservice.cook.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.cookservice.cook.dto.response.CartMonthlyHotDto;
import com.sobok.cookservice.cook.dto.response.CookResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sobok.cookservice.cook.entity.QCombination.combination;
import static com.sobok.cookservice.cook.entity.QCook.cook;
import static com.sobok.cookservice.cook.entity.QIngredient.ingredient;

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

    public List<CookResDto> getSearchCook(Long numOfRows, BooleanBuilder builder, long offset) {
        return factory.select(
                        Projections.fields(
                                CookResDto.class,
                                cook.id,
                                cook.name,
                                cook.allergy,
                                cook.recipe,
                                cook.category,
                                cook.thumbnail,
                                cook.active
                        )
                )
                .from(cook)
                .where(builder)
                .offset(offset)
                .orderBy(cook.updatedAt.desc())
                .limit(numOfRows)
                .fetch();
    }

    public List<Tuple> getCookInfo(Long cookId) {
        return factory.select(
                        cook.id,
                        cook.name,
                        cook.allergy,
                        cook.category,
                        cook.recipe,
                        cook.thumbnail,
                        ingredient.id,
                        ingredient.ingreName,
                        ingredient.price,
                        ingredient.unit,
                        combination.unitQuantity
                )
                .from(cook)
                .where(cook.id.eq(cookId).and(cook.active.eq("Y")))
                .join(combination)
                .on(combination.cookId.eq(cook.id))
                .join(ingredient)
                .on(ingredient.id.eq(combination.ingreId))
                .fetch();
    }
}
