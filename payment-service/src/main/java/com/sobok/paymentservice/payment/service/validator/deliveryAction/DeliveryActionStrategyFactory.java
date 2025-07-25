package com.sobok.paymentservice.payment.service.validator.deliveryAction;

import com.sobok.paymentservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryActionStrategyFactory {

    private final AssignDeliveryAction assignDeliveryAction;
    private final CompleteDeliveryAction completeDeliveryAction;

    public DeliveryActionHandler getStrategy(String state) {
        return switch (state.toLowerCase()) {
            case "assign" -> assignDeliveryAction;
            case "complete" -> completeDeliveryAction;
            default -> throw new CustomException("지원하지 않는 상태: " + state, HttpStatus.BAD_REQUEST);
        };
    }
}
