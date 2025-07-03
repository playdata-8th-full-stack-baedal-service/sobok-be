package com.sobok.authservice.auth.service.info;

import com.sobok.authservice.auth.client.UserServiceClient;
import com.sobok.authservice.auth.dto.info.AuthBaseInfoResDto;
import com.sobok.authservice.auth.dto.info.AuthUserInfoResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("USER")
@RequiredArgsConstructor
@Slf4j
public class AuthUserInfoProvider implements AuthInfoProvider {
    private final UserServiceClient userServiceClient;

    @Override
    public AuthUserInfoResDto getInfo(Long authId) {
        log.info("사용자 정보찾기 시작");
        return userServiceClient.getUserInfo(authId).getBody();
    }
}
