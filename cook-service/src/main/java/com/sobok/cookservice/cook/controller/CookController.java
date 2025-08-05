package com.sobok.cookservice.cook.controller;

import com.sobok.cookservice.common.dto.CommonResponse;
import com.sobok.cookservice.cook.controller.docs.CookControllerDocs;
import com.sobok.cookservice.cook.dto.request.CookCreateReqDto;
import com.sobok.cookservice.cook.dto.response.*;
import com.sobok.cookservice.cook.service.CookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cook")
@RequiredArgsConstructor
@Slf4j
public class CookController implements CookControllerDocs {
    private final CookService cookService;

    /**
     * 요리 등록
     */
    @PostMapping("/cook-register")
    public ResponseEntity<?> registerCook(@Valid @RequestBody CookCreateReqDto dto) {
        CookCreateResDto resDto = cookService.createCook(dto);
        return ResponseEntity.ok().body(CommonResponse.ok(resDto, "요리 등록 성공"));
    }

    /**
     * 요리 전체 조회
     */
    @GetMapping("/get-cook")
    public ResponseEntity<?> getCook(@RequestParam Long pageNo, @RequestParam Long numOfRows) {
        List<CookResDto> resDto = cookService.getCook(pageNo, numOfRows);
        return ResponseEntity.ok().body(CommonResponse.ok(resDto, "페이징으로 요청한 모든 요리가 정상적으로 조회되었습니다."));
    }

    /**
     * 요리 검색
     */
    @GetMapping("/search-cook")
    public ResponseEntity<?> getCook(@RequestParam String keyword, @RequestParam Long pageNo, @RequestParam Long numOfRows) {
        List<CookResDto> resDto = cookService.searchCook(keyword, pageNo, numOfRows);
        return ResponseEntity.ok().body(CommonResponse.ok(resDto, "페이징으로 요청한 키워드 검색 요리가 정상적으로 조회되었습니다."));
    }

    /**
     * 요리 카테고리 조회
     */
    @GetMapping("/get-cook-category")
    public ResponseEntity<?> getCookCategory(@RequestParam String category, @RequestParam Long pageNo, @RequestParam Long numOfRows) {
        List<CookResDto> resDto = cookService.getCookByCategory(category, pageNo, numOfRows);
        return ResponseEntity.ok().body(CommonResponse.ok(resDto, "페이징으로 요청한 카테고리 검색이 정상적으로 조회되었습니다."));
    }

    /**
     * 요리 단건 조회
     */
    @GetMapping("/get-cook/{id}")
    public ResponseEntity<?> getCookById(@PathVariable(name = "id") Long cookId) {
        CookIndividualResDto resDto = cookService.getCookById(cookId);
        return ResponseEntity.ok().body(CommonResponse.ok(resDto, "입력한 요리 아이디에 맞는 요리 정보가 조회되었습니다."));
    }

    /**
     * 한달 주문량 기준 요리 페이지 조회
     */
    @GetMapping("/popular-12")
    public PagedResponse<PopularCookResDto> getPopularCooks(@RequestParam int page,
                                                            @RequestParam int size) {
        return cookService.getPopularCooks(page, size);
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getMonthlyHotCooks(@RequestParam int pageNo, @RequestParam int numOfRows) {
        List<MonthlyHotCookDto> resDto = cookService.getMonthlyHotCooks(pageNo, numOfRows);
        return ResponseEntity.ok().body(CommonResponse.ok(resDto, "페이징으로 요청한 한달 주문량 순 요리 검색 정보가 조회되었습니다."));
    }
}
