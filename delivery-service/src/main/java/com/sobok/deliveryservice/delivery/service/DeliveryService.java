package com.sobok.deliveryservice.delivery.service;

import com.sobok.deliveryservice.common.exception.CustomException;
import com.sobok.deliveryservice.delivery.client.AuthFeignClient;
import com.sobok.deliveryservice.delivery.dto.info.AuthRiderInfoResDto;
import com.sobok.deliveryservice.delivery.dto.payment.DeliveryRegisterDto;
import com.sobok.deliveryservice.delivery.dto.payment.RiderPaymentInfoResDto;
import com.sobok.deliveryservice.delivery.dto.request.RiderReqDto;
import com.sobok.deliveryservice.delivery.dto.response.ByPhoneResDto;
import com.sobok.deliveryservice.delivery.dto.response.DeliveryResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderInfoResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderResDto;
import com.sobok.deliveryservice.delivery.entity.Delivery;
import com.sobok.deliveryservice.delivery.entity.Rider;
import com.sobok.deliveryservice.delivery.repository.DeliveryRepository;
import com.sobok.deliveryservice.delivery.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DeliveryService {

    private final RiderRepository riderRepository;
    private final DeliveryRepository deliveryRepository;
    private final AuthFeignClient authFeignClient;

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

    public void registerDelivery(DeliveryRegisterDto reqDto) {
        // 배달 객체 생성
        Delivery delivery = Delivery.builder()
                .shopId(reqDto.getShopId())
                .paymentId(reqDto.getPaymentId())
                .build();

        deliveryRepository.save(delivery);
    }

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
                    RiderInfoResDto authInfo = authFeignClient.getRiderAuthInfo(rider.getAuthId());

                    return RiderInfoResDto.builder()
                            .id(rider.getId())
                            .loginId(authInfo.getLoginId())
                            .name(rider.getName())
                            .phone(rider.getPhone())
                            .permissionNumber(rider.getPermissionNumber())
                            .active(authInfo.getActive())
                            .build();
                })
                .toList();
    }

    /**
     * 라이더 정보 조회 (전체 주문 조회용)
     */
    public RiderPaymentInfoResDto getRiderInfoByPaymentId(Long paymentId) {
        Delivery delivery = deliveryRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new CustomException("배달 정보 없음", HttpStatus.NOT_FOUND));

        Rider rider = riderRepository.findById(delivery.getRiderId())
                .orElseThrow(() -> new CustomException("라이더 정보 없음", HttpStatus.NOT_FOUND));

        return RiderPaymentInfoResDto.builder()
                .riderId(rider.getId())
                .riderName(rider.getName())
                .riderPhone(rider.getPhone())
                .build();
    }

    /**
     * 라이더 면허번호 중복 체크
     */
    public void checkPermission(String permission) {
        if (riderRepository.existsByPermissionNumber(permission)) {
            throw new CustomException("사용할 수 없는 면허번호 입니다.", HttpStatus.BAD_REQUEST);
        }
    }


    public DeliveryResDto getDelivery(Long paymentId) {
        Optional<Delivery> delivery = deliveryRepository.findByPaymentId(paymentId);
        return DeliveryResDto.builder()
                .shopId(delivery.get().getShopId())
                .completeTime(delivery.get().getCompleteTime())
                .riderId(delivery.get().getRiderId())
                .build();
    }

    public List<Long> getPaymentId(Long shopId) {
        return deliveryRepository.findByShopId(shopId).stream()
        .map(Delivery::getPaymentId)
        .filter(Objects::nonNull)
        .distinct()
        .toList();

    }
}
