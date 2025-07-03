package com.sobok.shopservice.shop.service;


import com.sobok.shopservice.common.exception.CustomException;
import com.sobok.shopservice.shop.dto.request.ShopSignupReqDto;
import com.sobok.shopservice.shop.dto.request.UserAddressReqDto;
import com.sobok.shopservice.shop.dto.response.AuthShopResDto;
import com.sobok.shopservice.shop.dto.response.ByPhoneResDto;
import com.sobok.shopservice.shop.dto.response.UserLocationResDto;
import com.sobok.shopservice.shop.entity.Shop;
import com.sobok.shopservice.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final ShopRepository shopRepository;
    private final ConvertAddressService convertAddressService;


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
}
