package com.sobok.cookservice.cook.controller;


import com.sobok.cookservice.cook.dto.display.MonthlyHot;
import com.sobok.cookservice.cook.dto.response.*;
import com.sobok.cookservice.cook.service.*;
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
    private final MonthlyHotCookUpdater monthlyHotCookUpdater;


    @GetMapping("/get-cook-default-ingre")
    ResponseEntity<Map<Long, Integer>> getDefaultIngreInfoList(@RequestParam Long cookId) {
        return ResponseEntity.ok().body(combinationService.getDefaultIngreInfoList(cookId));
    }

    // 장바구니 조회용
    @GetMapping("/cook/{cookId}")
    public ResponseEntity<List<CookDetailResDto>> getCookDetail(@PathVariable List<Long> cookId) {
        return ResponseEntity.ok().body(cookService.getCookDetail(cookId));
    }

    // 식재료 조회
    @GetMapping("/ingredients/{id}")
    public ResponseEntity<List<CookIngredientResDto>> getIngredient(@PathVariable List<Long> id) {
        return ResponseEntity.ok().body(ingredientService.getIngredientDtoById(id));
    }

    /**
     * cookId가 DB에 존재하는지 확인
     */
    @GetMapping("/check-cook")
    ResponseEntity<?> checkCook(@RequestParam Long cookId) {
        return ResponseEntity.ok().body(cookService.checkCook(cookId));
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
     * 식재료 이름 조회(주문 조회)
     */
    @PostMapping("/admin/ingredient-names")
    public ResponseEntity<List<IngredientNameResDto>> getIngredientNames(@RequestBody List<Long> ingreIds) {
        return ResponseEntity.ok().body(ingredientService.getIngredientNamesByIds(ingreIds));
    }


    //주문 내역 조회용
    @GetMapping("/cooks-info")
    ResponseEntity<List<CookInfoResDto>> getCookDetails(@RequestParam("id") List<Long> cookIds) {
        return ResponseEntity.ok().body(cookService.getCooksInfolList(cookIds));
    }

    /**
     * 게시글 등록(요리 이름 조회)
     */
    @GetMapping("/cook-name")
    public ResponseEntity<String> getCookNameById(@RequestParam Long cookId) {
        return ResponseEntity.ok().body(cookService.getCookNameById(cookId));
    }

    /**
     * 특정 요리 ID에 대한 기본 식재료 목록을 조회 (게시글 상세 조회용)
     */
    @GetMapping("/cook/base-ingredients")
    public ResponseEntity<List<CookWithIngredientResDto>> getBaseIngredients(@RequestParam Long cookId) {
        return ResponseEntity.ok().body(cookService.getBaseIngredients(cookId));
    }

    /**
     * 특정 식재료 ID에 대한 상세 정보를 조회 ( 게시글 상세 조회용)
     */
    @GetMapping("/cook/ingredient-info")
    public ResponseEntity<IngredientInfoResDto> getIngredientInfo(@RequestParam Long ingreId) {
        return ResponseEntity.ok().body(cookService.getIngredientInfo(ingreId));
    }

    /**
     * 요리 Id 리스트를 받아 해당 요리들의 이름 정보를 반환
     */
    @PostMapping("/cook-names")
    public ResponseEntity<List<CookNameResDto>> getCookNamesForPostService(@RequestBody List<Long> cookIds) {
        return ResponseEntity.ok().body(cookService.getCookNamesByIds(cookIds));
    }

    /**
     * 대표 이미지가 없는 post를 위한 사진 공유
     */
    @GetMapping("/cook-thumbnail")
    public ResponseEntity<String> getCookThumbnail(@RequestParam Long cookId) {
        return ResponseEntity.ok().body(cookService.getCookThumbnail(cookId));
    }

    @PutMapping("/monthly-hot")
    void updateMonthlyHotCooks(@RequestBody List<MonthlyHot> monthlyHotList) {
        monthlyHotCookUpdater.updateMonthlyHotCooks(monthlyHotList);
    }

    @GetMapping("/exist-ingre")
    public ResponseEntity<Boolean> verifyUser(@RequestParam Long ingreId) {
        return ResponseEntity.ok().body(ingredientService.existIngredient(ingreId));
    }

    @GetMapping("/get-names")
    public ResponseEntity<Map<Long, String>> getNamesByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok().body(ingredientService.getNamesByIds(ids));
    }
}
