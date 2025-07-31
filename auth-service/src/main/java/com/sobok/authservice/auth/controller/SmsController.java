package com.sobok.authservice.auth.controller;


import com.sobok.authservice.auth.dto.request.SmsReqDto;
import com.sobok.authservice.auth.dto.request.VerificationReqDto;
import com.sobok.authservice.auth.service.etc.SmsService;
import com.sobok.authservice.common.dto.ApiResponse;
import com.sobok.authservice.common.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@Slf4j
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    public ResponseEntity<?> SendSMS(@RequestBody @Valid SmsReqDto smsReqDto) {
        smsService.SendSms(smsReqDto);
        return ResponseEntity.ok(ApiResponse.ok("문자를 전송했습니다."));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody @Valid VerificationReqDto request) {
        log.info("인증번호 검증 요청 phoneNumber: {}, inputCode: {}", request.getPhoneNumber(), request.getInputCode());

        boolean isValid = smsService.verifySmsCode(request.getPhoneNumber(), request.getInputCode());

        if (isValid) {
            return ResponseEntity.ok(ApiResponse.ok("인증 성공"));
        } else {
            throw new CustomException("인증번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }


}
