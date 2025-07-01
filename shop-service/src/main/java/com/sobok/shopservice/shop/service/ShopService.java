package com.sobok.shopservice.shop.service;


import com.sobok.shopservice.shop.dto.request.ShopSignupReqDto;
import com.sobok.shopservice.shop.dto.request.UserAddressReqDto;
import com.sobok.shopservice.shop.dto.response.AuthShopResDto;
import com.sobok.shopservice.shop.dto.response.UserLocationResDto;
import com.sobok.shopservice.shop.entity.Shop;
import com.sobok.shopservice.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final ShopRepository shopRepository;
    private final ConvertAddressService convertAddressService;


    public AuthShopResDto createShop(ShopSignupReqDto shopSignupReqDto) {

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
}
