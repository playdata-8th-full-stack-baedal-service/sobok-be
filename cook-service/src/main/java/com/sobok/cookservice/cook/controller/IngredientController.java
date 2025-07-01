package com.sobok.cookservice.cook.controller;

import com.sobok.cookservice.cook.service.IngredientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingredient")
@RequiredArgsConstructor
@Slf4j
public class IngredientController {

    private final IngredientService ingredientService;
}
