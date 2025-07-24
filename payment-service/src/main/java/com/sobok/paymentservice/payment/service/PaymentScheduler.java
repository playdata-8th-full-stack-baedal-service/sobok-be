package com.sobok.paymentservice.payment.service;

import com.sobok.paymentservice.payment.client.CookFeignClient;
import com.sobok.paymentservice.payment.dto.cart.CartMonthlyHotDto;
import com.sobok.paymentservice.payment.dto.cart.MonthlyHot;
import com.sobok.paymentservice.payment.repository.CartCookQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.sobok.paymentservice.payment.entity.QCartCook.cartCook;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentScheduler {

    private final CartCookQueryRepository  cartCookQueryRepository;
    private final CookFeignClient cookFeignClient;

    @Scheduled(cron = "0 0 * * * *")
    public void scheduledOrderCountFeign() {
        // 현재 시각 기준으로 한달 전 주문 조회
        long monthToMillis = LocalDateTime.now().minusMonths(1)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // 주문량 순 요리 조회
        List<MonthlyHot> monthlyHotList = cartCookQueryRepository.getMonthlyHotCartCook(monthToMillis);

        // cook service로 영혼 보내기
        cookFeignClient.updateMonthlyHotCooks(monthlyHotList);
    }
}
