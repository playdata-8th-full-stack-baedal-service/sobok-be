package com.sobok.cookservice.cook.service;

import com.sobok.cookservice.common.exception.CustomException;
import com.sobok.cookservice.cook.dto.request.CookCreateReqDto;
import com.sobok.cookservice.cook.dto.response.CookCreateResDto;
import com.sobok.cookservice.cook.entity.Combination;
import com.sobok.cookservice.cook.entity.Cook;
import com.sobok.cookservice.cook.entity.Ingredient;
import com.sobok.cookservice.cook.repository.CombinationRepository;
import com.sobok.cookservice.cook.repository.CookRepository;
import com.sobok.cookservice.cook.repository.IngredientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CookService {


    private final CookRepository cookRepository;
    private final CombinationRepository combinationRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional
    public CookCreateResDto createCook(CookCreateReqDto dto) {

        // 레시피 이름 중복 검증
        cookRepository.findByName(dto.getName())
                .ifPresent(cook -> {
                    throw new CustomException("이미 존재하는 요리 이름입니다.", HttpStatus.BAD_REQUEST);
                });

        // 레시피 썸네일 중복 검증
        cookRepository.findByThumbnail(dto.getThumbnailUrl())
                .ifPresent(cook -> {
                    throw new CustomException("이미 사용 중인 썸네일입니다.", HttpStatus.BAD_REQUEST);
                });

        // 요리 저장
        Cook cook = Cook.builder()
                .name(dto.getName())
                .allergy(dto.getAllergy())
                .recipe(dto.getRecipe())
                .category(dto.getCategory())
                .thumbnail(dto.getThumbnailUrl())
                .build();

        cookRepository.save(cook); // DB 저장

        // 식재료 조합 저장
        List<Combination> combinations = dto.getIngredients().stream()
                .map(ingredientDto -> {
                    Ingredient ingredient = ingredientRepository.findById(ingredientDto.getIngredientId())
                            .orElseThrow(() -> new CustomException("해당 식재료가 존재하지 않습니다: id=" + ingredientDto.getIngredientId(),
                                    HttpStatus.BAD_REQUEST));

                    return Combination.builder()
                            .cookId(cook.getId())
                            .ingreId(ingredient.getId())
                            .ingredient(ingredient)
                            .unitQuantity(ingredientDto.getUnitQuantity())
                            .build();
                })
                .toList();

        combinationRepository.saveAll(combinations);

        // 등록된 요리 ID 반환
        return new CookCreateResDto(cook.getId());
    }


}
