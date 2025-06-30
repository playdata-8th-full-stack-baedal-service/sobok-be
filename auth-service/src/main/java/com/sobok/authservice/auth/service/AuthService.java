package com.sobok.authservice.auth.service;


import com.sobok.authservice.auth.dto.request.AuthLoginReqDto;
import com.sobok.authservice.auth.dto.request.AuthReissueReqDto;
import com.sobok.authservice.auth.dto.request.AuthRiderReqDto;
import com.sobok.authservice.auth.dto.request.AuthShopReqDto;
import com.sobok.authservice.auth.dto.response.AuthLoginResDto;
import com.sobok.authservice.auth.dto.response.AuthResDto;
import com.sobok.authservice.auth.dto.response.AuthRiderResDto;
import com.sobok.authservice.auth.dto.response.AuthShopResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.common.dto.TokenUserInfo;
import com.sobok.authservice.common.enums.Role;
import com.sobok.authservice.common.exception.CustomException;
import com.sobok.authservice.common.jwt.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

import com.sobok.authservice.auth.dto.request.AuthReqDto;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

import static com.sobok.authservice.common.util.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisStringTemplate;
    private final RabbitTemplate rabbitTemplate;

    /**
     * <pre>
     *  # 로그인  처리
     *  1. 로그인 아이디를 통해 회원 정보 획득
     *  2. 복구 가능 대상인지 확인 (Active 상태 확인 포함)
     *  3. 비밀번호 확인
     *  4. 토큰 발급
     * </pre>
     */
    public AuthLoginResDto login(AuthLoginReqDto reqDto) throws EntityNotFoundException, CustomException {
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
                log.error("비활성화된 계정의 로그인 시도 발생");
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

        // 토큰 저장
        jwtTokenProvider.saveRefreshToken(auth, refreshToken);

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

        // 비동기로 user service에서 회원가입 진행
        rabbitTemplate.convertAndSend(AUTH_EXCHANGE, USER_SIGNUP_ROUTING_KEY, authReqDto);

        log.info("회원가입 성공: {}", saved);

        return AuthResDto.builder()
                .id(saved.getId())
                .nickname(authReqDto.getNickname())
                .build();

    }

    /**
     * <pre>
     *     # 로그아웃
     *     - redis 내에 있는 refresh token 삭제
     * </pre>
     * @param userInfo
     */
    public void logout(TokenUserInfo userInfo) {
        // redis에 있는 refresh token 삭제
        redisStringTemplate.delete(REFRESH_TOKEN_KEY + userInfo.getId().toString());
        log.info("{}번 사용자의 로그아웃 성공", userInfo.getId());
    }

    /**
     * <pre>
     *     # 토큰 재발급
     *     1. redis에 refresh 토큰이 있는 지 확인
     *     2. 있다면 토큰이 일치하는 지 확인
     *     3. 일치하면 access 토큰 재발급
     * </pre>
     */
    public String reissue(AuthReissueReqDto reqDto) throws EntityNotFoundException, CustomException {
        // redis에 refresh token이 있는 지 확인
        boolean hasRefreshToken = redisStringTemplate.hasKey(REFRESH_TOKEN_KEY + reqDto.getId().toString());
        if (hasRefreshToken) {
            // 토큰 검증 시작 - 저장된 token 꺼내기
            String storedToken = redisStringTemplate.opsForValue().get(REFRESH_TOKEN_KEY + reqDto.getId().toString());
            if (storedToken != null && storedToken.equals(reqDto.getRefreshToken())) {
                // 토큰이 일치한다면 auth 정보 획득
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

    /**
     * <pre>
     *     # 사용자 비활성화
     *     1. 사용자의 role이 USER 라면 redis에 복구 대상임을 저장
     *     2. 사용자의 active 상태를 N으로 변경
     * </pre>
     */
    public void delete(TokenUserInfo userInfo) throws EntityNotFoundException {
        // 사용자 정보 획득
        Auth auth = authRepository.findById(userInfo.getId()).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 사용자입니다.")
        );

        // 사용자의 role이 USER 라면 redis에 저장
        if (auth.getRole() == Role.USER) {
            // 회원 아이디 값으로 redis에 복구 대상임을 알 수 있는 정보 저장, value는 auth의 id 값
           redisStringTemplate.opsForValue().set(RECOVERY_KEY + auth.getId().toString(), auth.getId().toString(), Duration.ofDays(RECOVERY_DAY));
        }

        // 활성화 상태 N으로 바꾸기
        auth.changeActive(false);

        // DB 저장
        authRepository.save(auth);

        log.info("{}번 사용자를 비활성화했습니다.", userInfo.getId());
    }

    /**
     * <pre>
     *     # 사용자 복구
     *     1. redis에서 복구용 키값이 있는지 확인
     *     2. 있다면 활성화 상태를 Y로 바꾸고 복구용 키 삭제
     * </pre>
     */
    public void recover(Long id) throws EntityNotFoundException, CustomException {
        // TODO : 인증 없이 복구하는 로직. 만약 복구하는 데 인증이 필요하다면 이전 단계에서 인증용 API를 먼저 호출하였는지 확인하는 작업이 필요함.
        // 복구 대상인지 확인
        boolean isRecoveryTarget = redisStringTemplate.hasKey(RECOVERY_KEY + id);
        if (isRecoveryTarget) {
            // 사용자 정보 가져오기
            Auth auth = authRepository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException("존재하지 않는 사용자입니다.")
            );

            // 활성화 상태 Y로 바꾸기
            auth.changeActive(true);

            // DB 저장
            authRepository.save(auth);

            // redis에 있는 복구용 key 삭제
            redisStringTemplate.delete(RECOVERY_KEY + id);

            log.warn("{}번 사용자의 복구가 완료되었습니다.", id);
        } else {
            log.warn("{}번 사용자는 복구 대상이 아닙니다.", id);
            throw new CustomException("복구 대상이 아닌 계정입니다.", HttpStatus.NOT_FOUND);
        }
    }


//    public void riderCreate(AuthReqDto authReqDto) {
//        Optional<Auth> findId = authRepository.findByLoginId(authReqDto.getLoginId());
//
//        Auth.builder()
//    }

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
