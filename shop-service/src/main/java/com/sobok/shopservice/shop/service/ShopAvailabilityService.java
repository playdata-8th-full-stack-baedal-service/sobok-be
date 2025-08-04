package com.sobok.shopservice.shop.service;

import com.sobok.shopservice.common.exception.CustomException;
import com.sobok.shopservice.shop.client.CookFeignClient;
import com.sobok.shopservice.shop.client.UserFeignClient;
import com.sobok.shopservice.shop.dto.payment.LocationResDto;
import com.sobok.shopservice.shop.dto.response.DeliveryAvailShopResDto;
import com.sobok.shopservice.shop.dto.stock.*;
import com.sobok.shopservice.shop.entity.Shop;
import com.sobok.shopservice.shop.repository.ShopRepository;
import com.sobok.shopservice.shop.repository.StockQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopAvailabilityService {
    private final UserFeignClient userFeignClient;
    private final ShopAssignService shopAssignService;
    private final StockQueryRepository stockQueryRepository;
    private final ShopRepository shopRepository;
    private final CookFeignClient cookFeignClient;

    public List<AvailableShopInfoDto> getAvailableShopList(Long addressId, IngredientIdListDto reqDto) {
        // 1. 입력 검증 - 재료가 없으면 빈 리스트 반환 (원래 로직 유지)
        if (isEmptyIngredientRequest(reqDto)) {
            return new ArrayList<>();
        }

        // 2. 사용자 위치 조회
        LocationResDto userLocation = getUserLocation(addressId);

        // 3. 근처 가게 조회
        List<DeliveryAvailShopResDto> nearShops = findNearbyShops(userLocation);

        // 4. 재고 기반 필터링된 가게 조회
        Map<Long, Integer> userIngreIdList = CartIngredientStock.convertIngreIdList(reqDto);
        log.info("userIngreIdList: {}", userIngreIdList);
        List<ShopStockResult> shopStockResultList = filterShopsByStock(nearShops, userIngreIdList);
        log.info("shopStockResultList: {}", shopStockResultList);

        // 5. 응답 생성
        return buildAvailableShopResponse(nearShops, shopStockResultList, userIngreIdList);
    }

    /**
     * 재료 리스트가 비어있는지 확인 (원래 로직 유지)
     */
    private boolean isEmptyIngredientRequest(IngredientIdListDto reqDto) {
        return reqDto.getCartIngredientStockList() == null || reqDto.getCartIngredientStockList().isEmpty();
    }

    /**
     * 사용자 위치 정보 조회
     */
    private LocationResDto getUserLocation(Long addressId) {
        try {
            return userFeignClient.getUserAddress(addressId).getBody();
        } catch (Exception e) {
            log.error("사용자 주소 조회 중 오류 발생: addressId={}", addressId, e);
            throw new CustomException("사용자 주소 조회에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 근처 가게 목록 조회
     */
    private List<DeliveryAvailShopResDto> findNearbyShops(LocationResDto userLocation) {
        try {
            List<DeliveryAvailShopResDto> nearShops = shopAssignService.findNearShop(
                    userLocation.getLatitude(),
                    userLocation.getLongitude(),
                    10.0
            );

            if (nearShops == null || nearShops.isEmpty()) {
                throw new CustomException("선택한 주소지에 가까운 가게를 찾지 못했습니다.", HttpStatus.NOT_FOUND);
            }

            return nearShops;
        } catch (CustomException e) {
            throw e; // CustomException은 그대로 전파
        } catch (Exception e) {
            log.error("근처 가게 탐색 중 오류 발생", e);
            throw new CustomException("가게 탐색 과정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 재고 기반으로 가게 필터링
     */
    private List<ShopStockResult> filterShopsByStock(
            List<DeliveryAvailShopResDto> nearShops,
            Map<Long, Integer> userIngreIdList) {

        List<Long> nearShopIdList = DeliveryAvailShopResDto.convertShopIdList(nearShops);
        Map<Long, Map<Long, Integer>> currentStockList = stockQueryRepository.getStockByIngreIdList(
                nearShopIdList,
                userIngreIdList.keySet()
        );

        List<ShopStockResult> shopStockResultList = new ArrayList<>();

        log.info("currentStockList: {}", currentStockList);
        if (currentStockList.isEmpty()) {
            // 모든 가게에 대해 0개 재고 맵 세팅
            for (Long shopId : nearShopIdList) {
                Map<Long, Integer> emptyStock = userIngreIdList.keySet()
                        .stream()
                        .collect(Collectors.toMap(ingreId -> ingreId, ingreId -> 0));
                currentStockList.put(shopId, emptyStock);
            }
        }

        for (Long shopId : currentStockList.keySet()) {
            Map<Long, Integer> shopStock = currentStockList.getOrDefault(shopId, new HashMap<>());

            List<MissingIngredientDto> missing = hasEnoughStock(shopStock, userIngreIdList);

            log.info("missing: {}", missing);

            ShopStockResult shopResult = new ShopStockResult();
            shopResult.setShopId(shopId);
            shopResult.setStockMap(shopStock);
            shopResult.setMissingIngredients(missing);
            shopResult.setSatisfiable(missing.isEmpty());

            shopStockResultList.add(shopResult);
        }

        return shopStockResultList;
    }

    /**
     * 가게의 재고가 충분한지 확인
     */
    private List<MissingIngredientDto> hasEnoughStock(Map<Long, Integer> shopStock, Map<Long, Integer> userRequirement) {
        List<MissingIngredientDto> missingList = new ArrayList<>();
        Set<Long> missingIngredientIds = new HashSet<>();

        for (Map.Entry<Long, Integer> entry : userRequirement.entrySet()) {
            Long ingredientId = entry.getKey();
            Integer required = entry.getValue();
            Integer available = shopStock.getOrDefault(ingredientId, 0);

            if (available < required) {
                missingIngredientIds.add(ingredientId);
                missingList.add(new MissingIngredientDto(ingredientId, null, available));
            }
        }

        if (missingIngredientIds.isEmpty()) return Collections.emptyList();
        Map<Long, String> names = cookFeignClient.getNames(new ArrayList<>(missingIngredientIds)).getBody();

        // DTO 리스트를 순회하며 이름 정보를 업데이트
        missingList.forEach(dto -> dto.setIngredientName(
                names.getOrDefault(dto.getIngredientId(), "알 수 없음")
        ));

        return missingList;
    }

    /**
     * 사용 가능한 가게 응답 객체 생성 (N+1 쿼리 문제 해결)
     */
    private List<AvailableShopInfoDto> buildAvailableShopResponse(
            List<DeliveryAvailShopResDto> nearShops,
            List<ShopStockResult> shopStockResultList,
            Map<Long, Integer> userIngreIdList) {

        // 한 번에 모든 가게 정보 조회 (N+1 쿼리 문제 해결)
        List<Long> availableShopIds = shopStockResultList.stream()
                .map(ShopStockResult::getShopId)
                .collect(Collectors.toList());
        Map<Long, String> shopNameMap = shopRepository.findAllById(availableShopIds)
                .stream()
                .collect(Collectors.toMap(Shop::getId, Shop::getShopName));

        Map<Long, ShopStockResult> shopStockResultMap = shopStockResultList.stream()
                .collect(Collectors.toMap(ShopStockResult::getShopId, Function.identity()));


        List<AvailableShopInfoDto> result = new ArrayList<>();

        // 근처 가게 순서 유지 (거리순)
        for (DeliveryAvailShopResDto nearShop : nearShops) {
            Long shopId = nearShop.getShopId();
            ShopStockResult shopStock = shopStockResultMap.get(shopId);

            if (shopStock == null) continue; // 재고가 부족한 가게는 제외

            String shopName = shopNameMap.get(shopId);
            if (shopName == null) {
                log.warn("가게 정보를 찾을 수 없습니다: shopId={}", shopId);
                continue;
            }

            List<CartIngredientStock> ingredientStocks = userIngreIdList.keySet()
                    .stream()
                    .map(ingredientId -> new CartIngredientStock(
                            shopId,
                            ingredientId,
                            shopStock.getStockMap().getOrDefault(ingredientId, 0)
                    ))
                    .collect(Collectors.toList());

            AvailableShopInfoDto shopInfo = new AvailableShopInfoDto(shopId, shopName, ingredientStocks,
                    shopStock.isSatisfiable(), shopStock.getMissingIngredients());
            result.add(shopInfo);
        }

        return result;
    }
}
