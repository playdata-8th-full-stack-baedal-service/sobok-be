package com.sobok.shopservice.shop.service;


import com.sobok.shopservice.common.dto.TokenUserInfo;
import com.sobok.shopservice.common.exception.CustomException;
import com.sobok.shopservice.shop.client.DeliveryFeignClient;
import com.sobok.shopservice.shop.client.PaymentFeignClient;
import com.sobok.shopservice.shop.dto.info.AuthShopInfoResDto;
import com.sobok.shopservice.shop.dto.request.ShopSignupReqDto;
import com.sobok.shopservice.shop.dto.request.UserAddressReqDto;
import com.sobok.shopservice.shop.dto.response.*;
import com.sobok.shopservice.shop.entity.Shop;
import com.sobok.shopservice.shop.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final ShopRepository shopRepository;
    private final ConvertAddressService convertAddressService;
    private final DeliveryFeignClient deliveryClient;
    private final PaymentFeignClient paymentFeignClient;


    public AuthShopResDto createShop(ShopSignupReqDto shopSignupReqDto) {


        if (shopRepository.findByShopName(shopSignupReqDto.getShopName()).isPresent()) {
            throw new CustomException("이미 존재하는 상호명입니다.", HttpStatus.CONFLICT);
        }

        if (shopRepository.findByRoadFull(shopSignupReqDto.getRoadFull()).isPresent()) {
            throw new CustomException("이미 존재하는 주소입니다.", HttpStatus.CONFLICT);
        }


        // ConvertAddressService로 요청 보내서 위도, 경도 받아오기
        UserAddressReqDto reqDto = UserAddressReqDto.builder()
                .roadFull(shopSignupReqDto.getRoadFull())
                .build();

        UserLocationResDto location = convertAddressService.getLocation(reqDto);

        Shop shop = Shop.builder()
                .authId(shopSignupReqDto.getAuthId())
                .ownerName(shopSignupReqDto.getOwnerName())
                .shopName(shopSignupReqDto.getShopName())
                .phone(shopSignupReqDto.getPhone())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .roadFull(shopSignupReqDto.getRoadFull())
                .build();

        Shop saved = shopRepository.save(shop);

        log.info("shop 등록 성공: {}", saved);

        return AuthShopResDto.builder()
                .id(saved.getId())
                .shopName(saved.getShopName())
                .ownerName(saved.getOwnerName())
                .build();
    }

    public ByPhoneResDto findByPhoneNumber(String phoneNumber) {
        Optional<Shop> byPhone = shopRepository.findByPhone(phoneNumber);
        if (byPhone.isPresent()) {
            Shop shop = byPhone.get();
            log.info("전화번호로 얻어온 shop 정보: {}", byPhone.toString());
            return ByPhoneResDto.builder()
                    .id(shop.getId())
                    .authId(shop.getAuthId())
                    .phone(shop.getPhone())
                    .build();

        } else {
            log.info("해당 번호로 가입하신 정보가 없습니다.");
            return null;
        }
    }

    /**
     * 가게 정보 찾기
     */
    public AuthShopInfoResDto getInfo(Long authId) {
        Shop shop = shopRepository.findByAuthId(authId).orElseThrow(
                () -> new CustomException("해당하는 가게 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND)
        );

        return AuthShopInfoResDto.builder()
                .shopName(shop.getShopName())
                .roadFull(shop.getRoadFull())
                .phone(shop.getPhone())
                .ownerName(shop.getOwnerName())
                .loginId(null)
                .build();
    }

    public Long getShopId(Long id) {
        Shop shop = shopRepository.getShopByAuthId(id).orElseThrow(
                () -> new EntityNotFoundException("해당하는 가게 정보를 찾을 수 없습니다.")
        );

        return shop.getId();
    }


    /**
     * 관리자용 가게 전체 조회
     */
    public List<ShopResDto> getAllShops() {
        return shopRepository.findAll().stream()
                .map(shop -> ShopResDto.builder()
                        .id(shop.getId())          // 목록 번호
                        .shopName(shop.getShopName())
                        .roadFull(shop.getRoadFull())
                        .ownerName(shop.getOwnerName())
                        .phone(shop.getPhone())
                        .build())
                .toList();
    }

    /**
     * 주문 전체 조회용 가게 정보 전달
     */
    public AdminShopResDto getShopInfo(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException("해당 가게를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        return AdminShopResDto.builder()
                .shopId(shop.getId())
                .shopName(shop.getShopName())
                .shopAddress(shop.getRoadFull())
                .ownerName(shop.getOwnerName())
                .shopPhone(shop.getPhone())
                .build();
    }

    /**
     * 가게 이름 중복 체크
     */
    public void checkShopName(String shopName) {
        if (shopRepository.existsByShopName(shopName)) {
            throw new CustomException("이미 등록된 지점명 입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 가게 주소 중복 체크
     */
    public void checkShopAddress(String shopAddress) {
        if (shopRepository.existsByRoadFull(shopAddress)) {
            throw new CustomException("중복된 가게 주소 입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public List<ShopPaymentResDto> getAllOrders(TokenUserInfo userInfo, Long pageNo, Long numOfRows) {
        // delivery-service 가서 shopId로 paymentId 가져와
        // 응답: 주문 번호, 주문 시간, 주문 상태
        List<Long> paymentIdList = deliveryClient.getPaymentId(userInfo.getShopId());
        log.info("들어온 결제 아이디 목록: {}", paymentIdList);
        return paymentFeignClient.getPayment(paymentIdList);
    }
}
