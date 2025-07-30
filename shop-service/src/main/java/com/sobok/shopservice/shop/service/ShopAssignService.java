package com.sobok.shopservice.shop.service;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.sobok.shopservice.common.exception.CustomException;
import com.sobok.shopservice.shop.client.DeliveryFeignClient;
import com.sobok.shopservice.shop.client.UserFeignClient;
import com.sobok.shopservice.shop.dto.payment.DeliveryRegisterDto;
import com.sobok.shopservice.shop.dto.payment.LocationResDto;
import com.sobok.shopservice.shop.dto.payment.ShopAssignDto;
import com.sobok.shopservice.shop.dto.response.DeliveryAvailShopResDto;
import com.sobok.shopservice.shop.dto.stock.StockResDto;
import com.sobok.shopservice.shop.entity.QShop;
import com.sobok.shopservice.shop.entity.Shop;
import com.sobok.shopservice.shop.entity.Stock;
import com.sobok.shopservice.shop.repository.ShopQueryRepository;
import com.sobok.shopservice.shop.repository.StockRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.sobok.shopservice.shop.entity.QShop.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopAssignService {
    private final UserFeignClient userFeignClient;
    private final StockService stockService;
    private final DeliveryFeignClient deliveryFeignClient;
    private final ShopQueryRepository shopQueryRepository;


    public void assignNearestShop(ShopAssignDto reqDto) {
        log.info("가게 자동 배정 시작 | Request : {}", reqDto);

        Long userAddressId = reqDto.getUserAddressId();
        LocationResDto userAddrDto = userFeignClient.getUserAddress(userAddressId).getBody();

        // 최대 거리는 나중에 바꾸자
        List<DeliveryAvailShopResDto> nearShopList = findNearShop(userAddrDto.getLatitude(), userAddrDto.getLongitude(), 10.0);

        DeliveryAvailShopResDto nearestShop = getDeliveryAvailableShop(reqDto, nearShopList);

        try {
            // Delivery에 객체 생성하면서 shop id, payment id 전달
            DeliveryRegisterDto deliveryReqDto = new DeliveryRegisterDto(nearestShop.getShopId(), reqDto.getPaymentId());

            // 배달 객체 저장
            deliveryFeignClient.registerDelivery(deliveryReqDto);
        } catch (FeignException e) {
            log.error("배달 객체 저장 실패 | paymentId : {}", reqDto.getPaymentId(), e);
            throw new CustomException("배달 정보를 저장하는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public DeliveryAvailShopResDto getDeliveryAvailableShop(ShopAssignDto reqDto, List<DeliveryAvailShopResDto> nearShopList) {
        try {
            DeliveryAvailShopResDto nearestShop = null;
            for (int i = 0; i < nearShopList.size(); i++) {
                DeliveryAvailShopResDto shopInfo = nearShopList.get(i);
                Map<Long, Integer> stock = stockService.getStock(shopInfo.getShopId())
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        StockResDto::getIngredientId,
                                        StockResDto::getQuantity
                                )
                        );

                boolean flag = true;
                Map<Long, Integer> request = reqDto.getCartIngreIdList();
                for (Long ingreId : request.keySet()) {
                    if (stock.containsKey(ingreId) && request.get(ingreId) <= stock.get(ingreId)) {
                    } else {
                        flag = false;
                        break;
                    }
                }

                if (flag) {
                    nearestShop = shopInfo;
                    break;
                }
            }

            if (nearestShop == null) {
                throw new CustomException("선택한 주소지에 가까운 가게를 찾지 못했습니다.", HttpStatus.NOT_FOUND);
            } else {
                stockService.updateStock(reqDto, nearestShop);
            }
            return nearestShop;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("가게 자동 배정 중 오류 발생", e);
            throw new CustomException("가게 자동 배정 과정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
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
        Shop nearestShop = shopQueryRepository.getNearestShop(shop, minLat, maxLat, minLng, maxLng, distance);

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
        List<Shop> nearShop = shopQueryRepository.getShopsByDistance(shop, minLat, maxLat, minLng, maxLng, distance);

        return nearShop.stream().map(shops -> DeliveryAvailShopResDto.builder()
                        .shopId(shops.getId())
                        .shopName(shops.getShopName())
                        .roadFull(shops.getRoadFull())
                        .build())
                .toList();

    }
}
