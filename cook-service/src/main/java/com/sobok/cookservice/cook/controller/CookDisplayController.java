package com.sobok.cookservice.cook.controller;

import com.sobok.cookservice.common.dto.ApiResponse;
import com.sobok.cookservice.cook.dto.display.BasicCookDisplay;
import com.sobok.cookservice.cook.dto.display.DisplayParamDto;
import com.sobok.cookservice.cook.service.CookDisplayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cooks")
public class CookDisplayController {
    private final CookDisplayService service;

    @GetMapping()
    public ResponseEntity<?> getCooks(@RequestParam(required = false) String category,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String sort,
                                      @RequestParam Long pageNo,
                                      @RequestParam Long numOfRows)
    {
        List<BasicCookDisplay> result = service.getCooks(new DisplayParamDto(category, keyword, sort, pageNo, numOfRows));
        return ResponseEntity.ok().body(ApiResponse.ok(result, "요청 조건에 맞는 요리가 모두 조회되었습니다."));
    }
}
