package com.sobok.cookservice.cook.controller;

import com.sobok.cookservice.common.dto.ApiResponse;
import com.sobok.cookservice.common.dto.TokenUserInfo;
import com.sobok.cookservice.cook.dto.request.IngreReqDto;
import com.sobok.cookservice.cook.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingredient")
@RequiredArgsConstructor
@Slf4j
public class IngredientController {

    private final IngredientService ingredientService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ingredient-register")
    public ResponseEntity<Object> ingreRegister(@Valid @RequestBody IngreReqDto reqDto) {
        ingredientService.ingreCreate(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(reqDto.getIngreName(), "재료가 등록되었습니다."));
    }
}
