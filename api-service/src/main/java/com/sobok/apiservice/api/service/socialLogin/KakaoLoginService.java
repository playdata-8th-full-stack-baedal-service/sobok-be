package com.sobok.apiservice.api.service.socialLogin;

import com.sobok.apiservice.api.client.AuthFeignClient;
import com.sobok.apiservice.api.dto.kakao.AuthLoginResDto;
import com.sobok.apiservice.api.dto.kakao.AuthSignupReqDto;
import com.sobok.apiservice.api.dto.kakao.OauthResDto;
import com.sobok.apiservice.api.dto.kakao.KakaoUserResDto;
import com.sobok.apiservice.api.entity.Oauth;
import com.sobok.apiservice.api.repository.OauthRepository;
import com.sobok.apiservice.common.exception.CustomException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoLoginService {

    @Value("${oauth2.kakao.client-id}")
    private String kakaoClientId;
    @Value("${oauth2.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    //    private final RestTemplate restTemplate;
    private final AuthFeignClient authFeignClient;
    private final OauthRepository oauthRepository;
    private final PasswordEncoder passwordEncoder;


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

    @Transactional
    public OauthResDto findOrCreateKakaoUser(KakaoUserResDto dto) {
        // 기존 카카오 로그인 사용자
        // 카카오 ID로 기존 사용자 찾기
        Optional<Oauth> oauth = oauthRepository.findBySocialProviderAndSocialId("KAKAO", dto.getId().toString());

        log.info("dto.getId(): {} oauth가 있을까요 없을까요: {}", dto.getId(), oauth);

        // 기존 사용자 존재
        if (oauth.isPresent()) {
            log.info("기존에 카카오 소셜 로그인한 유저입니다.");
            Oauth foundUser = oauth.get();
            OauthResDto oauthResDto = authFeignClient.authIdById(foundUser.getId());
            log.info("oauthResDto: {}", oauthResDto);
            return OauthResDto.builder()
                    .id(foundUser.getId())
                    .authId(oauthResDto.getAuthId())
                    .isNew(false)
                    .build();
        }

        // 기존 계정(email)으로 가입한 유저인지 확인
        // 구현 안함

        // 처음 카카오 로그인 한 사람 -> 새 사용자 생성. oauth + auth
        log.info("카카오 로그인으로 처음 방문한 신규 유저입니다. 회원가입 시작");

        Oauth kakao = Oauth.builder()
                .socialProvider("KAKAO")
                .socialId(dto.getId().toString())
                .build();

        Oauth saved = oauthRepository.save(kakao);

        log.info("saved: {}", saved);

        // 임시 password 생성
        String dummyPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        AuthSignupReqDto authEntity = AuthSignupReqDto.builder()
                .id(saved.getId())  //oauthId
                .loginId("kakao" + dto.getId().toString())  //로그인아이디 만들기
                .password(dummyPassword)
//                    .role(Role.USER)
//                    .active("Y")
                .build();

        log.info("auth로 페인 요청해서 저장할 authEntity: {}", authEntity);
        OauthResDto oauthResDto;
        try {
            // feign으로 auth한테 저장하라고 보내기
            oauthResDto = authFeignClient.authSignup(authEntity);
            log.info("auth 회원가입 성공 response: {}", oauthResDto);
        } catch (FeignException e) {
            log.error("사용자 정보 저장 실패");
            throw new CustomException("회원가입에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return oauthResDto;
    }

    public AuthLoginResDto kakaoLoginToken(Long id) {  //authId
        return authFeignClient.kakaoToken(id);
    }

}
