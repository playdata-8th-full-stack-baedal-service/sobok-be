package com.sobok.userservice.user.service;

import com.sobok.userservice.common.dto.TokenUserInfo;
import com.sobok.userservice.common.exception.CustomException;
import com.sobok.userservice.user.dto.info.UserAddressDto;
import com.sobok.userservice.user.dto.request.UserAddressEditReqDto;
import com.sobok.userservice.user.dto.request.UserAddressReqDto;
import com.sobok.userservice.user.dto.response.UserLocationResDto;
import com.sobok.userservice.user.entity.User;
import com.sobok.userservice.user.entity.UserAddress;
import com.sobok.userservice.user.repository.UserAddressRepository;
import com.sobok.userservice.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAddressService {
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final ConvertAddressService convertAddressService;

    /**
     * <pre>
     *     # 주소 추가
     *     1. 사용자 확인
     *     2. 도로명 주소를 위도, 경도로 변환
     *     3. 사용자 주소 정보 저장
     * </pre>
     */
    @Transactional
    public void addAddress(Long authId, UserAddressReqDto reqDto) throws CustomException, EntityNotFoundException {
        // 사용자가 존재하는 지 확인
        User user = userRepository.findByAuthId(authId).orElseThrow(
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

    /**
     * <pre>
     *     # 주소 편집
     *     1. 사용자 확인
     *     2. 사용자 주소 확인
     *     3. 상세주소만 변경되었다면 상세주소만 변경
     *     4. 도로명주소까지 변경되었다면 다시 계산
     *     5. DB 저장
     * </pre>
     */
    public void editAddress(Long authId, UserAddressEditReqDto reqDto) {
        // 사용자가 존재하는 지 확인
        User user = userRepository.findByAuthId(authId).orElseThrow(
                () -> new EntityNotFoundException("존재하는 사용자가 없습니다.")
        );

        // 기존의 입력한 주소 가져오기
        UserAddress userAddress = userAddressRepository.findById(reqDto.getAddressId()).orElseThrow(
                () -> new EntityNotFoundException("해당하는 사용자 주소가 없습니다.")
        );

        // 상세 주소만 달라졌다면
        if (userAddress.getRoadFull().equals(reqDto.getRoadFull())) {
            log.info("상세주소만 변경되었습니다.");
            userAddress.editDetail(reqDto.getAddrDetail());
        } else {
            log.info("도로명 주소가 변경되어 좌표 계산을 다시 실행합니다.");

            // 요청 DTO 가공
            UserAddressReqDto address = new UserAddressReqDto(reqDto.getRoadFull(), reqDto.getAddrDetail());

            // 도로명 주소를 위도 경도로 변환
            UserLocationResDto location = convertAddressService.getLocation(address);

            // 주소 변경
            userAddress.editAddress(address, location);
        }

        userAddressRepository.save(userAddress);
        log.info("주소 변경이 완료되었습니다.");
    }

    public List<UserAddressDto> getAddress(Long id) {
        User user = userRepository.findByAuthId(id).orElseThrow(
                () -> new CustomException("해당하는 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND)
        );

        return userAddressRepository.findByUserId(user.getId())
                .stream()
                .map(addr -> new UserAddressDto(addr.getId(), addr.getRoadFull(), addr.getAddrDetail()))
                .collect(Collectors.toList());
    }

    public UserLocationResDto getUserAddress(Long userAddressId) {
        UserAddress userAddress = userAddressRepository.findById(userAddressId).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 주소입니다.")
        );

        return UserLocationResDto.builder()
                .longitude(userAddress.getLongitude())
                .latitude(userAddress.getLatitude())
                .build();
    }

    @Transactional
    public Long deleteAddress(TokenUserInfo userInfo, Long id) {
        UserAddress userAddress = userAddressRepository.findById(id).orElseThrow(
                () -> new CustomException("존재하지 않는 주소입니다.", HttpStatus.NOT_FOUND)
        );

        if(!userAddress.getUserId().equals(userInfo.getUserId())) {
            throw new CustomException("잘못된 사용자 요청입니다.", HttpStatus.FORBIDDEN);
        }

        String active = userAddress.getActive();
        if (active == null || "Y".equals(active)) {
            throw new CustomException("이미 비활성화된 주소입니다.", HttpStatus.BAD_REQUEST);
        } else {
            userAddress.convertActive(false);
        }

        return id;
    }
}
