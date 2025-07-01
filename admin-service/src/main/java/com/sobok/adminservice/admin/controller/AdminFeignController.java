package com.sobok.adminservice.admin.controller;


import com.sobok.adminservice.admin.client.AdminFeignClient;
import com.sobok.adminservice.common.dto.ApiResponse;
import com.sobok.adminservice.common.dto.TokenUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
// 관리자 승인 요청 컨트롤러
public class AdminFeignController {

    private final AdminFeignClient adminFeignClient;

    @PutMapping("/rider-active")
    // 데이터 응답이 null 이라 Void로 설정
    public ResponseEntity<ApiResponse<Void>> activeRider(
            @RequestParam Long authId,
            @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {

        //관리자 권한 확인
        if (!tokenUserInfo.getRole().equals("ADMIN")) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(HttpStatus.FORBIDDEN, "권한이 없습니다."));
        }

        // auth-service에 Feign 호출
        adminFeignClient.activeRider(authId);
        // 성공 응답
        return ResponseEntity.ok(ApiResponse.ok(null, "라이더 계정이 활성화되었습니다."));

    }
}
