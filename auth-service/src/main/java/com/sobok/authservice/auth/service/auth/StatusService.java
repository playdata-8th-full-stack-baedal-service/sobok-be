package com.sobok.authservice.auth.service.auth;

import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.common.dto.TokenUserInfo;
import com.sobok.authservice.common.enums.Role;
import com.sobok.authservice.common.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Objects;

import static com.sobok.authservice.common.util.Constants.RECOVERY_DAY;
import static com.sobok.authservice.common.util.Constants.RECOVERY_KEY;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatusService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisStringTemplate;
    private final AuthService authService;

    /**
     * <pre>
     *     # 사용자 비활성화
     *     1. 사용자의 role이 USER 라면 redis에 복구 대상임을 저장
     *     2. 사용자의 active 상태를 N으로 변경
     * </pre>
     */
    public void delete(TokenUserInfo userInfo) {
        Auth auth = authRepository.findById(userInfo.getId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

        // 사용자의 role이 USER 라면 redis에 저장
        if (auth.getRole() == Role.USER) {
            // 회원 아이디 값으로 redis에 복구 대상임을 알 수 있는 정보 저장, value는 auth의 id 값
            redisStringTemplate.opsForValue().set(
                    RECOVERY_KEY + auth.getId(),
                    auth.getId().toString(),
                    Duration.ofDays(RECOVERY_DAY)
            );
        }
        // 활성화 상태 N으로 바꾸기
        auth.changeActive(false);

        // DB 저장
        authRepository.save(auth);

        // 로그아웃 처리
        log.info("{}번 사용자를 비활성화했습니다.", userInfo.getId());

        authService.logout(userInfo);
    }

    /**
     * <pre>
     *     # 사용자 복구
     *     1. redis에서 복구용 키값이 있는지 확인
     *     2. 있다면 활성화 상태를 Y로 바꾸고 복구용 키 삭제
     * </pre>
     */
    public void recover(Long id, TokenUserInfo userInfo) throws EntityNotFoundException, CustomException {
        if(!Objects.equals(userInfo.getId(), id))  {
            throw new CustomException("복구 대상이 아닌 계정입니다.", HttpStatus.BAD_REQUEST);
        }

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

    /**
     * 라이더 활성화 기능
     */
    @Transactional
    public void activeRider(Long authId) {
        // Id 검증
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 없습니다."));

        // Role이 RIDER 인지 검증
        if (auth.getRole() != Role.RIDER) {
            throw new CustomException("배달원만 활성화 가능합니다.", HttpStatus.BAD_REQUEST);
        }
        // 라이더 active 활성화 상태로 변경
        auth.changeActive(true);
        authRepository.save(auth);
    }
}
