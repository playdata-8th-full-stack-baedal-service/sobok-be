package com.sobok.userservice.user.service;

import com.sobok.userservice.common.exception.CustomException;
import com.sobok.userservice.user.dto.request.UserAddressReqDto;
import com.sobok.userservice.user.dto.response.KakaoLocDto;
import com.sobok.userservice.user.entity.UserAddress;
import com.sobok.userservice.user.entity.Users;
import com.sobok.userservice.user.repository.UserAddressRepository;
import com.sobok.userservice.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAddressService {
    private final UserRepository userRepository;
    private final RestClient restClient;

    private final UserAddressRepository userAddressRepository;

    @Value("${kakao.restKey}")
    private String kakaoRestKey;

    @Transactional
    public void addAddress(Long authId, UserAddressReqDto reqDto) throws CustomException, EntityNotFoundException {
        // 사용자가 존재하는 지 확인
        Users user = userRepository.findByAuthId(authId).orElseThrow(
                () -> new EntityNotFoundException("존재하는 사용자가 없습니다.")
        );

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

        // 주소 저장 로직
        try {
            // 위도, 경도 값 추출
            String x = locData.getDocuments().get(0).getX();
            String y = locData.getDocuments().get(0).getY();

            // 사용자 주소 정보 저장
            UserAddress userAddress = UserAddress.builder()
                    .userId(user.getId())
                    .roadFull(reqDto.getRoadFull())
                    .addrDetail(reqDto.getAddrDetail())
                    .latitude(Double.parseDouble(y))
                    .longitude(Double.parseDouble(x))
                    .build();

            userAddressRepository.save(userAddress);
        } catch (Exception e) {
            log.error("잘못된 주소값이 입력된 것 같습니다.");
            throw new CustomException("입력된 주소를 좌표로 변환하는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
