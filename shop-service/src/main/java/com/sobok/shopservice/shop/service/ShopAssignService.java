package com.sobok.shopservice.shop.service;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.shopservice.common.exception.CustomException;
import com.sobok.shopservice.shop.client.DeliveryFeignClient;
import com.sobok.shopservice.shop.client.UserFeignClient;
import com.sobok.shopservice.shop.dto.payment.DeliveryRegisterDto;
import com.sobok.shopservice.shop.dto.payment.LocationResDto;
import com.sobok.shopservice.shop.dto.payment.ShopAssignDto;
import com.sobok.shopservice.shop.dto.response.DeliveryAvailShopResDto;
import com.sobok.shopservice.shop.entity.QShop;
import com.sobok.shopservice.shop.entity.Shop;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.sobok.shopservice.shop.entity.QShop.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopAssignService {
    private final UserFeignClient userFeignClient;
    private final JPAQueryFactory factory;
    private final DeliveryFeignClient deliveryFeignClient;

    public void assignNearestShop(ShopAssignDto reqDto) {
        log.info("가게 자동 배정 시작 | Request : {}", reqDto);

        Long userAddressId = reqDto.getUserAddressId();
        LocationResDto userAddrDto = userFeignClient.getUserAddress(userAddressId);

        // 최대 거리는 나중에 바꾸자
        Shop nearestShop = findNearestShop(userAddrDto.getLatitude(), userAddrDto.getLongitude(), 100.0).orElseThrow(
                () -> new CustomException("조건을 만족하는 가게가 존재하지 않습니다.", HttpStatus.NOT_FOUND)
        );

        try {
            // Delivery에 객체 생성하면서 shop id, payment id 전달
            DeliveryRegisterDto deliveryReqDto = new DeliveryRegisterDto(nearestShop.getId(), reqDto.getPaymentId());

            // 배달 객체 저장
            deliveryFeignClient.registerDelivery(deliveryReqDto);
        } catch (FeignException e) {
            log.error("배달 객체 저장 실패 | paymentId : {}", reqDto.getPaymentId(), e);
            throw new CustomException("배달 정보를 저장하는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<Shop> findNearestShop(double userLatitude, double userLongitude, double radiusKm) {
        log.info("가게 주소와 사용자 주소 간 거리 비교 시작 | 사용자 위도 : {}, 사용자 경도 : {}, 최대 거리 : {}", userLatitude, userLongitude, radiusKm);

        QShop shop = QShop.shop;

        // 1. 경계 조건 설정
        double latDiff = radiusKm / 111.0; // 위도 1도 = 약 111km
        double lngDiff = radiusKm / (111.0 * Math.cos(Math.toRadians(userLatitude))); // 위도에 따른 경도 보정

        double minLat = userLatitude - latDiff; // 위도 최소값
        double maxLat = userLatitude + latDiff; // 위도 최대값
        double minLng = userLongitude - lngDiff; // 경도 최소값
        double maxLng = userLongitude + lngDiff; // 경도 최대값

        // 2. Haversine 수식 정의 (위도 경도 값을 통한 거리 계산식)
        NumberTemplate<Double> distance = Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1}.latitude)) * cos(radians({1}.longitude) - radians({2})) + sin(radians({0})) * sin(radians({1}.latitude)))",
                userLatitude, shop, userLongitude
        );

        // 3. 쿼리 실행
        Shop nearestShop = factory
                .select(shop)
                .from(shop)
                .where(
                        shop.latitude.between(minLat, maxLat),
                        shop.longitude.between(minLng, maxLng)
                )
                .orderBy(distance.asc())
                .limit(1)
                .fetchFirst();

        return Optional.ofNullable(nearestShop);
    }


    public List<DeliveryAvailShopResDto> findNearShop(double riderLatitude, double riderLongitude, double radiusKm) {
        log.info("가게 주소와 라이더 주소 간 거리 비교 시작 | 라이더 위도 : {}, 라이더 경도 : {}, 최대 거리 : {}", riderLatitude, riderLongitude, radiusKm);

        QShop shop = QShop.shop;

        // 1. 경계 조건 설정
        double latDiff = radiusKm / 111.0; // 위도 1도 = 약 111km
        double lngDiff = radiusKm / (111.0 * Math.cos(Math.toRadians(riderLatitude))); // 위도에 따른 경도 보정

        double minLat = riderLatitude - latDiff; // 위도 최소값
        double maxLat = riderLatitude + latDiff; // 위도 최대값
        double minLng = riderLongitude - lngDiff; // 경도 최소값
        double maxLng = riderLongitude + lngDiff; // 경도 최대값

        // 2. Haversine 수식 정의 (위도 경도 값을 통한 거리 계산식)
        NumberTemplate<Double> distance = Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1}.latitude)) * cos(radians({1}.longitude) - radians({2})) + sin(radians({0})) * sin(radians({1}.latitude)))",
                riderLatitude, shop, riderLongitude
        );

        // 3. 쿼리 실행
        List<Shop> nearShop = factory
                .select(shop)
                .from(shop)
                .where(
                        shop.latitude.between(minLat, maxLat),
                        shop.longitude.between(minLng, maxLng)
                )
                .orderBy(distance.asc())
                .fetch();

        return nearShop.stream().map(shops -> DeliveryAvailShopResDto.builder()
                        .shopId(shops.getId())
                        .shopName(shops.getShopName())
                        .roadFull(shops.getRoadFull())
                        .build())
                .toList();

    }
}
