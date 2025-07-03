package com.sobok.cookservice.cook.controller;

import com.sobok.cookservice.common.dto.ApiResponse;
import com.sobok.cookservice.cook.dto.request.CookCreateReqDto;
import com.sobok.cookservice.cook.dto.response.CookCreateResDto;
import com.sobok.cookservice.cook.service.CookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cook")
@RequiredArgsConstructor
@Slf4j
public class CookController {
    private final CookService cookService;

    /**
     * 요리 등록
     */
    @PostMapping("/cook-register")
    public ResponseEntity<?> registerCook(@RequestBody CookCreateReqDto dto) {
        CookCreateResDto resDto = cookService.createCook(dto);
        return ResponseEntity.ok().body(ApiResponse.ok(resDto, "요리 등록 성공"));
    }

}
