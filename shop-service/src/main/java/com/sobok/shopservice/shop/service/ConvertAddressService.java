package com.sobok.shopservice.shop.service;

import com.sobok.shopservice.common.exception.CustomException;
import com.sobok.shopservice.shop.client.ApiServiceClient;
import com.sobok.shopservice.shop.dto.response.KakaoLocDto;
import com.sobok.shopservice.shop.dto.request.UserAddressReqDto;
import com.sobok.shopservice.shop.dto.response.UserLocationResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConvertAddressService {
    private final ApiServiceClient apiServiceClient;

    public UserLocationResDto getLocation(UserAddressReqDto reqDto) {
        return apiServiceClient.convertAddress(reqDto.getRoadFull());
    }
}
