package com.sobok.userservice.user.controller;


import com.sobok.userservice.common.dto.TokenUserInfo;
import com.sobok.userservice.user.dto.request.UserAddressReqDto;
import com.sobok.userservice.user.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserAddressService userAddressService;

    @PostMapping("/addAddress")
    public ResponseEntity<?> addAddress(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody UserAddressReqDto reqDto) {
        userAddressService.addAddress(userInfo.getId(), reqDto);
        return ResponseEntity.ok().build();
    }

}
