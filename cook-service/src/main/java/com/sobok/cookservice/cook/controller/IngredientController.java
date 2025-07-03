package com.sobok.cookservice.cook.controller;

import com.sobok.cookservice.common.dto.ApiResponse;
import com.sobok.cookservice.common.dto.TokenUserInfo;
import com.sobok.cookservice.cook.dto.request.IngreReqDto;
import com.sobok.cookservice.cook.dto.request.KeywordSearchReqDto;
import com.sobok.cookservice.cook.dto.response.IngreResDto;
import com.sobok.cookservice.cook.entity.Ingredient;
import com.sobok.cookservice.cook.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredient")
@RequiredArgsConstructor
@Slf4j
public class IngredientController {

    private final IngredientService ingredientService;

    /**
     * 관리자 재료 등록
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<Object> ingreRegister(@Valid @RequestBody IngreReqDto reqDto) {
        ingredientService.ingreCreate(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(reqDto.getIngreName(), "식재료가 등록되었습니다."));
    }

    /**
     * 통합 재료 검색
     */
    @GetMapping("/keyword-search")
    public ResponseEntity<Object> ingreSearch(@RequestBody KeywordSearchReqDto keywordSearchReqDto) {
        List<IngreResDto> ingredients = ingredientService.ingreSearch(keywordSearchReqDto);
        if (ingredients.isEmpty()) {
            return ResponseEntity.ok().body(ApiResponse.ok(null, HttpStatus.NO_CONTENT));  // 204
        }
        return ResponseEntity.ok().body(ApiResponse.ok(ingredients, "키워드로 검색한 식재료 결과입니다."));
    }


}
