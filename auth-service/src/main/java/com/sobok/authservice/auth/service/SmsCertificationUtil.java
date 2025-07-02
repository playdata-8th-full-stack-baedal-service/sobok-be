package com.sobok.authservice.auth.service;

import com.sobok.authservice.common.exception.CustomException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SmsCertificationUtil {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.fromNumber}") // 발신자 번호 주입
    private String fromNumber;

    DefaultMessageService messageService; // 메시지 서비스를 위한 객체

    @PostConstruct // 의존성 주입이 완료된 후 초기화를 수행하는 메서드
    public void init(){
        messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr"); // 메시지 서비스 초기화
    }

    // 단일 메시지 발송
    public void sendSMS(String phoneNumber, String certificationCode){
        try {
            log.info("phone number: {}", phoneNumber);
            Message message = new Message(); // 새 메시지 객체 생성
            message.setFrom(fromNumber); // 발신자 번호 설정
            message.setTo(phoneNumber); // 수신자 번호 설정
            message.setText("본인 확인 인증번호는 " + certificationCode + "입니다."); // 메시지 내용 설정
            // 임시 주석. 테스트 할 시 주석 해제
//            messageService.sendOne(new SingleMessageSendingRequest(message)); // 메시지 발송 요청
        } catch (Exception e) {
            throw new CustomException("인증번호 전송에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
