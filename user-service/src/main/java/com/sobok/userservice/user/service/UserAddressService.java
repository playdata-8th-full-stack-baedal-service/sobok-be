package com.sobok.userservice.user.service;

import com.sobok.userservice.common.exception.CustomException;
import com.sobok.userservice.user.dto.request.UserAddressReqDto;
import com.sobok.userservice.user.dto.response.KakaoLocDto;
import com.sobok.userservice.user.dto.response.UserLocationResDto;
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
    private final UserAddressRepository userAddressRepository;
    private final ConvertAddressService convertAddressService;

    @Transactional
    public void addAddress(Long authId, UserAddressReqDto reqDto) throws CustomException, EntityNotFoundException {
        // 사용자가 존재하는 지 확인
        Users user = userRepository.findByAuthId(authId).orElseThrow(
                () -> new EntityNotFoundException("존재하는 사용자가 없습니다.")
        );

        // 도로명 주소를 위도 경도로 변환
        UserLocationResDto location = convertAddressService.getLocation(reqDto);

        // 사용자 주소 정보 저장
        UserAddress userAddress = UserAddress.builder()
                .userId(user.getId())
                .roadFull(reqDto.getRoadFull())
                .addrDetail(reqDto.getAddrDetail())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();

        userAddressRepository.save(userAddress);
    }

}
