package com.sobok.apiservice.api.service.socialLogin;

import com.sobok.apiservice.api.client.AuthFeignClient;
import com.sobok.apiservice.api.dto.kakao.AuthLoginResDto;
import com.sobok.apiservice.api.dto.kakao.KakaoUserResDto;
import com.sobok.apiservice.api.dto.kakao.OauthResDto;
import com.sobok.apiservice.api.dto.social.SocialUserDto;
import com.sobok.apiservice.api.entity.Oauth;
import com.sobok.apiservice.api.repository.OauthRepository;
import com.sobok.apiservice.common.exception.CustomException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocialLoginService {

    private final OauthRepository oauthRepository;
    private final AuthFeignClient authFeignClient;

    @Transactional
    public OauthResDto findOrCreateKakaoUser(SocialUserDto dto) {
        // 기존 소셜 로그인 사용자
        // 소셜 ID로 기존 사용자 찾기
        Optional<Oauth> oauth = oauthRepository.findBySocialProviderAndSocialId(dto.getProvider(), dto.getSocialId());

        log.info("dto.getId(): {} oauth가 있다면: {}", dto.getSocialId(), oauth);

        // 기존 사용자 존재
        if (oauth.isPresent()) {
            log.info("기존에 소셜 로그인한 유저입니다.");
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

        // 처음 소셜 로그인 한 사람 -> 새 사용자 생성. oauth
        log.info("소셜 로그인으로 처음 방문한 신규 유저입니다. 회원가입 진행해야 됨");

        Oauth build = Oauth.builder()
                .socialProvider(dto.getProvider())
                .socialId(dto.getSocialId().toString())
                .build();

        Oauth saved = oauthRepository.save(build);

        log.info("saved: {}", saved);

        return OauthResDto.builder()
                .oauthId(saved.getId())
                .isNew(true)
                .build();
    }

    public AuthLoginResDto socialLoginToken(Long id) {  //authId
        return authFeignClient.socialToken(id);
    }

    public OauthResDto findOauth(Long oauthId) {
        Oauth oauth = oauthRepository.findById(oauthId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Oauth ID의 사용자를 찾을 수 없습니다."));

        log.info("oauth: {}", oauth);

        return OauthResDto.builder()
                .oauthId(oauth.getId())
                .socialId(oauth.getSocialId())
                .build();
    }

}
