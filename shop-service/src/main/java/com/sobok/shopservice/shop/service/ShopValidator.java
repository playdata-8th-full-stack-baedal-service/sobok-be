package com.sobok.shopservice.shop.service;

import com.sobok.shopservice.common.dto.TokenUserInfo;
import com.sobok.shopservice.common.exception.CustomException;
import com.sobok.shopservice.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopValidator {
    private final ShopRepository shopRepository;

    public boolean shopExists(Long shopId) {
        return shopRepository.existsById(shopId);
    }

    public boolean isValid(TokenUserInfo userInfo, Long shopId) {
        return userInfo.getShopId() != null &&
                shopId.equals(userInfo.getShopId()) &&
                shopExists(shopId);
    }

    public void shopCheck(TokenUserInfo userInfo, Long shopId) {
        if (!isValid(userInfo, shopId)) {
            throw new CustomException("정상적인 접근이 아닙니다.", HttpStatus.FORBIDDEN);
        }
    }
}
