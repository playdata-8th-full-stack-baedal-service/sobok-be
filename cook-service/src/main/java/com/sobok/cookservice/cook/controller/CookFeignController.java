package com.sobok.cookservice.cook.controller;

import com.sobok.cookservice.cook.repository.CombinationRepository;
import com.sobok.cookservice.cook.service.CombinationService;
import com.sobok.cookservice.cook.service.IngredientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import com.sobok.cookservice.cook.service.CookService;
import org.springframework.web.bind.annotation.*;
      
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CookFeignController {

    private final CombinationService combinationService;
    private final IngredientService ingredientService;
    private final CookService cookService;


    @GetMapping("/get-cook-default-ingre")
    Map<Long, Integer> getDefaultIngreInfoList(@RequestParam Long cookId) {
        return combinationService.getDefaultIngreInfoList(cookId);
    }

    /**
     * cookId가 DB에 존재하는지 확인
     */
    @GetMapping("/check-cook")
    ResponseEntity<?> checkCook(@RequestParam Long cookId) {
        return ResponseEntity.ok(cookService.checkCook(cookId));
    }

}
