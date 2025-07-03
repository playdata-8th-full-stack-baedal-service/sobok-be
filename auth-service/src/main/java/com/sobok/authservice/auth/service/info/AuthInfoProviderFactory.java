package com.sobok.authservice.auth.service.info;

import com.sobok.authservice.auth.dto.info.AuthUserInfoResDto;
import com.sobok.authservice.common.enums.Role;
import com.sobok.authservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInfoProviderFactory {
    private final Map<String, AuthInfoProvider> providerMap;

    public AuthInfoProvider getProvider(Role role) {
        AuthInfoProvider provider = providerMap.get(role.name());
        if (provider == null) {
            log.error("권한에 대한 회원 정보 조회 제공 서비스가 없습니다. : {}", role.name());
            throw new CustomException("사용자 조회에 대한 잘못된 권한 접근입니다.", HttpStatus.BAD_REQUEST);
        }

        return provider;
    }

}
