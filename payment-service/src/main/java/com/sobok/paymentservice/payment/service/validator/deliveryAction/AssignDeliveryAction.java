package com.sobok.paymentservice.payment.service.validator.deliveryAction;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.dto.delivery.AcceptOrderReqDto;
import com.sobok.paymentservice.payment.entity.Payment;
import com.sobok.paymentservice.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssignDeliveryAction implements DeliveryActionHandler {

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final PaymentRepository paymentRepository;

    @Override
    public void execute(TokenUserInfo userInfo, Long paymentId, Consumer<AcceptOrderReqDto> deliveryAction, Payment payment) {

        //분산 락의 키
        String lockKey = "delivery-lock:" + paymentId;
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;
        try {
            // 캐시 히트 체크 (키가 있으면 이미 수락된 상태) -> 이미 수락된 배달이면 종료
            //Redis 캐시 키
            String cacheKey = "delivery-accepted:" + paymentId;

            String value = redisTemplate.opsForValue().get(cacheKey);
            log.info("value: {}", value);

            if (redisTemplate.hasKey(cacheKey)) {
                throw new CustomException("다른 라이더가 처리 중입니다.", HttpStatus.CONFLICT);
            }

            // 최대 5초 동안 락 획득을 시도하고, 락을 획득하면 10초 동안 점유. 자동 해제
            isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new CustomException("다른 라이더가 승인 중입니다.", HttpStatus.CONFLICT);
            }

            // 승인 처리
            AcceptOrderReqDto reqDto = AcceptOrderReqDto.builder()
                    .paymentId(paymentId)
                    .riderId(userInfo.getRiderId())
                    .build();

            deliveryAction.accept(reqDto);

            // 상태 변경 및 저장
            payment.nextState();
            paymentRepository.save(payment);

            // 캐시에 해당 배달이 수락됐음을 기록 (TTL 5분. 추후 히트 방지)
            redisTemplate.opsForValue().set(cacheKey, "true", Duration.ofMinutes(2));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException("락 처리 중 예외 발생. 락 획득 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
