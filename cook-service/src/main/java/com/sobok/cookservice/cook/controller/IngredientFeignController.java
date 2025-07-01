package com.sobok.cookservice.cook.controller;


import com.sobok.cookservice.common.dto.ApiResponse;
import com.sobok.cookservice.cook.dto.request.IngreReqDto;
import com.sobok.cookservice.cook.service.IngredientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class IngredientFeignController {

    private final IngredientService ingredientService;

    @PostMapping("/ingredient-register")
    public ResponseEntity<Object> ingreRegister(@RequestBody IngreReqDto reqDto) {
        ingredientService.ingreCreate(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(reqDto.getIngreName(), "재료가 등록되었습니다."));
    }

}
