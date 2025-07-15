package com.sobok.apiservice.api.service.socialLogin;

import com.sobok.apiservice.api.client.AuthFeignClient;
import com.sobok.apiservice.api.client.GoogleFeignClient;
import com.sobok.apiservice.api.client.GoogleTokenFeignClient;
import com.sobok.apiservice.api.dto.google.*;
import com.sobok.apiservice.api.dto.kakao.OauthResDto;
import com.sobok.apiservice.api.entity.Oauth;
import com.sobok.apiservice.api.repository.OauthRepository;
import com.sobok.apiservice.common.dto.ApiResponse;
import com.sobok.apiservice.common.exception.CustomException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    private final OauthRepository oauthRepository;
    private final AuthFeignClient authFeignClient;

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

    public GoogleDetailResDto loginGoogle(String code) {
        GoogleResDto googleTokenResponse = googleTokenFeignClient.getGoogleToken(GoogleReqDto.builder()
                .clientId(googleClientId)
                .clientSecret(googleClientPw)
                .code(code)
                .redirectUri("http://localhost:8000/api-service/api/google-login")
                .grantType("authorization_code")
                .build());

        System.out.println("받은 id_token: " + googleTokenResponse.getId_token());
        GoogleDetailResDto userInfo = googleFeignClient.getUserInfo("Bearer " + googleTokenResponse.getAccess_token());
        return userInfo;
    }

    //응답받은 sub값으로 oauth db에 있는지 조회
    public GoogleCallResDto googleCallback(String code) {
        // 인가 코드에서 얻은 액세스토큰으로 사용자 정보 받기
        GoogleDetailResDto googleDetailResDto = loginGoogle(code);
        log.info("받은 googleDetailResDto 사용자 정보: {}", googleDetailResDto);

        // 회원가입 or 로그인 처리
        OauthResDto oauthResDto = findOrCreateKakaoUser(googleDetailResDto);  //authId와 닉네임
        log.info("oauthResDto: {}", oauthResDto);

        return GoogleCallResDto.builder()
                .oauthId(oauthResDto.getOauthId())
                .authId(oauthResDto.getAuthId())
                .name(googleDetailResDto.getName())
                .email(googleDetailResDto.getEmail())
                .picture(googleDetailResDto.getPicture())
                .isNew(oauthResDto.isNew())
                .build();
    }

    @Transactional
    public OauthResDto findOrCreateKakaoUser(GoogleDetailResDto dto) {
        // 기존 구글 로그인 사용자
        // 구글 ID로 기존 사용자 찾기
        Optional<Oauth> oauth = oauthRepository.findBySocialProviderAndSocialId("GOOGLE", dto.getSub());

        log.info("dto.getSub(): {} oauth가 있다면: {}", dto.getSub(), oauth);

        // 기존 사용자 존재
        if (oauth.isPresent()) {
            log.info("기존에 구글 소셜 로그인한 유저입니다.");
            Oauth foundUser = oauth.get();
            OauthResDto oauthResDto;
            try {
                oauthResDto = authFeignClient.authIdById(foundUser.getId());
            } catch (FeignException.NotFound e) {
                log.info("oauth는 생성하였지만 회원가입을 마치지 못한 유저입니다.");
                return OauthResDto.builder()
                        .oauthId(foundUser.getId())
                        .socialId(foundUser.getSocialId())
                        .isNew(true)
                        .build();
            } catch (FeignException e) {
                log.error("Feign 호출 중 예외 발생", e);
                throw new CustomException("OAuth 정보 조회 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info("oauthResDto: {}", oauthResDto);
            return OauthResDto.builder()
                    .oauthId(foundUser.getId())
                    .socialId(foundUser.getSocialId())
                    .authId(oauthResDto.getAuthId())
                    .isNew(false)
                    .build();
        }

        // 처음 구글 로그인 한 사람 -> 새 사용자 생성. oauth
        log.info("구글 로그인으로 처음 방문한 신규 유저입니다. 회원가입 진행해야 됨");

        Oauth build = Oauth.builder()
                .socialProvider("GOOGLE")
                .socialId(dto.getSub())
                .build();

        Oauth saved = oauthRepository.save(build);

        log.info("saved: {}", saved);

        return OauthResDto.builder()
                .oauthId(saved.getId())
                .isNew(true)
                .build();
    }

}
