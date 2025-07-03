package com.sobok.cookservice.cook.controller;

import com.sobok.cookservice.cook.dto.request.CookCreateReqDto;
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

    @PostMapping("cook-register")
    public ResponseEntity<?> registerCook(@RequestBody CookCreateReqDto dto) {
        cookService.createCook(dto);
        return ResponseEntity.ok().body("요리 등록 완료");
    }


}
