package com.sobok.authservice.auth.service.auth;

import com.sobok.authservice.auth.dto.request.AuthUserReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegisterServiceTest {
    @Autowired
    private RegisterService registerService;


    @Test
    @DisplayName("register")
    void registerTest() {

        for (int i = 0; i < 30; i++) {
            registerService.userCreate(AuthUserReqDto.builder()
                            .loginId("testuser0" + i)
                            .nickname("소복"+i)
                            .photo("skldjfalj")
                            .roadFull()
                    .build())
        }
        // given

        // when

        // then

    }
}