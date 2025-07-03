package com.sobok.cookservice.cook.service;

import com.sobok.cookservice.common.exception.CustomException;
import com.sobok.cookservice.cook.dto.request.IngreReqDto;
import com.sobok.cookservice.cook.dto.request.KeywordSearchReqDto;
import com.sobok.cookservice.cook.dto.response.IngreResDto;
import com.sobok.cookservice.cook.entity.Ingredient;
import com.sobok.cookservice.cook.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;


    public void ingreCreate(IngreReqDto reqDto) {

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

    public List<Ingredient> ingreSearch(KeywordSearchReqDto keywordSearchReqDto) {
        List<Ingredient> ingreResList = ingredientRepository.keywordSearch(keywordSearchReqDto.getKeyword());
        log.info("ingreResList: {}", ingreResList.toString());
        return ingreResList;
    }
}
