package com.sobok.authservice.auth.service.etc;

import com.sobok.authservice.auth.client.UserServiceClient;
import com.sobok.authservice.auth.dto.request.SmsReqDto;
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
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.fromNumber}") // 발신자 번호 주입
    private String fromNumber;

    DefaultMessageService messageService; // 메시지 서비스를 위한 객체

    private final RedisService redisService;
    private final UserServiceClient userServiceClient;


    public void SendSms(SmsReqDto smsReqDto) {

        // 전화번호 중복 검증 -> 아이디 찾기에서도 사용되기 때문에 주석 처리하겠습니다.
//        if (userServiceClient.existsByPhone(smsReqDto.getPhone())) {
//            throw new CustomException("이미 사용 중인 전화번호입니다.", HttpStatus.BAD_REQUEST);
//        }

        String certificationCode = Integer.toString((int) (Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성
        // 3분 동안 Redis에 인증번호 저장
        String key = "auth:verify:" + smsReqDto.getPhone();
        redisService.setDataExpire(key, certificationCode, 300);
        sendSMS(smsReqDto.getPhone(), certificationCode);
    }

    public boolean verifySmsCode(String phoneNumber, String userInputCode) {
        String key = "auth:verify:" + phoneNumber;
        String savedCode = redisService.getData(key);
        return savedCode != null && savedCode.equals(userInputCode);
    }

    @PostConstruct // 의존성 주입이 완료된 후 초기화를 수행하는 메서드
    public void init() {
        messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr"); // 메시지 서비스 초기화
    }

    // 단일 메시지 발송
    public void sendSMS(String phoneNumber, String certificationCode) {
        try {
            Message message = new Message(); // 새 메시지 객체 생성
            message.setFrom(fromNumber); // 발신자 번호 설정
            message.setTo(phoneNumber); // 수신자 번호 설정
            message.setText("본인 확인 인증번호는 " + certificationCode + "입니다."); // 메시지 내용 설정
            // 임시 주석. 테스트 할 시 주석 해제
            messageService.sendOne(new SingleMessageSendingRequest(message)); // 메시지 발송 요청
        } catch (Exception e) {
            throw new CustomException("인증번호 전송에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }
    }

}
