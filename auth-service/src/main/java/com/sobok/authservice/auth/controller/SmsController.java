package com.sobok.authservice.auth.controller;


import com.sobok.authservice.auth.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<?> SendSMS(@RequestBody String phoneNumber){
        log.info("문자 전송 시작");
        smsService.SendSms(phoneNumber);
        return ResponseEntity.ok("문자를 전송했습니다.");
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(){
        log.info("테스트용");
        return ResponseEntity.ok("test용.");
    }

}
