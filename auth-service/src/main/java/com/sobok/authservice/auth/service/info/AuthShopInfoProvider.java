package com.sobok.authservice.auth.service.info;

import com.sobok.authservice.auth.client.ShopServiceClient;
import com.sobok.authservice.auth.dto.info.AuthShopInfoResDto;
import com.sobok.authservice.auth.dto.info.AuthUserInfoResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.common.dto.TokenUserInfo;
import com.sobok.authservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component("HUB")
@RequiredArgsConstructor
@Slf4j
public class AuthShopInfoProvider implements AuthInfoProvider {
    private final ShopServiceClient shopServiceClient;
    private final AuthRepository authRepository;

    @Override
    public AuthShopInfoResDto getInfo(TokenUserInfo userInfo) {
        log.info("가게 정보 조회 시작");
        AuthShopInfoResDto authShopInfoResDto = shopServiceClient.getInfo(userInfo.getShopId()).getBody();
        Auth auth = authRepository.findById(authShopInfoResDto.getAuthId())
                .orElseThrow(() -> new CustomException("해당 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND));
        authShopInfoResDto.setAuthId(auth.getId());
        authShopInfoResDto.setLoginId(auth.getLoginId());
        return authShopInfoResDto;
    }
}
