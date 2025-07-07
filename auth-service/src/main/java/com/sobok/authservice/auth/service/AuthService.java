package com.sobok.authservice.auth.service;


import com.sobok.authservice.auth.client.DeliveryClient;
import com.sobok.authservice.auth.client.ShopServiceClient;
import com.sobok.authservice.auth.client.UserServiceClient;
import com.sobok.authservice.auth.dto.info.AuthBaseInfoResDto;
import com.sobok.authservice.auth.dto.request.*;
import com.sobok.authservice.auth.dto.response.*;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.auth.service.info.AuthInfoProvider;
import com.sobok.authservice.auth.service.info.AuthInfoProviderFactory;
import com.sobok.authservice.common.dto.ApiResponse;
import com.sobok.authservice.common.dto.TokenUserInfo;
import com.sobok.authservice.common.enums.Role;
import com.sobok.authservice.common.exception.CustomException;
import com.sobok.authservice.common.jwt.JwtTokenProvider;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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

    private final SmsService smsService;

    private final AuthInfoProviderFactory authInfoProviderFactory;

    @Value("${oauth2.kakao.client-id}")
    private String kakaoClientId;
    @Value("${oauth2.kakao.redirect-uri}")
    private String kakaoRedirectUri;

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

        Long roleId = switch (auth.getRole()) {
            case USER -> userServiceClient.getUserId(auth.getId());
            case RIDER -> deliveryClient.getRiderId(auth.getId());
            case HUB -> shopServiceClient.getShopId(auth.getId());
            default ->  0L;
        };

        // 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(auth, roleId);
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

    /**
     * <pre>
     *     # 사용자 회원가입
     *     1. 로그인 ID 중복 여부 확인
     *     2. 비밀번호 암호화 후 Auth 엔티티 생성 및 저장
     *     3. User 서비스로 사용자 정보 전달
     *     4. 회원가입 성공 시 AuthUserResDto 반환
     * </pre>
     *
     * @param authUserReqDto 회원가입 요청 데이터
     * @return AuthUserResDto 회원가입 후 응답 데이터
     */
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

                Long roleId = switch (auth.getRole()) {
                    case USER -> userServiceClient.getUserId(auth.getId());
                    case RIDER -> deliveryClient.getRiderId(auth.getId());
                    case HUB -> shopServiceClient.getShopId(auth.getId());
                    default ->  0L;
                };

                // access token 재발급
                log.info("{}번 유저 토큰 재발급", reqDto.getId());
                return jwtTokenProvider.generateAccessToken(auth, roleId);
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

    /**
     * <pre>
     *     # 라이더 회원가입
     *     1. 로그인 ID 중복 여부 확인
     *     2. 비밀번호 암호화 후 Auth(RIDER) 엔티티 생성 및 저장 (기본 비활성 상태)
     *     3. delivery-service에 라이더 정보 전달
     *     4. 회원가입 성공 시 AuthRiderResDto 반환
     * </pre>
     *
     * @param authRiderReqDto 라이더 회원가입 요청 데이터
     * @return AuthRiderResDto 회원가입 후 응답 데이터
     */
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

    /**
     * <pre>
     *     # 가게 등록
     *     1. 로그인 ID 중복 여부 확인
     *     2. 비밀번호 암호화 후 Auth(HUB) 엔티티 생성 및 저장
     *     3. shop-service에 가게 정보 전달
     *     4. 예외 발생 시 상태에 따라 상세 응답 처리
     *     5. 회원가입 성공 시 AuthShopResDto 반환
     * </pre>
     *
     * @param authShopReqDto 가게 회원가입 요청 데이터
     * @param userInfo       로그인한 사용자 정보
     * @return AuthShopResDto 회원가입 후 응답 데이터
     */
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
    public List<AuthFindIdResDto> userFindId(AuthFindIdReqDto authFindIdReqDto) {
        boolean isVerified = smsService.verifySmsCode(authFindIdReqDto.getUserPhoneNumber(), authFindIdReqDto.getUserInputCode());

        log.info("isVerified: {}", isVerified);

        if (!isVerified) {
            throw new CustomException("인증번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        try {
            log.info("문자 검증 통과");

            // 모든 서비스에 요청보내서 유효한 값만 리스트에 담기
            List<ApiResponse<ByPhoneResDto>> response = Stream.of(
                            userServiceClient.findByPhone(authFindIdReqDto.getUserPhoneNumber()),
                            shopServiceClient.findByPhone(authFindIdReqDto.getUserPhoneNumber())
//                            deliveryClient.findByPhone(authFindIdReqDto.getUserPhoneNumber())
                    ).filter(resp -> resp != null && resp.getData() != null)
                    .toList();

            log.info("페인으로 응답받은 response <ByPhoneResDto>: {}", response);

            // 각 응답에서 authId 추출 → authRepository 조회 → AuthFindIdResDto로 변환
            List<AuthFindIdResDto> result = new ArrayList<>();

            for (ApiResponse<ByPhoneResDto> res : response) {
                Long authId = res.getData().getAuthId();

                Optional<Auth> authById = authRepository.findByIdAndActive(authId, "Y");

                authById.ifPresent(auth -> {
                    AuthFindIdResDto dto = AuthFindIdResDto.builder()
                            .loginId(auth.getLoginId())
                            .build();

                    result.add(dto);
                });
            }

            // 결과가 없다면 예외
            if (result.isEmpty()) {
                throw new CustomException("해당 AUTH 정보가 없습니다.", HttpStatus.NOT_FOUND);
            }

            return result;

        } catch (FeignException.BadRequest e) {
            log.warn("feign 요청에서 사용자 조회 실패 (400): {}", e.contentUTF8());
            throw new CustomException("입력하신 번호로 가입된 계정이 없습니다.", HttpStatus.NOT_FOUND);
        } catch (FeignException e) {
            log.error("feign 호출 중 오류 발생: {}", e.getMessage());
            throw new CustomException("회원 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * 통합 비밀번호 찾기
     */
    //1단계
    public Long authVerification(@Valid AuthVerifyReqDto authVerifyReqDto) {
        //아이디로 auth.loginId가 존재하는지 확인, active "Y"
        Auth auth = authRepository.findByLoginIdAndActive(authVerifyReqDto.getLoginId(), "Y")
                .orElseThrow(() -> new CustomException("해당 ID의 사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

        log.info("auth: {}", auth.toString());
        log.info("role: {}", auth.getRole());

        // 전화번호로 정보 조회 (각 service) - authId와 해당 전화번호의 authId가 같은지 확인
        ApiResponse<ByPhoneResDto> findAuth;

        switch (auth.getRole()) {
            case USER -> {
                findAuth = userServiceClient.findByPhone(authVerifyReqDto.getUserPhoneNumber());
                if (findAuth == null || !findAuth.isSuccess() || findAuth.getData() == null) {
                    throw new CustomException("유저 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
                }
            }

            case RIDER -> {
                findAuth = deliveryClient.findByPhone(authVerifyReqDto.getUserPhoneNumber());
                if (findAuth == null || !findAuth.isSuccess() || findAuth.getData() == null) {
                    throw new CustomException("라이더 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
                }
            }

            case HUB -> {
                findAuth = shopServiceClient.findByPhone(authVerifyReqDto.getUserPhoneNumber());
                if (findAuth == null || !findAuth.isSuccess() || findAuth.getData() == null) {
                    throw new CustomException("허브 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
                }
            }

            default -> throw new CustomException("알 수 없는 사용자 역할입니다.", HttpStatus.BAD_REQUEST);
        }

        // auth ID 일치 확인
        if (!auth.getId().equals(findAuth.getData().getAuthId())) {
            throw new CustomException("해당 ID와 전화번호를 가진 사용자가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // 화면단에서는 (authVerifyReqDto 정보로 통과했을 때) 문자 전송보내고 인증번호 입력하는 박스가 나옴
        // -> 통과하지 못했다면 "사용자 정보 없음" 에러 던지기 - 그게 위에 있음
        // 같은지 확인하면 sms 문자 전송
        SmsReqDto smsReqDto = SmsReqDto.builder().phone(authVerifyReqDto.getUserPhoneNumber()).build();
        try {
            smsService.SendSms(smsReqDto);
        } catch (Exception e) {
            throw new CustomException("인증번호 전송에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return auth.getId();

    }

    //2단계 비밀번호 업데이트
    public void resetPassword(AuthResetPwReqDto authResetPwReqDto) {

        try {
            // auth 정보 조회
            Auth auth = authRepository.findById(authResetPwReqDto.getAuthId())
                    .orElseThrow(() -> new CustomException("해당 auth 사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

            // 새 비밀번호 암호화 후 저장
            String encodedPassword = passwordEncoder.encode(authResetPwReqDto.getNewPassword());
            auth.changePassword(encodedPassword);
            authRepository.save(auth);

            log.info("비밀번호 변경 완료: authId = {}", authResetPwReqDto.getAuthId());

        } catch (Exception e) {
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

    /**
     * loginId 중복 체크
     */
    public void checkLoginId(String loginId) {
        if (authRepository.findByLoginId(loginId).isPresent()) {
            throw new CustomException("이미 사용 중인 아이디입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * nickname 중복 체크
     */
    public void checkNickname(String nickname) {
        if (userServiceClient.checkNickname(nickname)) {
            throw new CustomException("이미 사용 중인 닉네임입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * email 중복 체크
     */
    public void checkEmail(String email) {
        if (userServiceClient.checkEmail(email)) {
            throw new CustomException("이미 사용 중인 이메일입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 라이더 면허번호 중복 체크
     */
    public void checkPermission(String permission) {
        if (deliveryClient.checkPermission(permission)) {
            throw new CustomException("사용할 수 없는 면허번호 입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 가게 이름 중복 체크
     */
    public void checkShopName(String shopName) {
        if (shopServiceClient.checkShopName(shopName)) {
            throw new CustomException("이미 등록된 지점명 입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 가게 주소 중복 체크
     */
    public void checkShopAddress(String shopAddress) {
        if (shopServiceClient.checkShopAddress(shopAddress)) {
            throw new CustomException("중복된 가게 주소 입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 비밀번호 검증
     *
     * @return loginId
     */
    public String verifyByPassword(Long id, AuthPasswordReqDto reqDto) {
        Auth auth = authRepository.findById(id).orElseThrow(
                () -> new CustomException("해당하는 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND)
        );

        boolean isMatch = passwordEncoder.matches(reqDto.getPassword(), auth.getPassword());
        if (!isMatch) {
            throw new CustomException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        return auth.getLoginId();
    }

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

    // 인가 코드로 카카오 액세스 토큰 받기
    public String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // 요청 URI
        String requestUri = "https://kauth.kakao.com/oauth/token";

        // 헤더정보 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 바디정보 세팅
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);
        map.add("redirect_uri", kakaoRedirectUri);
        map.add("client_id", kakaoClientId);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                requestUri, HttpMethod.POST, request, Map.class
        );

        // 응답 데이터에서 JSON 추출
        Map<String, Object> responseJSON
                = (Map<String, Object>) responseEntity.getBody();

        log.info("응답 JSON 데이터: {}", responseJSON);

        // Access Token 추출 (카카오 로그인 중인 사용자의 정보를 요청할 때 필요한 토큰)
        String accessToken = (String) responseJSON.get("access_token");

        return accessToken;

    }

    // Access Token으로 사용자 정보 얻어오기!
    public KakaoUserResDto getKakaoUserInfo(String kakaoAccessToken) {
        String requestUri = "https://kapi.kakao.com/v2/user/me";

        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "Bearer " + kakaoAccessToken);

        // 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KakaoUserResDto> response = restTemplate.exchange(
                requestUri,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                KakaoUserResDto.class
        );

        KakaoUserResDto dto = response.getBody();
        log.info("응답된 사용자 정보: {}", dto);

        return dto;

    }
}
