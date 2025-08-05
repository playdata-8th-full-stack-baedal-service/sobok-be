package com.sobok.cookservice.cook.controller;

import com.sobok.cookservice.common.dto.CommonResponse;
import com.sobok.cookservice.cook.controller.docs.IngredientControllerDocs;
import com.sobok.cookservice.cook.dto.request.IngreEditReqDto;
import com.sobok.cookservice.cook.dto.request.IngreReqDto;
import com.sobok.cookservice.cook.dto.response.IngreResDto;
import com.sobok.cookservice.cook.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredient")
@RequiredArgsConstructor
@Slf4j
public class IngredientController implements IngredientControllerDocs {

    private final IngredientService ingredientService;

    /**
     * 관리자 재료 등록
     */
    @PostMapping("/register")
    public ResponseEntity<?> ingreRegister(@Valid @RequestBody IngreReqDto reqDto) {
        IngreResDto ingreResDto = ingredientService.ingreCreate(reqDto);
        return ResponseEntity.ok().body(CommonResponse.ok(ingreResDto, "식재료가 등록되었습니다."));
    }

    /**
     * 통합 재료 검색
     */
    @GetMapping("/keyword-search")
    public ResponseEntity<?> ingreSearch(@RequestParam String keyword) {
        log.info("keyword={}", keyword);
        List<IngreResDto> ingredients = ingredientService.ingreSearch(keyword);
        if (ingredients.isEmpty()) {
            return ResponseEntity.ok().body(CommonResponse.ok(null, HttpStatus.NO_CONTENT));  // 204
        }
        return ResponseEntity.ok().body(CommonResponse.ok(ingredients, "키워드로 검색한 식재료 결과입니다."));
    }

    /**
     * 식재료 전체 조회 (관리자)
     */
    @GetMapping("/all-search")
    public ResponseEntity<?> allSearch() {
        List<IngreResDto> ingredients = ingredientService.ingreSearch("");
        if (ingredients.isEmpty()) {
            return ResponseEntity.ok().body(CommonResponse.ok(null, HttpStatus.NO_CONTENT));  // 204
        }
        return ResponseEntity.ok().body(CommonResponse.ok(ingredients, "전체 식재료 결과입니다."));
    }

    /**
     * 식재료 정보 수정 (관리자)
     */
    @PatchMapping("/edit")
    public ResponseEntity<?> ingreEdit(@Valid @RequestBody IngreEditReqDto newReqDto) {
        ingredientService.ingreEdit(newReqDto);
        return ResponseEntity.ok().body(CommonResponse.ok(newReqDto.getId(), "해당 식재료 정보가 수정되었습니다."));
    }

}
