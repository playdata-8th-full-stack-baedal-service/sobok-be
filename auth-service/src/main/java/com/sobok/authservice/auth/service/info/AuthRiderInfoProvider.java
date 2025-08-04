package com.sobok.authservice.auth.service.info;

import com.sobok.authservice.auth.client.DeliveryClient;
import com.sobok.authservice.auth.dto.info.AuthRiderInfoResDto;
import com.sobok.authservice.auth.dto.info.AuthUserInfoResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.common.dto.TokenUserInfo;
import com.sobok.authservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component("RIDER")
@RequiredArgsConstructor
@Slf4j
public class AuthRiderInfoProvider implements AuthInfoProvider {
    private final DeliveryClient deliveryClient;
    private final AuthRepository authRepository;

    @Override
    public AuthRiderInfoResDto getInfo(TokenUserInfo userInfo) {
        log.info("라이더 정보 조회 시작");
        AuthRiderInfoResDto authRiderInfoResDto = deliveryClient.getInfo(userInfo.getRiderId()).getBody();
        Auth auth = authRepository.findById(authRiderInfoResDto.getAuthId())
                .orElseThrow(() -> new CustomException("해당 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND));
        authRiderInfoResDto.setAuthId(auth.getId());
        authRiderInfoResDto.setLoginId(auth.getLoginId());
        return authRiderInfoResDto;
    }
}
