package com.sobok.authservice.auth.service;


import com.sobok.authservice.auth.client.DeliveryClient;
import com.sobok.authservice.auth.client.ShopServiceClient;
import com.sobok.authservice.auth.client.UserServiceClient;
import com.sobok.authservice.auth.dto.request.*;
import com.sobok.authservice.auth.dto.response.*;
import com.sobok.authservice.auth.entity.Auth;
//import com.sobok.authservice.auth.feign.UserFeignClient;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.common.dto.ApiResponse;
import com.sobok.authservice.common.dto.TokenUserInfo;
import com.sobok.authservice.common.enums.Role;
import com.sobok.authservice.common.exception.CustomException;
import com.sobok.authservice.common.jwt.JwtTokenProvider;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final UserServiceClient userServiceClient;
    private final ShopServiceClient shopServiceClient;


    private final DeliveryClient deliveryClient;

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

    @Transactional
    public AuthUserResDto userCreate(AuthUserReqDto authUserReqDto) {
        // 회원 Id 가져와서 중복 확인
        authRepository.findByLoginId(authUserReqDto.getLoginId())
                .ifPresent(auth -> {
                    throw new CustomException("이미 존재하는 아이디입니다.", HttpStatus.BAD_REQUEST);
                });

        Auth authEntity = Auth.builder()
                .loginId(authUserReqDto.getLoginId())
                .password(passwordEncoder.encode(authUserReqDto.getPassword()))
                .role(Role.USER)
                .active("Y")
                .build();

        Auth saved = authRepository.save(authEntity); // DB에 저장


        // 사용자 회원가입에 필요한 정보 전달 객체 생성
        UserSignupReqDto messageDto = UserSignupReqDto.builder()
                .authId(saved.getId())
                .nickname(authUserReqDto.getNickname())
                .email(authUserReqDto.getEmail())
                .phone(authUserReqDto.getPhone())
                .photo(authUserReqDto.getPhoto())
                .roadFull(authUserReqDto.getRoadFull())
                .addrDetail(authUserReqDto.getAddrDetail())
                .build();

        // 비동기로 user service에서 회원가입 진행
//        rabbitTemplate.convertAndSend(AUTH_EXCHANGE, USER_SIGNUP_ROUTING_KEY, messageDto);

        try {
            // feign으로 user한테 저장하라고 보내기
            ResponseEntity<Object> response = userServiceClient.userSignup(messageDto);
        } catch (FeignException e) {
            log.error("사용자 정보 저장 실패");
            throw new CustomException("회원가입에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }


        log.info("회원가입 성공: {}", saved);

        return AuthUserResDto.builder()
                .id(saved.getId())
                .nickname(authUserReqDto.getNickname())
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
                log.info("refresh token : {}", storedToken);
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
     *     1. 사용자 검증 및 비밀번호 확인
     *     2. 사용자의 role이 USER 라면 redis에 복구 대상임을 저장
     *     3. 사용자의 active 상태를 N으로 변경
     * </pre>
     */
    public void delete(TokenUserInfo userInfo, AuthPasswordReqDto reqDto) throws EntityNotFoundException, CustomException {
        // 사용자 정보 획득
        Auth auth = authRepository.findById(userInfo.getId()).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 사용자입니다.")
        );

        // 비밀번호가 일치하지 않는다면 예외 처리
        if (!passwordEncoder.matches(reqDto.getPassword(), auth.getPassword())) {
            log.error("비밀번호가 일치하지 않습니다. id : {}", auth.getId());
            throw new CustomException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

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

        // 로그아웃 처리
        logout(userInfo);
    }

    /**
     * <pre>
     *     # 사용자 복구
     *     1. redis에서 복구용 키값이 있는지 확인
     *     2. 있다면 활성화 상태를 Y로 바꾸고 복구용 키 삭제
     * </pre>
     */
    public void recover(Long id) throws EntityNotFoundException, CustomException {
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
    @Transactional
    public AuthRiderResDto riderCreate(AuthRiderReqDto authRiderReqDto) {
        // 라이더 Id 중복 확인
        authRepository.findByLoginId(authRiderReqDto.getLoginId())
                .ifPresent(auth -> {
                    throw new CustomException("이미 존재하는 아이디입니다.", HttpStatus.BAD_REQUEST);

                });

        // 인증 정보 저장
        Auth authEntity = Auth.builder()
                .loginId(authRiderReqDto.getLoginId())
                .password(passwordEncoder.encode(authRiderReqDto.getPassword()))
                .role(Role.RIDER)
                .active("N") // 기본 비활성
                .build();

        Auth saved = authRepository.save(authEntity);

        // delivery-service에 rider 정보 전달
        RiderReqDto riderDto = RiderReqDto.builder()
                .authId(saved.getId())
                .name(authRiderReqDto.getName())
                .phone(authRiderReqDto.getPhone())
                .permissionNumber(authRiderReqDto.getPermissionNumber())
                .build();

        deliveryClient.registerRider(riderDto);

        log.info("라이더 회원가입 완료: {}", saved);

        // 응답 반환
        return AuthRiderResDto.builder()
                .id(saved.getId())
                .name(authRiderReqDto.getName())
                .build();
    }

    @Transactional
    public AuthShopResDto shopCreate(AuthShopReqDto authShopReqDto, TokenUserInfo userInfo) {

        authRepository.findByLoginId(authShopReqDto.getLoginId())
                .ifPresent(auth -> {
                    throw new CustomException("이미 존재하는 가게입니다.", HttpStatus.BAD_REQUEST);
                });

        Auth authEntity = Auth.builder()
                .loginId(authShopReqDto.getLoginId())
                .password(passwordEncoder.encode(authShopReqDto.getPassword()))
                .role(Role.HUB)
                .active("Y")
                .build();

        Auth saved = authRepository.save(authEntity);

        // 사용자 회원가입에 필요한 정보 전달 객체 생성
        ShopSignupReqDto shopDto = ShopSignupReqDto.builder()
                .authId(saved.getId())
                .shopName(authShopReqDto.getShopName())
                .ownerName(authShopReqDto.getOwnerName())
                .phone(authShopReqDto.getPhone())
                .roadFull(authShopReqDto.getRoadFull())
                .build();


        // feign으로 save 요청
        try {
            ApiResponse<AuthShopResDto> response = shopServiceClient.shopSignup(shopDto);
        } catch (Exception e) {
            if (e.getMessage().contains("409")) {
                throw new CustomException("중복된 정보로 인해 가게 등록에 실패했습니다.", HttpStatus.CONFLICT);
            }
            log.error("shop-service 호출 중 예외 발생: {}", e.getMessage());
            throw new CustomException("shop-service와의 통신에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("가게 회원가입 완료: {}", saved);

        return AuthShopResDto.builder()
                .id(saved.getId())
                .shopName(authShopReqDto.getShopName())
                .ownerName(authShopReqDto.getOwnerName())
                .build();

    }


    /**
     * <pre>
     *     # 사용자 Id 찾기
     *     1. 사용자의 전화번호로 user 정보 조회
     * </pre>
     *
     * @return
     */
    public AuthFindIdResDto userFindId(AuthFindIdReqDto authFindIdReqDto) {

        try {
            ApiResponse<UserResDto> response = userServiceClient.findByPhone(authFindIdReqDto.getUserPhoneNumber());

            UserResDto byPhone = response.getData();
            log.info("user-service에서 받아온 user 정보: {}", byPhone.toString());

            Optional<Auth> authById = authRepository.findById(byPhone.getAuthId());

            if (authById.isEmpty()) {
                throw new CustomException("해당 AUTH 정보가 없습니다.", HttpStatus.NOT_FOUND);
            }

            return AuthFindIdResDto.builder()
                    .loginId(authById.get().getLoginId())
                    .build();

        } catch (FeignException.BadRequest e) {
            log.warn("user-service에서 사용자 조회 실패 (400): {}", e.contentUTF8());
            throw new CustomException("입력하신 번호로 가입된 계정이 없습니다.", HttpStatus.NOT_FOUND);
        } catch (FeignException e) {
            log.error("user-service 호출 중 오류 발생: {}", e.getMessage());
            throw new CustomException("회원 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public void resetPassword(AuthResetPwReqDto authResetPwReqDto) {

        try {
            // 전화번호로 user 정보 조회 (user-service)
            ApiResponse<UserResDto> response = userServiceClient.findByPhone(authResetPwReqDto.getUserPhoneNumber());

            UserResDto byPhone = response.getData();

            log.info("user-service에서 받아온 user 정보: {}", byPhone.toString());

            // user에서 authId 추출
            Long authId = byPhone.getAuthId();

            // auth 정보 조회
            Auth auth = authRepository.findById(authId)
                    .orElseThrow(() -> new CustomException("해당 auth 사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

            // 로그인 ID 일치 확인
            if (!auth.getLoginId().equals(authResetPwReqDto.getLoginId())) {
                throw new CustomException("해당 ID를 가진 사용자가 없습니다.", HttpStatus.BAD_REQUEST);
            }

            // 새 비밀번호 암호화 후 저장
            String encodedPassword = passwordEncoder.encode(authResetPwReqDto.getNewPassword());
            auth.changePassword(encodedPassword);
            authRepository.save(auth);

            log.info("비밀번호 변경 완료: authId = {}", authId);

        } catch (FeignException.BadRequest e) {
            log.warn("user-service에서 사용자 조회 실패 (400): {}", e.contentUTF8());
            throw new CustomException("입력하신 번호로 가입된 계정이 없습니다.", HttpStatus.NOT_FOUND);
        } catch (FeignException e) {
            log.error("user-service 호출 중 오류 발생: {}", e.getMessage());
            throw new CustomException("회원 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // 라이더 활성화 기능
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


    public String getTempToken() {
        return jwtTokenProvider.generateTempToken();
    }

}
