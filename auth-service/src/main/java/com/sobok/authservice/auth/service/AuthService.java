package com.sobok.authservice.auth.service;

import com.sobok.authservice.auth.dto.request.AuthLoginReqDto;
import com.sobok.authservice.auth.dto.response.AuthLoginResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.common.enums.Role;
import com.sobok.authservice.common.exception.CustomException;
import com.sobok.authservice.common.jwt.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;



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
            } else {
                log.error("비활성화 된 회원의 로그인 시도 발생");
                throw new EntityNotFoundException("존재하지 않는 아이디입니다.");
            }
        }

        // 비밀번호 확인
        boolean passwordCorrect = passwordEncoder.encode(reqDto.getPassword()).matches(auth.getPassword());
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
}
