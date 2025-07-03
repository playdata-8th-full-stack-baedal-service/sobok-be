package com.sobok.userservice.user.controller;


import com.sobok.userservice.common.dto.ApiResponse;
import com.sobok.userservice.user.dto.info.UserAddressDto;
import com.sobok.userservice.user.dto.response.UserResDto;
import com.sobok.userservice.user.service.UserService;
import com.sobok.userservice.common.dto.TokenUserInfo;
import com.sobok.userservice.user.dto.request.UserAddressEditReqDto;
import com.sobok.userservice.user.dto.request.UserAddressReqDto;
import com.sobok.userservice.user.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAddressService userAddressService;


    @PostMapping("/addAddress")
    public ResponseEntity<?> addAddress(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody UserAddressReqDto reqDto) {
        userAddressService.addAddress(userInfo.getId(), reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(userInfo.getId(), "성공적으로 주소가 저장되었습니다."));
    }

    @PatchMapping("/editAddress")
    public ResponseEntity<?> editAddress(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody UserAddressEditReqDto reqDto) {
        userAddressService.editAddress(userInfo.getId(), reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(userInfo.getId(), "성공적으로 주소가 변경되었습니다."));
    }

    @GetMapping("/getAddress")
    public ResponseEntity<?> getAddress(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<UserAddressDto> address = userAddressService.getAddress(userInfo.getId());
        return ResponseEntity.ok().body(ApiResponse.ok(address, "사용자의 주소를 성공적으로 조회하였습니다."));
    }

    // TODO : 이메일, 사진 추가 변경 가능해야 함.


}
