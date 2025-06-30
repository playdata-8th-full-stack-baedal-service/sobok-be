package com.sobok.authservice.auth.service;


import com.sobok.authservice.auth.dto.request.AuthLoginReqDto;
import com.sobok.authservice.auth.dto.request.AuthRiderReqDto;
import com.sobok.authservice.auth.dto.request.AuthShopReqDto;
import com.sobok.authservice.auth.dto.response.AuthLoginResDto;
import com.sobok.authservice.auth.dto.response.AuthResDto;
import com.sobok.authservice.auth.dto.response.AuthRiderResDto;
import com.sobok.authservice.auth.dto.response.AuthShopResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.common.enums.Role;
import com.sobok.authservice.common.exception.CustomException;
import com.sobok.authservice.common.jwt.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

import com.sobok.authservice.auth.dto.request.AuthReqDto;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisStringTemplate;

    private static final String RECOVERY_KEY = "recovery:";

    public AuthLoginResDto login(AuthLoginReqDto reqDto) throws EntityNotFoundException, IOException {
        // 회원 정보 가져오기
        Auth auth = authRepository.findByLoginId(reqDto.getLoginId()).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 아이디입니다.")
        );

        // 사용자이면서 활성화 상태가 아니라면 복구 가능한지 Redis 체크
        if (auth.getActive().equals("N")) {
            if (auth.getRole() == Role.USER) {
                // TODO : Redis에 올라가있는지 확인
                // 복구 가능하다면 recoveryTarget = true로 넘겨주자
                redisStringTemplate.hasKey(RECOVERY_KEY + auth.getId());
            } else {
                log.error("비활성화 된 회원의 로그인 시도 발생");
                throw new EntityNotFoundException("존재하지 않는 아이디입니다.");
            }
        }

        // 비밀번호 확인
        boolean passwordCorrect = passwordEncoder.encode(reqDto.getPassword()).matches(auth.getPassword());
        if (!passwordCorrect) {
            // 비밀번호 다르다면 -> 예외 터뜨리기
            log.error("비밀번호가 일치하지 않습니다.");
            throw new CustomException("비밀번호가 틀렸습니다.", HttpStatus.FORBIDDEN);
        }

        // 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(auth);
        String refreshToken = jwtTokenProvider.generateRefreshToken(auth);
        if (accessToken.isBlank() || refreshToken.isBlank()) {
            log.error("토큰 생성 과정에서 오류가 발생했습니다.");
            throw new IOException("토큰 생성 과정에서 오류가 발생했습니다.");
        }

        log.info("로그인 성공 : {}", auth.getId());

        return AuthLoginResDto.builder()
                .id(auth.getId())
                .role(auth.getRole().toString())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .recoveryTarget(false)
                .build();
    }

    public AuthResDto userCreate(AuthReqDto authReqDto) {
        Optional<Auth> findByLoginId = authRepository.findByLoginId(authReqDto.getLoginId());

        if (findByLoginId.isPresent()) {
            throw new CustomException("이미 존재하는 아이디입니다.", HttpStatus.BAD_REQUEST);
        }

        Auth userEntity = Auth.builder()
                .loginId(authReqDto.getLoginId())
                .password(passwordEncoder.encode(authReqDto.getPassword()))
                .role(Role.USER)
                .active("Y")
                .build();

        Auth saved = authRepository.save(userEntity);

        log.info("회원가입 성공: {}", saved);

        return AuthResDto.builder()
                .id(saved.getId())
                .nickname(authReqDto.getNickname())
                .build();

    }

    public AuthRiderResDto riderCreate(AuthRiderReqDto authRiderReqDto) {
        Optional<Auth> findByLoginId = authRepository.findByLoginId(authRiderReqDto.getLoginId());

        if (findByLoginId.isPresent()) {
            throw new CustomException("이미 존재하는 아이디", HttpStatus.BAD_REQUEST);
        }

        Auth riderEntity = Auth.builder()
                .loginId(authRiderReqDto.getLoginId())
                .password(passwordEncoder.encode(authRiderReqDto.getPassword()))
                .role(Role.RIDER)
                .active("N") // 라이더 기본값 N
                .build();

        Auth saved = authRepository.save(riderEntity);

        log.info("라이더 회원가입 완료: {}", saved);

        return AuthRiderResDto.builder()
                .id(saved.getId())
                .name(authRiderReqDto.getName())
                .build();
    }

    public AuthShopResDto shopCreate(AuthShopReqDto authShopReqDto) {
        Optional<Auth> findByLoginId = authRepository.findByLoginId(authShopReqDto.getLoginId());

        if (findByLoginId.isPresent()) {
            throw new CustomException("이미 존재하는 아이디입니다.", HttpStatus.BAD_REQUEST);
        }
        Auth shopEntity = Auth.builder()
                .loginId(authShopReqDto.getLoginId())
                .password(passwordEncoder.encode(authShopReqDto.getPassword()))
                .role(Role.HUB)
                .active("Y")
                .build();

        Auth saved = authRepository.save(shopEntity);

        log.info("가게 회원가입 완료: {}", saved);

        return AuthShopResDto.builder()
                .id(saved.getId())
                .shopName(authShopReqDto.getShopName())
                .build();

    }
}
