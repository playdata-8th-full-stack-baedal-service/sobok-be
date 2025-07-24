package com.sobok.cookservice.cook.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.cookservice.cook.dto.response.IngreResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sobok.cookservice.cook.entity.QIngredient.ingredient;

@RequiredArgsConstructor
@Repository
@Slf4j
public class IngredientQueryRepository {
    private final JPAQueryFactory factory;

    public List<IngreResDto> getSearchIngredient(BooleanBuilder builder) {
        return factory
                .select(Projections.fields(
                        IngreResDto.class,
                        ingredient.id,
                        ingredient.ingreName,
                        ingredient.price,
                        ingredient.origin,
                        ingredient.unit
                ))
                .from(ingredient)
                .where(builder)
                .orderBy(ingredient.ingreName.asc())
                .fetch();
    }

}
