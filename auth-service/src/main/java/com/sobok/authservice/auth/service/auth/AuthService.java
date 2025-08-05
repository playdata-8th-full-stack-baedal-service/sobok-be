package com.sobok.authservice.auth.service.auth;


import com.sobok.authservice.auth.client.DeliveryClient;
import com.sobok.authservice.auth.client.ShopServiceClient;
import com.sobok.authservice.auth.client.UserServiceClient;
import com.sobok.authservice.auth.dto.request.*;
import com.sobok.authservice.auth.dto.response.*;
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
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.sobok.authservice.common.util.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private static final String ACCESS_TOKEN_BLACKLIST_KEY = "BLACKLIST_ACCESS_TOKEN:";
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisStringTemplate;
    private final UserServiceClient userServiceClient;
    private final ShopServiceClient shopServiceClient;
    private final DeliveryClient deliveryClient;
    private final RedisTemplate<String, String> redisTemplate;


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
        if ("N".equals(auth.getActive())) {
            if (!passwordEncoder.matches(reqDto.getPassword(), auth.getPassword())) {
                log.warn("비활성화 계정");
                throw new CustomException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
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
        if (!passwordCorrect) {
            // 비밀번호 다르다면 -> 예외 터뜨리기
            log.error("비밀번호가 일치하지 않습니다.");
            throw new CustomException("비밀번호가 틀렸습니다.", HttpStatus.FORBIDDEN);
        }

        // 로그인 성공 시 블랙리스트 기록 초기화
        // Redis에 남아있던 해당 사용자의 블랙리스트 키를 삭제
        redisTemplate.delete(ACCESS_TOKEN_BLACKLIST_KEY + auth.getId());
        log.info("{}번 사용자의 블랙리스트 기록을 초기화합니다.", auth.getId());
        Long roleId = getRoleId(auth, false);

        return generateAuthLoginResDto(auth, roleId);
    }

    private Long getRoleId(Auth auth, boolean isSocialLogin) {
        return switch (auth.getRole()) {
            case USER -> userServiceClient.getUserId(auth.getId()).getBody();
            case RIDER -> {
                if (!isSocialLogin) yield deliveryClient.getRiderId(auth.getId()).getBody();
                else yield 0L; // socialLoginToken 에서는 RIDER 처리 안함
            }
            case HUB -> {
                if (!isSocialLogin) yield shopServiceClient.getShopId(auth.getId()).getBody();
                else yield 0L; // socialLoginToken 에서는 HUB 처리 안함
            }
            default -> 0L;
        };
    }

    private AuthLoginResDto generateAuthLoginResDto(Auth auth, Long roleId) {
        // 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(auth, roleId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(auth, roleId);

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

    /**
     * <pre>
     *     # 로그아웃
     *     - redis 내에 있는 refresh token 삭제
     * </pre>
     *
     * @param userInfo
     */
    public void logout(TokenUserInfo userInfo, String accessToken) {
        // redis에 있는 refresh token 삭제
        redisStringTemplate.delete(REFRESH_TOKEN_KEY + userInfo.getId().toString());
        // 액세스 토큰 블랙리스트 추가
        jwtTokenProvider.logout(accessToken, userInfo.getId());
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
                // 기존 리프레시 토큰 무효화
                // 새로운 토큰을 발급하기 전에 현재 사용된 리프레시 토큰을 Redis에서 삭제
                // 한 번 사용된 리프레시 토큰은 더 이상 유효하지 않음
                redisStringTemplate.delete(REFRESH_TOKEN_KEY + reqDto.getId().toString());
                log.info("{}번 유저의 기존 리프레시 토큰을 무효화합니다.", reqDto.getId());
                // 토큰이 일치한다면 auth 정보 획득
                Auth auth = authRepository.findById(reqDto.getId()).orElseThrow(
                        () -> new EntityNotFoundException("존재하지 않는 사용자입니다.")
                );

                Long roleId = switch (auth.getRole()) {
                    case USER -> userServiceClient.getUserId(auth.getId()).getBody();
                    case RIDER -> deliveryClient.getRiderId(auth.getId()).getBody();
                    case HUB -> shopServiceClient.getShopId(auth.getId()).getBody();
                    default -> 0L;
                };

                //  Access Token과 Refresh Token을 모두 새로 발급
                String newAccessToken = jwtTokenProvider.generateAccessToken(auth, roleId);
                String newRefreshToken = jwtTokenProvider.generateRefreshToken(auth, roleId);
                // 새로운 리프레시 토큰을 Redis에 저장
                redisStringTemplate.opsForValue().set(
                        REFRESH_TOKEN_KEY + reqDto.getId().toString(),
                        newRefreshToken
                );

                log.info("{}번 유저의 토큰 재발급 성공", reqDto.getId());
                return newAccessToken; // 새 액세스 토큰 반환
            } else {
                // 토큰이 일치하지 않는다면
                log.info("refresh token : {}", storedToken);
                log.warn("refresh 토큰이 일치하지 않습니다.");
                throw new CustomException("토큰이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
        } else { // 재발급 불가능
            log.warn("refresh 토큰이 redis에 존재하지 않습니다.");
            throw new CustomException("저장된 토큰을 찾을 수 없습니다. 다시 로그인해주세요.", HttpStatus.NOT_FOUND);
        }
    }

    public String getTempToken() {
        return jwtTokenProvider.generateTempToken();
    }

    /**
     * 소셜 로그인 토큰 발급
     */
    public AuthLoginResDto socialLoginToken(Long id) {
        // 회원 정보 가져오기
        Auth auth = authRepository.findById(id).orElseThrow(
                () -> new CustomException("존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND)
        );

        Long roleId = getRoleId(auth, true);

        return generateAuthLoginResDto(auth, roleId);
    }


    /**
     * <pre>
     *     1. 사용자 검증 및 비밀번호 확인
     * </pre>
     */
    public void verifyPassword(TokenUserInfo userInfo, AuthPasswordReqDto reqDto) {
        // 사용자 정보 획득
        Auth auth = authRepository.findById(userInfo.getId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

        // 비밀번호가 일치하지 않는다면 예외 처리
        if (!passwordEncoder.matches(reqDto.getPassword(), auth.getPassword())) {
            log.error("비밀번호가 일치하지 않습니다. id : {}", auth.getId());
            throw new CustomException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        log.info("비밀번호 확인 성공. 사용자 ID: {}", auth.getId());
    }

}
