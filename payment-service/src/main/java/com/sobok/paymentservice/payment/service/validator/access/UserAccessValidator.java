package com.sobok.paymentservice.payment.service.validator.access;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.dto.delivery.DeliveryResDto;
import com.sobok.paymentservice.payment.entity.CartCook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserAccessValidator implements RoleAccessValidator {

    @Override
    public String getRole() {
        return "USER";
    }

    public void validate(TokenUserInfo userInfo, List<CartCook> cartCookList, DeliveryResDto delivery) {
        // 사용자 검증
        // 주문한 유저가 본인인지 확인
        cartCookList.forEach(cart -> {
            if (!cart.getUserId().equals(userInfo.getUserId())) {
                throw new CustomException("주문한 사용자만 조회가능합니다.", HttpStatus.FORBIDDEN);
            }
        });
    }
}
