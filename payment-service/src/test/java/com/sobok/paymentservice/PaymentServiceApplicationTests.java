package com.sobok.paymentservice;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.enums.DeliveryState;
import com.sobok.paymentservice.payment.client.DeliveryFeignClient;
import com.sobok.paymentservice.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class PaymentServiceApplicationTests {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private DeliveryFeignClient deliveryFeignClient;

    @Test
    void concurrentAcceptDelivery_shouldAllowOnlyOneSuccess() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount); // threadCount만큼 쓰레드 풀 생성
        CountDownLatch latch = new CountDownLatch(threadCount);  // 모든 쓰레드 종료 대기용
        AtomicInteger successCount = new AtomicInteger(0); // 성공 횟수 저장
        AtomicInteger failureCount = new AtomicInteger(0); // 실패 횟수 저장

        Long testPaymentId = 5L; // 주문 Id, OrderState.READY_FOR_DELIVERY 상태여야 함

        for (int i = 0; i < threadCount; i++) {
            final long riderId = i + 4L; // 라이더 ID 4부터 시작해서 4,5,6,...103

            Runnable task = () -> {
                // 테스트용 라이더 정보 생성
                TokenUserInfo dynamicUserInfo = TokenUserInfo.builder()
                        .id(1L)
                        .role("RIDER")
                        .userId(null)
                        .riderId(riderId)
                        .shopId(null)
                        .build();

                try {
                    // 배달 처리 시도
                    paymentService.processDeliveryAction(
                            dynamicUserInfo,
                            testPaymentId,
                            DeliveryState.ASSIGN,
                            deliveryFeignClient::assignRider
                    );
                    successCount.incrementAndGet(); // 성공 카운트 증가
                } catch (Exception e) {
                    failureCount.incrementAndGet(); // 실패 카운트 증가
                } finally {
                    latch.countDown(); // 작업 완료 알림
                }
            };
            executor.submit(task); // 쓰레드풀에 작업 제출
        }

        latch.await(); // 모든 작업이 끝날 때까지 기다림
        executor.shutdown(); // 쓰레드풀 종료 요청

        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("실패 횟수: " + failureCount.get());

        assertThat(successCount.get()).isEqualTo(1); // 성공한 작업은 1개여야 함
        assertThat(failureCount.get()).isEqualTo(threadCount - 1); // 실패한 작업은 나머지(총 쓰레드 수 - 1)여야 함
    }

}