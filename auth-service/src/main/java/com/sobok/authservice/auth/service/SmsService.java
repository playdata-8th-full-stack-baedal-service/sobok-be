package com.sobok.authservice.auth.service;

import com.sobok.authservice.auth.dto.request.SmsReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {

    private final SmsCertificationUtil smsCertificationUtil;
    private final RedisUtil redisUtil;


    public void SendSms(SmsReqDto smsReqDto) {
        String certificationCode = Integer.toString((int) (Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성
        log.info("생성된 인증 번호 Certification code: {}", certificationCode);
        // 3분 동안 Redis에 인증번호 저장
        String key = "auth:verify:" + smsReqDto.getPhone();
        redisUtil.setDataExpire(key, certificationCode, 180);
        smsCertificationUtil.sendSMS(smsReqDto.getPhone(), certificationCode);
    }

    public boolean verifySmsCode(String phoneNumber, String userInputCode) {
        String key = "auth:verify:" + phoneNumber;
        String savedCode = redisUtil.getData(key);
        return savedCode != null && savedCode.equals(userInputCode);
    }


}
