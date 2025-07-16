package com.sobok.cookservice.cook.service;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.cookservice.common.exception.CustomException;
import com.sobok.cookservice.cook.dto.request.IngreEditReqDto;
import com.sobok.cookservice.cook.dto.request.IngreReqDto;
import com.sobok.cookservice.cook.dto.request.KeywordSearchReqDto;
import com.sobok.cookservice.cook.dto.response.CookIngredientResDto;
import com.sobok.cookservice.cook.dto.response.IngreResDto;
import com.sobok.cookservice.cook.dto.response.IngredientNameResDto;
import com.sobok.cookservice.cook.entity.Ingredient;
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
    public List<IngreResDto> ingreSearch(String keyword) {

        BooleanBuilder builder = new BooleanBuilder();

        //재료 이름에 키워드가 포함된 것만 가져오기
        if (keyword != null) {
            builder.and(ingredient.ingreName.contains(keyword));
        }

        //이름순 정렬 후 IngreResDto로 리턴
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

    /**
     * 식재료 정보 수정 (관리자)
     */
    public void ingreEdit(IngreEditReqDto newReqDto) {
        log.info("newReqDto: {}", newReqDto);
        Ingredient ingredient = ingredientRepository.findById(newReqDto.getId()).orElseThrow(
                () -> new CustomException("해당 식재료가 존재하지 않습니다.", HttpStatus.BAD_REQUEST)
        );
        ingredient.update(newReqDto);
        ingredientRepository.save(ingredient);
        log.info("정보 변경 완료");
    }

    /**
     * 추가 식재료 조회 Feign
     */
    public CookIngredientResDto getIngredientDtoById(Long id) {
        Ingredient ingre = ingredientRepository.findById(id)
                .orElseThrow(() -> new CustomException("식재료가 존재하지 않습니다.", HttpStatus.NOT_FOUND));
        log.info("ingredient name: {}", ingre.getIngreName());
        return CookIngredientResDto.builder()
                .ingredientId(ingre.getId())
                .ingreName(ingre.getIngreName())
                .unit(ingre.getUnit())
                .price(ingre.getPrice())
                .origin(ingre.getOrigin())
                .build();
    }
    /**
     * 식재료 이름 조회용 (주문 전체 조회)
     */
    public List<IngredientNameResDto> getIngredientNamesByIds(List<Long> ingreIds) {
        List<Ingredient> ingredients = ingredientRepository.findAllById(ingreIds);

        if (ingredients.size() != ingreIds.size()) {
            throw new CustomException("일부 식재료 ID가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        return ingredients.stream()
                .map(ingredient -> new IngredientNameResDto(ingredient.getId(), ingredient.getIngreName()))
                .toList();
    }

}
