package com.sobok.deliveryservice.delivery.service;

import com.sobok.deliveryservice.common.exception.CustomException;
import com.sobok.deliveryservice.delivery.client.AuthFeignClient;
import com.sobok.deliveryservice.delivery.dto.info.AuthRiderInfoResDto;
import com.sobok.deliveryservice.delivery.dto.request.RiderReqDto;
import com.sobok.deliveryservice.delivery.dto.response.ByPhoneResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderInfoResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderResDto;
import com.sobok.deliveryservice.delivery.entity.Rider;
import com.sobok.deliveryservice.delivery.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class RiderService {

    private final RiderRepository riderRepository;
    private final AuthFeignClient authFeignClient;

    /**
     * 라이더 회원 가입
     */
    public RiderResDto riderCreate(RiderReqDto dto) {

        if (riderRepository.existsByPhone(dto.getPhone())) {
            throw new CustomException("이미 사용 중인 전화번호입니다.", HttpStatus.BAD_REQUEST);
        }
        if (riderRepository.existsByPermissionNumber(dto.getPermissionNumber())) {
            throw new CustomException("이미 사용 중인 면허번호입니다.", HttpStatus.BAD_REQUEST);
        }

        Rider rider = Rider.builder()
                .authId(dto.getAuthId())
                .name(dto.getName())
                .phone(dto.getPhone())
                .permissionNumber(dto.getPermissionNumber())
                .build();

        Rider saved = riderRepository.save(rider);

        return RiderResDto.builder()
                .id(saved.getId())
                .authId(saved.getAuthId())
                .name(saved.getName())
                .phone(saved.getPhone())
                .permissionNumber(saved.getPermissionNumber())
                .build();
    }

    /**
     * 전화번호로 라이더 정보를 조회
     */
    public ByPhoneResDto findByPhoneNumber(String phoneNumber) {
        Optional<Rider> byPhone = riderRepository.findByPhone(phoneNumber);
        if (byPhone.isPresent()) {
            Rider rider = byPhone.get();
            log.info("전화번호로 얻어온 rider 정보: {}", byPhone.toString());
            return ByPhoneResDto.builder()
                    .id(rider.getId())
                    .authId(rider.getAuthId())
                    .phone(rider.getPhone())
                    .build();

        } else {
            log.info("해당 번호로 가입하신 정보가 없습니다.");
            return null;
        }
    }

    /**
     * 라이더 정보 찾기
     */
    public AuthRiderInfoResDto getInfo(Long authId) {
        Rider rider = riderRepository.getRiderByAuthId(authId).orElseThrow(
                () -> new CustomException("해당하는 라이더가 존재하지 않습니다.", HttpStatus.BAD_REQUEST)
        );

        return AuthRiderInfoResDto.builder()
                .phone(rider.getPhone())
                .permissionNumber(rider.getPermissionNumber())
                .name(rider.getName())
                .loginId(null)
                .build();
    }

    /**
     * authId를 통해 라이더의 Id를 조회
     */
    public Long getRiderId(Long id) {
        Rider rider = riderRepository.getRiderByAuthId(id).orElseThrow(
                () -> new CustomException("해당하는 라이더가 존재하지 않습니다.", HttpStatus.BAD_REQUEST)
        );

        return rider.getId();
    }

    /**
     * 라이더 정보 조회
     */
    public List<RiderInfoResDto> getAllRiders() {
        return riderRepository.findAll()
                .stream()
                .map(rider -> {
                    ResponseEntity<RiderInfoResDto> authInfo = authFeignClient.getRiderAuthInfo(rider.getAuthId());

                    if (authInfo == null || authInfo.getBody() == null) {
                        throw new CustomException("라이더 정보 조회 실패", HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                    return RiderInfoResDto.builder()
                            .id(rider.getId())
                            .authId(authInfo.getBody().getAuthId())
                            .loginId(authInfo.getBody().getLoginId())
                            .name(rider.getName())
                            .phone(rider.getPhone())
                            .permissionNumber(rider.getPermissionNumber())
                            .active(authInfo.getBody().getActive())
                            .build();
                })
                .toList();
    }

    /**
     * 라이더 면허번호 중복 체크
     */
    public void checkPermission(String permission) {
        if (riderRepository.existsByPermissionNumber(permission)) {
            throw new CustomException("사용할 수 없는 면허번호 입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 승인 대기 중인 라이더 목록을 조회
     */
    public ArrayList<RiderResDto> getPendingRiders() {
        List<Long> inactiveRidersAuthIds;
        try {
            inactiveRidersAuthIds = authFeignClient.getInactiveRidersInfo().getBody();
        } catch (Exception e) {
            throw new CustomException("Feign 과정에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (Objects.requireNonNull(inactiveRidersAuthIds).isEmpty()) {
            return new ArrayList<>();
        }

        return riderRepository.findAll()
                .stream()
                .filter(rider -> inactiveRidersAuthIds.contains(rider.getAuthId()))
                .map(rider -> RiderResDto.builder()
                        .authId(rider.getAuthId())
                        .name(rider.getName())
                        .phone(rider.getPhone())
                        .permissionNumber(rider.getPermissionNumber())
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Id로 라이더를 조회
     */
    public Rider findRiderByIdOrThrow(Long riderId) {
        return riderRepository.findById(riderId)
                .orElseThrow(() -> new CustomException("라이더 정보 없음", HttpStatus.NOT_FOUND));
    }

    /**
     * 라이더 검증 중복 로직 분리
     */
    public void existsByIdOrThrow(Long riderId) {
        if (!riderRepository.existsById(riderId)) {
            throw new CustomException("해당하는 라이더가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 라이더 면허 번호 중복 검증
     */
    public boolean existsByPermissionNumber(String permission) {
        return riderRepository.existsByPermissionNumber(permission);
    }
}