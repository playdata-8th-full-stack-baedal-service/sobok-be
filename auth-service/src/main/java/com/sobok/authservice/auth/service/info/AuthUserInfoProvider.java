package com.sobok.authservice.auth.service.info;

import com.sobok.authservice.auth.client.UserServiceClient;
import com.sobok.authservice.auth.dto.info.AuthUserInfoResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.common.dto.TokenUserInfo;
import com.sobok.authservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component("USER")
@RequiredArgsConstructor
@Slf4j
public class AuthUserInfoProvider implements AuthInfoProvider {
    private final UserServiceClient userServiceClient;
    private final AuthRepository authRepository;

    @Override
    public AuthUserInfoResDto getInfo(TokenUserInfo userInfo) {
        log.info("사용자 정보찾기 시작");
        AuthUserInfoResDto authUserInfoResDto = userServiceClient.getUserInfo(userInfo.getUserId()).getBody();
        log.info("authUserInfoResDto: {}", authUserInfoResDto);
        Auth auth = authRepository.findById(Objects.requireNonNull(authUserInfoResDto).getAuthId())
                .orElseThrow(() -> new CustomException("해당 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND));
        Objects.requireNonNull(authUserInfoResDto).setSocialUser(auth.getOauthId() != null);
        authUserInfoResDto.setAuthId(auth.getId());
        authUserInfoResDto.setLoginId(auth.getLoginId());
        return authUserInfoResDto;
    }
}
