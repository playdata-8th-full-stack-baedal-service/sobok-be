package com.sobok.cookservice.cook.service;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.cookservice.common.exception.CustomException;
import com.sobok.cookservice.cook.dto.request.IngreReqDto;
import com.sobok.cookservice.cook.dto.request.KeywordSearchReqDto;
import com.sobok.cookservice.cook.dto.response.IngreResDto;
import com.sobok.cookservice.cook.entity.Ingredient;
import com.sobok.cookservice.cook.entity.QIngredient;
import com.sobok.cookservice.cook.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.querydsl.core.BooleanBuilder;

import static com.sobok.cookservice.cook.entity.QIngredient.*;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final JPAQueryFactory factory;


    /**
     * 관리자 재료 등록
     */
    public void ingreCreate(IngreReqDto reqDto) {

        log.info("컨트롤러 통과");
        // 재료 이름으로 db에 있는지 확인
        Boolean isExist = ingredientRepository.existsByIngreName(reqDto.getIngreName());

        if (isExist) {
            throw new CustomException("이미 등록된 재료입니다.", HttpStatus.BAD_REQUEST);
        }

        Ingredient ingredient = Ingredient.builder()
                .ingreName(reqDto.getIngreName())
                .origin(reqDto.getOrigin())
                .price(reqDto.getPrice())
                .unit(reqDto.getUnit())
                .build();

        ingredientRepository.save(ingredient);

    }

    /**
     * 통합 재료 검색
     */
    public List<IngreResDto> ingreSearch(KeywordSearchReqDto keywordSearchReqDto) {

        BooleanBuilder builder = new BooleanBuilder();

        //재료 이름에 키워드가 포함된 것만 가져오기
        if (keywordSearchReqDto.getKeyword() != null) {
            builder.and(ingredient.ingreName.contains(keywordSearchReqDto.getKeyword()));
        }

        //이름순 정렬 후 IngreResDto로 리턴
        return factory
                .select(Projections.fields(
                        IngreResDto.class,
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
