package com.sobok.shopservice.shop.service;

import com.sobok.shopservice.common.dto.TokenUserInfo;
import com.sobok.shopservice.common.exception.CustomException;
import com.sobok.shopservice.shop.client.UserFeignClient;
import com.sobok.shopservice.shop.dto.payment.LocationResDto;
import com.sobok.shopservice.shop.dto.response.DeliveryAvailShopResDto;
import com.sobok.shopservice.shop.dto.stock.AvailableShopInfoDto;
import com.sobok.shopservice.shop.dto.stock.CartIngredientStock;
import com.sobok.shopservice.shop.dto.stock.IngredientIdListDto;
import com.sobok.shopservice.shop.repository.ShopRepository;
import com.sobok.shopservice.shop.repository.StockQueryRepository;
import com.sobok.shopservice.shop.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {
    private final ShopService shopService;
    private final StockRepository stockRepository;
    private final UserFeignClient userFeignClient;
    private final ShopAssignService shopAssignService;
    private final StockQueryRepository stockQueryRepository;
    private final ShopRepository shopRepository;

    public List<AvailableShopInfoDto> getAvailableShopList(Long addressId, IngredientIdListDto reqDto) {
        // --- 재료가 없다면 빈 리스트 반환 ---
        if (reqDto.getCartIngredientStockList() == null || reqDto.getCartIngredientStockList().isEmpty()) {
            return new ArrayList<>();
        }

        // --- 사용자 주소 검증 ---
        List<DeliveryAvailShopResDto> nearShopList = new ArrayList<>();
        try {
            LocationResDto userAddress = userFeignClient.getUserAddress(addressId);
            nearShopList = shopAssignService.findNearShop(userAddress.getLatitude(), userAddress.getLongitude(), 10.0);
        } catch (Exception e) {
            throw new CustomException("가게 탐색 과정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // --- 가까운 가게가 없다면 예외 반환 ---
        if (nearShopList == null || nearShopList.isEmpty()) {
           throw new CustomException("선택한 주소지에 가까운 가게를 찾지 못했습니다.", HttpStatus.NOT_FOUND);
        }

        // --- 조회를 위한 Map 생성 ---
        Map<Long, Integer> userIngreIdList = CartIngredientStock.convertIngreIdList(reqDto);
        List<Long> nearShopIdList = DeliveryAvailShopResDto.convertShopIdList(nearShopList);
        Map<Long, Map<Long, Integer>> currentStockList = stockQueryRepository.getStockByIngreIdList(nearShopIdList, userIngreIdList.keySet());

        // --- 식재료 검사 ---
        Map<Long, Map<Long, Integer>> availableShopIds = new HashMap<>();
        for (Long shopId : currentStockList.keySet()) {
            // --- 현재 가게 재고 상태 객체 가져와서 있는지 확인 ---
            Map<Long, Integer> shopState = currentStockList.getOrDefault(shopId, new HashMap<>());
            if (shopState.size() == userIngreIdList.size()) {
                // --- 재료 수량 점검 ---
                boolean flag = true;
                for (Long ingreId : userIngreIdList.keySet()) {
                    Integer shopStock = shopState.getOrDefault(ingreId, -1);
                    Integer userStock = userIngreIdList.get(ingreId);

                    // --- 사용자 재고가 가게 재고보다 작아야 함 ---
                    if (shopStock < userStock) {
                        flag = false;
                        break;
                    }
                }

                // --- 재고가 모두 충분하다면 가능한 가게에 넣기 ---
                if (flag) {
                    availableShopIds.put(shopId, shopState);
                }
            }
        }

        // --- 응답 객체 생성 ---
        List<AvailableShopInfoDto> resDto = new ArrayList<>();
        for (Long shopId : nearShopIdList) {
            Map<Long, Integer> shopState = availableShopIds.getOrDefault(shopId, null);
            if (shopState == null) continue;

            List<CartIngredientStock> ingredientStocks = new ArrayList<>();
            for (Long ingreId : userIngreIdList.keySet()) {
                ingredientStocks.add(new CartIngredientStock(shopId, ingreId, shopState.get(ingreId)));
            }

            String shopName = shopRepository.findById(shopId).orElseThrow(
                    () -> new CustomException("해당하는 가게가 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
            ).getShopName();


            AvailableShopInfoDto shopInfoDto = new AvailableShopInfoDto(shopId, shopName, ingredientStocks);
            resDto.add(shopInfoDto);
        }


        return resDto;

    }
}
