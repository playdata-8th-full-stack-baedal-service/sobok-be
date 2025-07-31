package com.sobok.paymentservice.payment.service.validator.deliveryAction;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.payment.dto.delivery.AcceptOrderReqDto;
import com.sobok.paymentservice.payment.entity.Payment;
import com.sobok.paymentservice.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class CompleteDeliveryAction implements DeliveryActionHandler {

    private final PaymentRepository paymentRepository;

    @Override
    public void execute(TokenUserInfo userInfo, Long paymentId, Consumer<AcceptOrderReqDto> deliveryAction, Payment payment) {

        AcceptOrderReqDto reqDto = AcceptOrderReqDto.builder()
                .paymentId(paymentId)
                .riderId(userInfo.getRiderId())
                .build();

        deliveryAction.accept(reqDto);
        payment.nextState();
        paymentRepository.save(payment);

    }
}
