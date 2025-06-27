package com.sobok.authservice.auth.service;

import com.sobok.authservice.auth.dto.AuthReqDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.entity.Role;
import com.sobok.authservice.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
//    private final PasswordEncoder passwordEncoder;

    public Auth userCreate(AuthReqDto authReqDto) {
        Optional<Auth> findId = authRepository.findByLoginId(authReqDto.getLoginId());

//        if (findId.isPresent()) { // 아이디 중복 체크
//            // 예외처리
//        }

        Auth userEntity = Auth.builder()
                .loginId(authReqDto.getLoginId())
//                .password(passwordEncoder.encode(authReqDto.getPassword()))
                .password(authReqDto.getPassword())
                .role(Role.valueOf(authReqDto.getRole().toUpperCase()))
                .active("Y")
                .build();

        Auth save = authRepository.save(userEntity);

        log.info("User created");

        return save;

    }

//    public void riderCreate(AuthReqDto authReqDto) {
//        Optional<Auth> findId = authRepository.findByLoginId(authReqDto.getLoginId());
//
//        Auth.builder()
//    }
}
