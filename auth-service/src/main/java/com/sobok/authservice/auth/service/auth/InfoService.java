package com.sobok.authservice.auth.service.auth;


import com.sobok.authservice.auth.dto.info.AuthBaseInfoResDto;
import com.sobok.authservice.auth.dto.response.AuthRiderInfoResDto;
import com.sobok.authservice.auth.dto.response.OauthResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.auth.service.info.AuthInfoProvider;
import com.sobok.authservice.auth.service.info.AuthInfoProviderFactory;
import com.sobok.authservice.common.dto.TokenUserInfo;
import com.sobok.authservice.common.enums.Role;
import com.sobok.authservice.common.exception.CustomException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InfoService {

    private final AuthRepository authRepository;
    private final AuthInfoProviderFactory authInfoProviderFactory;

    /**
     * 회원정보 받아오기
     *
     * @return
     */
    public AuthBaseInfoResDto getInfo(TokenUserInfo userInfo, String loginId) {
        AuthBaseInfoResDto info = null;

        // 2. role에 따라 요청 보내기
        try {
            AuthInfoProvider provider = authInfoProviderFactory.getProvider(Role.valueOf(userInfo.getRole()));
            info = provider.getInfo(userInfo.getId());
            info.setLoginId(loginId);
            info.setAuthId(userInfo.getId());
        } catch (IllegalArgumentException e) {
            log.error("Role 변환 과정 중 오류 발생!");
            throw new CustomException("권한 변환 과정에서 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        } catch (FeignException e) {
            log.error("Feign 처리 과정에서 오류가 발생하였습니다.");
            throw new CustomException("정보를 가져오는 도중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException e) {
            log.error("정보를 받아왔지만 Null이 응답되었습니다.");
            throw new CustomException("아무런 정보도 받아오지 못했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return info;
    }

    /**
     * 유저 정보 조회(authId로)
     */
    public String getLoginIdByAuthId(Long authId) {
        return authRepository.findById(authId)
                .map(Auth::getLoginId)
                .orElseThrow(() -> new CustomException("authId에 해당하는 유저가 없습니다.", HttpStatus.NOT_FOUND));
    }

    /**
     * 유저 정보 조회(oauthId로)
     */
    public OauthResDto findByOauthId(Long id) {
        // 회원 정보 가져오기
        Auth auth = authRepository.findByOauthId(id).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 사용자입니다.")
        );

        return OauthResDto.builder()
                .oauthId(auth.getOauthId())
                .authId(auth.getId())
                .build();
    }

    public List<Long> getInactiveRidersInfo() {
        List<Auth> inactiveRiders = authRepository.findInactiveRiders();
        if (inactiveRiders == null) {
            log.error("비활성화된 라이더가 존재하지 않습니다.");
            return new ArrayList<>();
        }

        return inactiveRiders.stream().map(Auth::getId).collect(Collectors.toList());
    }

    /**
     * 라이더 loginId, active 전달
     */
    public AuthRiderInfoResDto getRiderAuthInfo(Long authId) {
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new CustomException("authId에 해당하는 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        return AuthRiderInfoResDto.builder()
                .loginId(auth.getLoginId())
                .active(auth.getActive())
                .build();
    }
}
