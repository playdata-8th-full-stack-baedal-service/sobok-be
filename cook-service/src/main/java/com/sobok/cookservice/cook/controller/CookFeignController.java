package com.sobok.cookservice.cook.controller;


import com.sobok.cookservice.cook.dto.response.*;
import com.sobok.cookservice.cook.repository.CookRepository;
import com.sobok.cookservice.cook.service.CombinationService;
import com.sobok.cookservice.cook.service.CookService;
import com.sobok.cookservice.cook.service.IngredientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

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
    public List<CookDetailResDto> getCookDetail(@PathVariable List<Long> cookId) {
        return cookService.getCookDetail(cookId);
    }

    // 추가 식재료 조회
    @GetMapping("/ingredients/{id}")
    public List<CookIngredientResDto> getIngredient(@PathVariable List<Long> id) {
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
    @GetMapping("/cooks-info")
    List<CookInfoResDto> getCookDetails(@RequestParam("id") List<Long> cookIds){
        return cookService.getCooksInfolList(cookIds);
    }

    /**
     * 게시글 등록(요리 이름 조회)
     */
    @GetMapping("/cook/name")
    public String getCookNameById(@RequestParam("id") Long cookId) {
        return cookService.getCookNameById(cookId);
    }

    /**
     * 특정 요리 ID에 대한 기본 식재료 목록을 조회 (게시글 상세 조회용)
     */
    @GetMapping("/cook/base-ingredients")
    public List<CookWithIngredientResDto> getBaseIngredients(@RequestParam Long cookId) {
        return cookService.getBaseIngredients(cookId);
    }

    /**
     * 특정 식재료 ID에 대한 상세 정보를 조회 ( 게시글 상세 조회용)
     */
    @GetMapping("/cook/ingredient-info")
    public IngredientInfoResDto getIngredientInfo(@RequestParam Long ingreId) {
        return cookService.getIngredientInfo(ingreId);
    }

    /**
     * 요리 Id 리스트를 받아 해당 요리들의 이름 정보를 반환
     */
    @PostMapping("/cook-names")
    public ResponseEntity<List<CookNameResDto>> getCookNamesForPostService(@RequestBody List<Long> cookIds) {
        return ResponseEntity.ok(cookService.getCookNamesByIds(cookIds));
    }
}
