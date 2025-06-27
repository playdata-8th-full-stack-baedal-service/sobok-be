package com.sobok.authservice.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {

    private final SmsCertificationUtil smsCertificationUtil;

    public void SendSms(String phoneNumber) {
        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성
        log.info("생성된 인증 번호 Certification code: {}", certificationCode);
        smsCertificationUtil.sendSMS(phoneNumber, certificationCode);
    }


}
