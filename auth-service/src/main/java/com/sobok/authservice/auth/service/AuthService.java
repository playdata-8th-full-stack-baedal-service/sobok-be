package com.sobok.authservice.auth.service;


import com.sobok.authservice.auth.dto.request.AuthLoginReqDto;
import com.sobok.authservice.auth.dto.request.AuthReissueReqDto;
import com.sobok.authservice.auth.dto.response.AuthLoginResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.common.dto.TokenUserInfo;
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

    private static final String RECOVERY_KEY = "RECOVERY:";
    private static final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN:";

    public AuthLoginResDto login(AuthLoginReqDto reqDto) throws EntityNotFoundException, IOException, CustomException {
        // 회원 정보 가져오기
        Auth auth = authRepository.findByLoginId(reqDto.getLoginId()).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 사용자입니다.")
        );

        // 사용자이면서 활성화 상태가 아니라면 복구 가능한지 Redis 체크
        if (auth.getActive().equals("N")) {
            // 복구 가능한 역할은 사용자만으로 제한
            if (auth.getRole() == Role.USER) {
                // 복구 가능하다면 recoveryTarget = true로 넘겨주자
                Boolean isRecoveryTarget = redisStringTemplate.hasKey(RECOVERY_KEY + auth.getId().toString());
                if (isRecoveryTarget) {
                    return AuthLoginResDto.builder()
                            .id(auth.getId())
                            .role(auth.getRole().toString())
                            .recoveryTarget(true)
                            .accessToken("")
                            .refreshToken("")
                            .build();
                } else {
                    log.warn("비활성화 사용자 로그인 시도 - ID: {}, 복구 대상 아님", auth.getId());
                    throw new EntityNotFoundException("존재하지 않는 아이디입니다.");
                }
            } else {
                log.error("비활성화 된 회원의 로그인 시도 발생");
                throw new EntityNotFoundException("존재하지 않는 아이디입니다.");
            }
        }

        // 비밀번호 확인
        boolean passwordCorrect = passwordEncoder.matches(reqDto.getPassword(), auth.getPassword());
        if(!passwordCorrect) {
            // 비밀번호 다르다면 -> 예외 터뜨리기
            log.error("비밀번호가 일치하지 않습니다.");
            throw new CustomException("비밀번호가 틀렸습니다.", HttpStatus.FORBIDDEN);
        }

        // 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(auth);
        String refreshToken = jwtTokenProvider.generateRefreshToken(auth);
        if(accessToken.isBlank() || refreshToken.isBlank()) {
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


  
    public Auth userCreate(AuthReqDto authReqDto) {
        Optional<Auth> findId = authRepository.findByLoginId(authReqDto.getLoginId());

//        if (findId.isPresent()) { // 아이디 중복 체크
//            // 예외처리
//        }

        Auth userEntity = Auth.builder()
                .loginId(authReqDto.getLoginId())
//                .password(passwordEncoder.encode(authReqDto.getPassword()))
                .password(authReqDto.getPassword())
                .role(Role.valueOf(authReqDto.getRole().toUpperCase()))
                .active("Y")
                .build();

        Auth save = authRepository.save(userEntity);

        log.info("User created");

        return save;

    }

    public void logout(TokenUserInfo userInfo) {
        // redis에 있는 refresh token 삭제
        redisStringTemplate.delete(REFRESH_TOKEN_KEY + userInfo.getId().toString());
        log.info("{}번 사용자의 로그아웃 성공", userInfo.getId());
    }

    public String reissue(AuthReissueReqDto reqDto) throws EntityNotFoundException, CustomException {
        // redis에 refresh token이 있는 지 확인
        boolean hasRefreshToken = redisStringTemplate.hasKey(REFRESH_TOKEN_KEY + reqDto.getId().toString());
        if (hasRefreshToken) { // 토큰 검증 시작
            String storedToken = redisStringTemplate.opsForValue().get(REFRESH_TOKEN_KEY + reqDto.getId().toString());
            if (storedToken != null && storedToken.equals(reqDto.getRefreshToken())) {
                // 토큰이 일치한다면
                Auth auth = authRepository.findById(reqDto.getId()).orElseThrow(
                        () -> new EntityNotFoundException("존재하지 않는 사용자입니다.")
                );

                // access token 재발급
                log.info("{}번 유저 토큰 재발급", reqDto.getId());
                return jwtTokenProvider.generateAccessToken(auth);
            } else {
                // 토큰이 일치하지 않는다면
                log.warn("refresh 토큰이 일치하지 않습니다.");
                throw new CustomException("토큰이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
        } else { // 재발급 불가능
            log.warn("refresh 토큰이 redis에 존재하지 않습니다.");
            throw new CustomException("저장된 토큰을 찾을 수 없습니다. 다시 로그인해주세요.", HttpStatus.NOT_FOUND);
        }
    }

//    public void riderCreate(AuthReqDto authReqDto) {
//        Optional<Auth> findId = authRepository.findByLoginId(authReqDto.getLoginId());
//
//        Auth.builder()
//    }

}
