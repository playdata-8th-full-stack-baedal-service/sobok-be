package com.sobok.apiservice.api.service.socialLogin;

import com.sobok.apiservice.api.client.AuthFeignClient;
import com.sobok.apiservice.api.dto.kakao.*;
import com.sobok.apiservice.api.dto.social.SocialUserDto;
import com.sobok.apiservice.api.entity.Oauth;
import com.sobok.apiservice.api.repository.OauthRepository;
import com.sobok.apiservice.common.exception.CustomException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoLoginService {

    @Value("${oauth2.kakao.client-id}")
    private String kakaoClientId;
    @Value("${oauth2.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final SocialLoginService socialLoginService;

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

    // Access Token으로 사용자 정보 얻어오기
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

    public KakaoCallResDto kakaoCallback(String code) {
        // 인가코드로 액세스토큰 받기
        String kakaoAccessToken = getKakaoAccessToken(code);
        // 액세스토큰으로 사용자 정보 받기
        KakaoUserResDto kakaoUserDto = getKakaoUserInfo(kakaoAccessToken);
        SocialUserDto socialDto = SocialUserDto.builder()
                .socialId(kakaoUserDto.getId().toString())
                .provider("KAKAO")
                .build();
        // 회원가입 or 로그인 처리
        OauthResDto oauthResDto = socialLoginService.findOrCreateKakaoUser(socialDto);  //authId와 닉네임

        log.info("oauthResDto: {}", oauthResDto);

        KakaoCallResDto.Properties properties = KakaoCallResDto.Properties.builder()
                .nickname(kakaoUserDto.getProperties().getNickname())
                .profileImage(kakaoUserDto.getProperties().getProfileImage())
                .build();

        KakaoCallResDto.KakaoAccount account = KakaoCallResDto.KakaoAccount.builder()
                .email(kakaoUserDto.getAccount().getEmail())
                .build();

        return KakaoCallResDto.builder()
                .oauthId(oauthResDto.getOauthId())
                .authId(oauthResDto.getAuthId())
                .isNew(oauthResDto.isNew())
                .properties(properties)
                .account(account)
                .build();
    }
}
