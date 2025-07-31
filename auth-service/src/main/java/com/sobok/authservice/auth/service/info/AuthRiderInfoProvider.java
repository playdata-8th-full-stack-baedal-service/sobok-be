package com.sobok.authservice.auth.service.info;

import com.sobok.authservice.auth.client.DeliveryClient;
import com.sobok.authservice.auth.dto.info.AuthRiderInfoResDto;
import com.sobok.authservice.auth.dto.info.AuthUserInfoResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("RIDER")
@RequiredArgsConstructor
@Slf4j
public class AuthRiderInfoProvider implements AuthInfoProvider {
    private final DeliveryClient deliveryClient;

    @Override
    public AuthRiderInfoResDto getInfo(Long authId) {
        log.info("라이더 정보 조회 시작");
        return deliveryClient.getInfo(authId).getBody();
    }
}
