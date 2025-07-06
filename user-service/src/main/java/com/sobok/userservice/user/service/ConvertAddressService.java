package com.sobok.userservice.user.service;

import com.sobok.userservice.common.exception.CustomException;
import com.sobok.userservice.user.client.ApiServiceClient;
import com.sobok.userservice.user.dto.request.UserAddressReqDto;
import com.sobok.userservice.user.dto.response.KakaoLocDto;
import com.sobok.userservice.user.dto.response.UserLocationResDto;
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
