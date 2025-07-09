package com.sobok.adminservice.admin.service;

import com.sobok.adminservice.admin.client.*;
import com.sobok.adminservice.admin.dto.order.*;
import com.sobok.adminservice.admin.dto.rider.RiderResDto;
import com.sobok.adminservice.admin.dto.shop.ShopResDto;
import com.sobok.adminservice.common.dto.ApiResponse;
import com.sobok.adminservice.common.dto.TokenUserInfo;
import com.sobok.adminservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final AdminShopFeignClient adminShopClient;
    private final AdminRiderFeignClient adminRiderClient;
    private final AdminPaymentFeignClient adminPaymentClient;
    private final UserFeignClient userFeignClient;
    private final AdminShopFeignClient adminShopFeignClient;
    private final AdminCookFeignClient adminCookFeignClient;
    private final AdminFeignClient adminFeignClient;


    /**
     * 관리자 전용 가게 전체 조회
     */
    public List<ShopResDto> getAllShops(TokenUserInfo userInfo) {
        if (!userInfo.getRole().equals("ADMIN")) {
            throw new CustomException("접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        ApiResponse<List<ShopResDto>> response = adminShopClient.getAllShops();
        return response.getData();
    }

    /**
     * 관리자 전용 라이더 전체 조회
     */
    public List<RiderResDto> getAllRiders(TokenUserInfo userInfo) {
        if (!userInfo.getRole().equals("ADMIN")) {
            throw new CustomException("접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        ApiResponse<List<RiderResDto>> response = adminRiderClient.getAllRiders();
        return response.getData();
    }

    /**
     * 관리자 전용 사용자 주문 전체 조회
     */
    public List<AdminPaymentResponseDto> getAllPayments(TokenUserInfo userInfo) {
        if (!userInfo.getRole().equals("ADMIN")) {
            throw new CustomException("접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        List<AdminPaymentResDto> payments = adminPaymentClient.getAllPayments().getData();

        return payments.stream().map(payment -> {
            // 유저 정보
            UserInfoResDto userInfoResDto = userFeignClient.getUserInfo(payment.getUserAddressId());
            // 라이더 정보
            RiderPaymentInfoResDto rider = adminRiderClient.getRiderName(payment.getId());

            // 가게 정보
            Long shopId = adminRiderClient.getShopIdByPaymentId(payment.getId());
            AdminShopResDto shopInfo = adminShopFeignClient.getShopInfo(shopId);

            // 요리 + 기본식재료 + 추가식재료
            List<CookDetailWithIngredientsResDto> cooks = getCookDetailsByPaymentId(payment.getId());

            // 사용자 정보
            Long userId = userFeignClient.getUserIdByUserAddressId(payment.getUserAddressId());
            Long authId = userFeignClient.getAuthIdByUserId(userId);
            String loginId = adminFeignClient.getLoginId(authId);

            return AdminPaymentResponseDto.builder()
                    .orderId(payment.getOrderId())
                    .totalPrice(payment.getTotalPrice())
                    .payMethod(payment.getPayMethod())
                    .orderState(payment.getOrderState())
                    .createdAt(payment.getCreatedAt())
                    .nickname(userInfoResDto.getNickname())
                    .phone(userInfoResDto.getPhone())
                    .roadFull(userInfoResDto.getRoadFull())
                    .address(userInfoResDto.getAddress())
                    .riderName(rider.getRiderName())
                    .riderPhone(rider.getRiderPhone())
                    .shopName(shopInfo.getShopName())
                    .shopPhone(shopInfo.getShopPhone())
                    .ownerName(shopInfo.getOwnerName())
                    .shopAddress(shopInfo.getShopAddress())
                    .cooks(cooks)
                    .loginId(loginId)
                    .build();
        }).toList();
    }

    /**
     * 결제 ID에 해당하는 장바구니 요리이름 재료 정보를 조회,
     * 요리 이름과 기본/추가 식재료 목록을 포함한 상세 DTO 리스트를 반환
     *
     * @param paymentId 결제 ID
     * @return List<CookDetailWithIngredientsResDto> 요리 이름, 기본 재료, 추가 재료 포함된 DTO 목록
     */
    public List<CookDetailWithIngredientsResDto> getCookDetailsByPaymentId(Long paymentId) {
        // 결제 ID에 해당하는 모든 장바구니 요리 목록 조회
        List<CartCookResDto> cartCooks = adminPaymentClient.getCartCooks(paymentId);

        // 요리 ID 목록만 추출
        List<Long> cookIds = cartCooks.stream()
                .map(CartCookResDto::getCookId)
                .distinct()
                .toList();

        // 요리 ID -> 요리 이름 Map으로 변환 (cookId 기준)
        Map<Long, String> cookNameMap = adminCookFeignClient.getCookNames(cookIds).stream()
                .collect(Collectors.toMap(CookNameResDto::getCookId, CookNameResDto::getCookName));

        List<CookDetailWithIngredientsResDto> result = new ArrayList<>();

        // 각 장바구니 요리별로 재료 조회 및 가공
        for (CartCookResDto cartCook : cartCooks) {
            // 해당 장바구니 요리에 포함된 재료 목록 조회
            List<CartIngredientResDto> ingredients = adminPaymentClient.getCartIngredients(cartCook.getId());

            // 기본 재료 ID만 필터링
            List<Long> baseIds = ingredients.stream()
                    .filter(i -> "Y".equals(i.getDefaultIngre()))
                    .map(CartIngredientResDto::getIngreId)
                    .toList();
            // 추가 재료 ID만 필터링
            List<Long> addIds = ingredients.stream()
                    .filter(i -> "N".equals(i.getDefaultIngre()))
                    .map(CartIngredientResDto::getIngreId)
                    .toList();
            //기본 재료 ID 이름 변환
            List<String> baseNames = adminCookFeignClient.getIngredientNames(baseIds).stream()
                    .map(IngredientNameResDto::getIngreName)
                    .toList();
            // 추가 재료 ID 이름 변환
            List<String> addNames = adminCookFeignClient.getIngredientNames(addIds).stream()
                    .map(IngredientNameResDto::getIngreName)
                    .toList();
            // 요리 이름 조회
            String cookName = cookNameMap.get(cartCook.getCookId());

            result.add(CookDetailWithIngredientsResDto.builder()
                    .cookName(cookName)
                    .baseIngredients(baseNames)
                    .additionalIngredients(addNames)
                    .build());
        }

        return result;
    }


}

