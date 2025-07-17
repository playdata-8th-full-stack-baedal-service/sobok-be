package com.sobok.authservice.auth.service.auth;

import com.sobok.authservice.auth.client.DeliveryClient;
import com.sobok.authservice.auth.client.ShopServiceClient;
import com.sobok.authservice.auth.client.UserServiceClient;
import com.sobok.authservice.auth.dto.request.*;
import com.sobok.authservice.auth.dto.response.AuthFindIdResDto;
import com.sobok.authservice.auth.dto.response.ByPhoneResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.auth.service.etc.SmsService;
import com.sobok.authservice.common.dto.ApiResponse;
import com.sobok.authservice.common.exception.CustomException;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;
    private final ShopServiceClient shopServiceClient;
    private final DeliveryClient deliveryClient;
    private final SmsService smsService;


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
                            userServiceClient.findByPhone(authFindIdReqDto.getUserPhoneNumber())
//                            shopServiceClient.findByPhone(authFindIdReqDto.getUserPhoneNumber())
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

    /**
     * 비밀번호 검증
     *
     * @return loginId
     */
    public String verifyByPassword(Long id) {
        Auth auth = authRepository.findById(id).orElseThrow(
                () -> new CustomException("해당하는 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND)
        );

//        boolean isMatch = passwordEncoder.matches(reqDto.getPassword(), auth.getPassword());
//        if (!isMatch) {
//            throw new CustomException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
//        }

        return auth.getLoginId();
    }
}
