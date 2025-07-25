package com.sobok.paymentservice.payment.service.validator.deliveryAction;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.dto.delivery.AcceptOrderReqDto;
import com.sobok.paymentservice.payment.entity.Payment;
import com.sobok.paymentservice.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
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
public class AssignDeliveryAction implements DeliveryActionHandler {

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final PaymentRepository paymentRepository;

    @Override
    public void execute(TokenUserInfo userInfo, Long paymentId, Consumer<AcceptOrderReqDto> deliveryAction, Payment payment) {

        String lockKey = "delivery-lock:" + paymentId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new CustomException("다른 라이더가 처리 중입니다.", HttpStatus.CONFLICT);
            }

            String cacheKey = "delivery-accepted:" + paymentId;
            if (redisTemplate.hasKey(cacheKey)) {
                throw new CustomException("이미 승인된 배달입니다.", HttpStatus.CONFLICT);
            }

            AcceptOrderReqDto reqDto = AcceptOrderReqDto.builder()
                    .paymentId(paymentId)
                    .riderId(userInfo.getRiderId())
                    .build();

            deliveryAction.accept(reqDto);
            payment.nextState();
            paymentRepository.save(payment);

            redisTemplate.opsForValue().set(cacheKey, "true", Duration.ofMinutes(5));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException("락 처리 중 예외 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
