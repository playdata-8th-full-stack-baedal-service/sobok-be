package com.sobok.authservice.auth.service.auth;

import com.sobok.authservice.auth.client.ApiServiceClient;
import com.sobok.authservice.auth.client.DeliveryClient;
import com.sobok.authservice.auth.client.ShopServiceClient;
import com.sobok.authservice.auth.client.UserServiceClient;
import com.sobok.authservice.auth.dto.request.*;
import com.sobok.authservice.auth.dto.response.AuthRiderResDto;
import com.sobok.authservice.auth.dto.response.AuthShopResDto;
import com.sobok.authservice.auth.dto.response.AuthUserResDto;
import com.sobok.authservice.auth.dto.response.OauthResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.auth.service.etc.SmsService;
import com.sobok.authservice.common.enums.Role;
import com.sobok.authservice.common.exception.CustomException;
import com.sobok.authservice.common.util.PasswordGenerator;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;
    private final ShopServiceClient shopServiceClient;
    private final DeliveryClient deliveryClient;
    private final ApiServiceClient apiServiceClient;
    private final SmsService smsService;


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
        Long authId = createAndRegisterUser(UnifiedSignupReqDto.builder()
                .loginId(authUserReqDto.getLoginId())
                .password(authUserReqDto.getPassword())
                .oauthId(null)
                .nickname(authUserReqDto.getNickname())
                .email(authUserReqDto.getEmail())
                .phone(authUserReqDto.getPhone())
                .photo(authUserReqDto.getPhoto())
                .roadFull(authUserReqDto.getRoadFull())
                .addrDetail(authUserReqDto.getAddrDetail())
                .inputCode(authUserReqDto.getInputCode())
                .build());

        return AuthUserResDto.builder()
                .id(authId)
                .nickname(authUserReqDto.getNickname())
                .build();
    }

    /**
     * 일반/소셜 사용자 회원가입 공통 로직
     *
     * @param dto
     */
    @Transactional
    public Long createAndRegisterUser(UnifiedSignupReqDto dto) {
        // ID 중복 검사 (loginId 기준)
        authRepository.findByLoginId(dto.getLoginId()).ifPresent(auth -> {
            throw new CustomException("이미 존재하는 아이디입니다.", HttpStatus.BAD_REQUEST);
        });

        //sms 인증번호 검증
        if (!smsService.verifySmsCode(dto.getPhone(), dto.getInputCode())) {
            throw new CustomException("인증된 전화번호가 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        // 사진 등록
        String photoUrl;
        try {
            photoUrl = apiServiceClient.registerImg(dto.getPhoto()).getBody();
        } catch (Exception e) {
            log.error("사진 등록 실패", e);
            photoUrl = null;
        }

        // Auth 저장
        Auth auth = Auth.builder()
                .loginId(dto.getLoginId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .oauthId(dto.getOauthId())
                .role(Role.USER)
                .active("Y")
                .build();

        Auth saved = authRepository.save(auth);

        // 유저 정보 전달
        UserSignupReqDto messageDto = UserSignupReqDto.builder()
                .authId(saved.getId())
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .photo(photoUrl)
                .roadFull(dto.getRoadFull())
                .addrDetail(dto.getAddrDetail())
                .build();

        try {
            userServiceClient.userSignup(messageDto);
        } catch (FeignException e) {
            log.error("사용자 정보 저장 실패", e);
            throw new CustomException("회원가입에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return saved.getId();
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
     * @return AuthShopResDto 회원가입 후 응답 데이터
     */
    @Transactional
    public AuthShopResDto shopCreate(AuthShopReqDto authShopReqDto) {

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
            ResponseEntity<AuthShopResDto> response = shopServiceClient.shopSignup(shopDto);
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
     * 소셜 회원가입
     */
    @Transactional
    public void socialUserCreate(@Valid AuthByOauthReqDto authByOauthReqDto) {
        authRepository.findByOauthId(authByOauthReqDto.getOauthId()).ifPresent(auth -> {
            throw new CustomException("이미 등록된 회원입니다.", HttpStatus.CONFLICT);
        });

        ResponseEntity<OauthResDto> oauthResDto = apiServiceClient.oauthIdById(authByOauthReqDto.getOauthId());
        if (oauthResDto == null || !oauthResDto.getStatusCode().is2xxSuccessful() || oauthResDto.getBody() == null) {
            throw new CustomException("oauth 정보가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        String dummyId = "social" + oauthResDto.getBody().getSocialId();
        String dummyPassword = PasswordGenerator.generate(12);

        createAndRegisterUser(UnifiedSignupReqDto.builder()
                .loginId(dummyId)
                .password(dummyPassword)
                .oauthId(authByOauthReqDto.getOauthId())
                .nickname(authByOauthReqDto.getNickname())
                .email(authByOauthReqDto.getEmail())
                .phone(authByOauthReqDto.getPhone())
                .photo(authByOauthReqDto.getPhoto())
                .roadFull(authByOauthReqDto.getRoadFull())
                .addrDetail(authByOauthReqDto.getAddrDetail())
                .inputCode(authByOauthReqDto.getInputCode())
                .build());
        log.info("회원가입 성공: {}", oauthResDto);
    }

    /**
     * loginId 중복 체크
     */
    public void checkLoginId(String loginId) {
        if (authRepository.findByLoginId(loginId).isPresent()) {
            throw new CustomException("이미 사용 중인 아이디입니다.", HttpStatus.BAD_REQUEST);
        }
    }

}
