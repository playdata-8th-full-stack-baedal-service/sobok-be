package com.sobok.authservice.auth.service.info;

import com.sobok.authservice.auth.client.ShopServiceClient;
import com.sobok.authservice.auth.dto.info.AuthShopInfoResDto;
import com.sobok.authservice.auth.dto.info.AuthUserInfoResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("HUB")
@RequiredArgsConstructor
@Slf4j
public class AuthShopInfoProvider implements AuthInfoProvider {
    private final ShopServiceClient shopServiceClient;

    @Override
    public AuthShopInfoResDto getInfo(Long authId) {
        log.info("가게 정보 조회 시작");
        return shopServiceClient.getInfo(authId).getBody();
    }
}
