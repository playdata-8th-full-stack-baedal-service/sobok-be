package com.sobok.cookservice.cook.controller;


import com.sobok.cookservice.common.exception.CustomException;
import com.sobok.cookservice.cook.dto.response.CookDetailResDto;
import com.sobok.cookservice.cook.dto.response.CookIngredientResDto;
import com.sobok.cookservice.cook.dto.response.CookNameResDto;
import com.sobok.cookservice.cook.dto.response.IngreResDto;
import com.sobok.cookservice.cook.dto.response.UserBookmarkResDto;
import com.sobok.cookservice.cook.dto.response.IngredientNameResDto;
import com.sobok.cookservice.cook.repository.CombinationRepository;
import com.sobok.cookservice.cook.repository.CookRepository;
import com.sobok.cookservice.cook.service.CombinationService;
import com.sobok.cookservice.cook.service.CookService;
import com.sobok.cookservice.cook.service.IngredientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CookFeignController {

    private final CombinationService combinationService;
    private final IngredientService ingredientService;
    private final CookService cookService;
    private final CookRepository cookRepository;


    @GetMapping("/get-cook-default-ingre")
    Map<Long, Integer> getDefaultIngreInfoList(@RequestParam Long cookId) {
        return combinationService.getDefaultIngreInfoList(cookId);
    }

    // 장바구니 조회용
    @GetMapping("/cook/{cookId}")
    public CookDetailResDto getCookDetail(@PathVariable Long cookId) {
        return cookService.getCookDetail(cookId);
    }

    // 추가 식재료 조회
    @GetMapping("/ingredients/{id}")
    public CookIngredientResDto getIngredient(@PathVariable Long id) {
        return ingredientService.getIngredientDtoById(id);
    }

    /**
     * cookId가 DB에 존재하는지 확인
     */
    @GetMapping("/check-cook")
    ResponseEntity<?> checkCook(@RequestParam Long cookId) {
        return ResponseEntity.ok(cookService.checkCook(cookId));
    }

    /**
     * 즐겨찾기 해당하는 요리 정보 가져오기
     */
    @PostMapping("/preLookup-cook")
    ResponseEntity<?> preLookupCook(@RequestBody List<Long> cookIds) {
        List<UserBookmarkResDto> result = cookService.findCookById(cookIds);
        return ResponseEntity.ok(result);
    }
    /**
     * 주문 전체 조회용(요리이름)
     */
    @PostMapping("/admin/cook-names")
    public ResponseEntity<List<CookNameResDto>> getCookNames(@RequestBody List<Long> cookIds) {
        return ResponseEntity.ok(cookService.getCookNamesByIds(cookIds));
    }

    /**
     * 식재료 이름 조회(주문 조회)
     */
    @PostMapping("/admin/ingredient-names")
    public List<IngredientNameResDto> getIngredientNames(@RequestBody List<Long> ingreIds) {
        return ingredientService.getIngredientNamesByIds(ingreIds);
    }


    //주문 내역 조회용
    @GetMapping("/cooks")
    List<CookDetailResDto> getCookDetails(@RequestParam("id") List<Long> cookIds){
        return cookService.getCookDetailList(cookIds);
    }

    /**
     * 게시글 등록(요리 이름 조회)
     */
    @GetMapping("/cook/name")
    public String getCookNameById(@RequestParam Long cookId) {
        return cookService.getCookNameById(cookId);
    }
}
