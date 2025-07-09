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

import java.util.List;

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
//            RiderInfoResDto rider = adminRiderClient.getRiderInfoByPaymentId(payment.getId());

            // 가게 정보
            Long shopId = adminRiderClient.getShopIdByPaymentId(payment.getId());
            AdminShopResDto shopInfo = adminShopFeignClient.getShopInfo(shopId);

            // 요리 이름
            List<Long> cookIds = adminPaymentClient.getCookIdsByPaymentId(payment.getId());
            List<CookNameResDto> cookName = adminCookFeignClient.getCookNames(cookIds);
            List<String> cookNameList = cookName.stream().map(CookNameResDto::getName).toList();

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
                    .cookNames(cookNameList)
                    .loginId(loginId)
                    .build();
        }).toList();
    }

}

