package com.sobok.apiservice.api.service.socialLogin;

import com.sobok.apiservice.api.client.GoogleFeignClient;
import com.sobok.apiservice.api.client.GoogleTokenFeignClient;
import com.sobok.apiservice.api.dto.google.GoogleDetailReqDto;
import com.sobok.apiservice.api.dto.google.GoogleDetailResDto;
import com.sobok.apiservice.api.dto.google.GoogleReqDto;
import com.sobok.apiservice.api.dto.google.GoogleResDto;
import com.sobok.apiservice.api.repository.OauthRepository;
import com.sobok.apiservice.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleLoginService {

    @Value("${google.client.id}")
    private String googleClientId;
    @Value("${google.client.pw}")
    private String googleClientPw;
    @Value("${google.login-uri}")
    private String googleApiUrl;
    @Value("${google.redirect-uri}")
    private String redirectUrl;

    private final GoogleFeignClient googleFeignClient;
    private final GoogleTokenFeignClient googleTokenFeignClient;

    //백엔드에서 구글 로그인 화면(authorization URL)을 생성해서 프론트에 전달해주는 메서드
    //사용자가 구글 로그인을 시도할 때 구글 인증 페이지로 리다이렉션할 수 있는 URL을 만들어 주는 역할
    public ApiResponse<String> getGoogleLoginView() {

        return ApiResponse.<String>builder()
                .data(googleApiUrl + "client_id=" + googleClientId
                        + "&redirect_uri=" + redirectUrl
                        + "&response_type=code"
                        + "&scope=email%20profile%20openid"
                        + "&access_type=offline")
                .message("google login view url입니다.")
                .build();
    }

    public ApiResponse<GoogleDetailResDto> loginGoogle(String code) {
        GoogleResDto googleTokenResponse = googleTokenFeignClient.getGoogleToken(GoogleReqDto.builder()
                .clientId(googleClientId)
                .clientSecret(googleClientPw)
                .code(code)
                .redirectUri("http://localhost:8000/api-service/api/google-login")
                .grantType("authorization_code")
                .build());

        System.out.println("받은 id_token: " + googleTokenResponse.getId_token());
        String idToken = googleTokenResponse.getId_token();
        GoogleDetailResDto userInfo = googleFeignClient.getUserInfo("Bearer " + googleTokenResponse.getAccess_token());
        System.out.println("받은 userInfo: " + userInfo);

        return ApiResponse.<GoogleDetailResDto>builder()
                .data(userInfo)
                .message("google 계정 정보입니다.")
                .build();
    }

}
