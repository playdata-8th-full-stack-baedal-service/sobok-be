package com.sobok.paymentservice.payment.service.payment;

import com.sobok.paymentservice.payment.dto.payment.ShopAssignDto;
import com.sobok.paymentservice.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    private static final String PAYMENT_EXCHANGE = "payment.exchange";
    private static final String SHOP_ASSIGN_ROUTING_KEY = "payment.shop.assign";

    public void sendShopAssignMessage(Payment payment) {
        ShopAssignDto message = new ShopAssignDto(
                payment.getUserAddressId(),
                payment.getId()
        );

        rabbitTemplate.convertAndSend(PAYMENT_EXCHANGE, SHOP_ASSIGN_ROUTING_KEY, message);
        log.info("MQ 가게 자동 지정 메세지 발행 | message : {}", message);
    }
}
