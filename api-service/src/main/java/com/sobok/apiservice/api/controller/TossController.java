package com.sobok.apiservice.api.controller;

import com.sobok.apiservice.api.dto.toss.TossPayReqDto;
import com.sobok.apiservice.api.dto.toss.TossPayResDto;
import com.sobok.apiservice.api.service.toss.TossPayService;
import com.sobok.apiservice.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/toss")
@Slf4j
public class TossController {

    private final TossPayService tossPayService;

    /**
     * 토스페이 결제
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody TossPayReqDto reqDto) {
        TossPayResDto resDto = tossPayService.confirmPayment(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(resDto, "정상 처리되었습니다."));
    }
}
