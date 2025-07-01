package com.sobok.authservice.auth.controller;

import com.sobok.authservice.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthFeignController {

    private final AuthService authService;

    /**
     * 라이더 활성화
     */
    @PutMapping("/active-rider")
    public ResponseEntity<Void> activeRider(@RequestParam Long authId) {
        authService.activeRider(authId);
        return ResponseEntity.ok().build();
    }
}
