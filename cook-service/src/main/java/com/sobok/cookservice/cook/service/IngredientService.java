package com.sobok.cookservice.cook.service;

import com.sobok.cookservice.common.exception.CustomException;
import com.sobok.cookservice.cook.dto.request.IngreEditReqDto;
import com.sobok.cookservice.cook.dto.request.IngreReqDto;
import com.sobok.cookservice.cook.dto.response.CookIngredientResDto;
import com.sobok.cookservice.cook.dto.response.IngreResDto;
import com.sobok.cookservice.cook.dto.response.IngredientNameResDto;
import com.sobok.cookservice.cook.entity.Ingredient;
import com.sobok.cookservice.cook.repository.IngredientQueryRepository;
import com.sobok.cookservice.cook.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.querydsl.core.BooleanBuilder;

import static com.sobok.cookservice.cook.entity.QIngredient.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientQueryRepository ingredientQueryRepository;

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
        return ingredientQueryRepository.getSearchIngredient(builder);
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
     * 식재료 조회 Feign
     */
    public List<CookIngredientResDto> getIngredientDtoById(List<Long> ids) {
        Map<Long, Ingredient> ingredientMap = ingredientRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Ingredient::getId, Function.identity()));

        return ids.stream()
                .map(id -> {
                    Ingredient ingre = ingredientMap.get(id);
                    if (ingre == null) {
                        log.warn("식재료 ID {} 는 존재하지 않습니다.", id);
                        return null; // 또는 예외 객체가 아닌 기본값 DTO로 대체 가능
                    }

                    log.info("ingredient name: {}", ingre.getIngreName());
                    return CookIngredientResDto.builder()
                            .ingredientId(ingre.getId())
                            .ingreName(ingre.getIngreName())
                            .unit(ingre.getUnit())
                            .price(ingre.getPrice())
                            .origin(ingre.getOrigin())
                            .build();
                })
                .collect(Collectors.toList());
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

    // 식재료 검증용 true false
    public boolean existIngredient(Long ingreId) {
        return ingredientRepository.existsById(ingreId);
    }

}
