package com.sobok.authservice.auth.service.info;

import com.sobok.authservice.auth.client.UserServiceClient;
import com.sobok.authservice.auth.dto.info.AuthUserInfoResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
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
    public AuthUserInfoResDto getInfo(Long authId) {
        log.info("사용자 정보찾기 시작");
        AuthUserInfoResDto authUserInfoResDto = userServiceClient.getUserInfo(authId).getBody();
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new CustomException("해당 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND));
        Objects.requireNonNull(authUserInfoResDto).setSocialUser(auth.getOauthId() != null);
        return authUserInfoResDto;
    }
}
