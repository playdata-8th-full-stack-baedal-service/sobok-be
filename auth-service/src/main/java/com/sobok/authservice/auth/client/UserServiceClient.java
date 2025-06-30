package com.sobok.authservice.auth.client;

import com.sobok.authservice.auth.dto.request.UserSignupReqDto;
import com.sobok.authservice.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import com.sobok.authservice.auth.dto.response.*;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/user/findByPhoneNumber")
    ApiResponse<UserResDto> findByPhone(@RequestBody String phoneNumber);

    @PostMapping("/api/signup")
    ResponseEntity<Object> userSignup(@RequestBody UserSignupReqDto reqDto);
}
