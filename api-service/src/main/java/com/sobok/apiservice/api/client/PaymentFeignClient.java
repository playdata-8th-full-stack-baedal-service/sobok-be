package com.sobok.apiservice.api.client;

import com.sobok.apiservice.api.dto.toss.TossPayResDto;
import com.sobok.apiservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", configuration = FeignConfig.class)
public interface PaymentFeignClient {
    @PostMapping("/api/register-payment")
    void registerPayment(@RequestBody TossPayResDto resDto);
    // TODO : 결제 등록 만들어야 함
    // 1. OrderState에 결제 대기 만들어놓기
    // 2. 프론트에서 toss로 보내기 전에 우리 서버에 결제 정보 등록하고 대기상태로 설정
    // 3. 결제 승인 나면 payment로 가서 정보 기입
    // 4. 결제 승인 쪽 로직에 MQ 로 자동 배정 시키기 만들기
}
