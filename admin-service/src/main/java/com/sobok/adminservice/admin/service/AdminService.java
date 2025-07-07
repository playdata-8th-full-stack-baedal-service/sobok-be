package com.sobok.adminservice.admin.service;

import com.sobok.adminservice.admin.client.AdminRiderFeignClient;
import com.sobok.adminservice.admin.client.AdminShopFeignClient;
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
}
