package com.sobok.authservice.auth.controller;

import com.sobok.authservice.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private UserService userService;

    @PostMapping("/user-signup")
    public ResponseEntity<?> createUser() {
        userService.userCreate();
        return ResponseEntity.ok().build();
    }



}
