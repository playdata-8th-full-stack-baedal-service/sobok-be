package com.sobok.paymentservice.payment.service.validator;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.enums.OrderState;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.dto.delivery.DeliveryResDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component("HUB")  //Spring이 자동으로 map으로 주입해줌. HUB가 맵의 key
public class HubRoleValidator implements RoleValidator {

    @Override
    public boolean supports(String role) {
        return "HUB".equals(role);
    }

    @Override
    public void validate(TokenUserInfo userInfo, DeliveryResDto delivery) {
        if (!Objects.equals(userInfo.getShopId(), delivery.getShopId())) {
            throw new CustomException("해당 HUB는 이 주문에 접근할 수 없습니다.", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public List<OrderState> allowedStates() {
        return List.of(OrderState.PREPARING_INGREDIENTS);
    }
}
