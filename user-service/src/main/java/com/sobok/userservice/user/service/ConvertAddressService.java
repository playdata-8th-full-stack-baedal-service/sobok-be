package com.sobok.userservice.user.service;

import com.sobok.userservice.common.exception.CustomException;
import com.sobok.userservice.user.dto.request.UserAddressReqDto;
import com.sobok.userservice.user.dto.response.KakaoLocDto;
import com.sobok.userservice.user.dto.response.UserLocationResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConvertAddressService {
    private final RestClient restClient;

    @Value("${kakao.restKey}")
    private String kakaoRestKey;

    public UserLocationResDto getLocation(UserAddressReqDto reqDto) {
        // 요청 보내서 주소에 해당하는 위도, 경도 받기
        KakaoLocDto locData = restClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/v2/local/search/address.json")
                        .queryParam("query", reqDto.getRoadFull())
                        .build()
                )
                .header("Authorization", "KakaoAK " + kakaoRestKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new CustomException("주소 변환 과정에서 " + res.getStatusCode() + " 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new CustomException("주소 변환 과정에서 " + res.getStatusCode() + " 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .body(KakaoLocDto.class);

        // 데이터가 정상적으로 오지 않으면 예외 발생
        if (locData == null || locData.getDocuments() == null || locData.getDocuments().isEmpty()) {
            throw new CustomException("주소 변환 과정에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 첫 번째 값 추출
        KakaoLocDto.Document first = locData.getDocuments().get(0);

        // 위도, 경도 값 추출
        String x = first.getX();
        String y = first.getY();
        log.info("위도 : {}, 경도 : {}", y, x);

        return UserLocationResDto.builder()
                .latitude(Double.parseDouble(y))
                .longitude(Double.parseDouble(x))
                .build();
    }
}
