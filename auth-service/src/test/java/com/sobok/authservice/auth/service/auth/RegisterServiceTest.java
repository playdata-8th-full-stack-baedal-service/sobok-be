package com.sobok.authservice.auth.service.auth;

import com.sobok.authservice.auth.dto.request.AuthRiderReqDto;
import com.sobok.authservice.auth.dto.request.AuthShopReqDto;
import com.sobok.authservice.auth.dto.request.AuthUserReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
class RegisterServiceTest {

    @Autowired
    private RegisterService registerService;

    List<String> photos = List.of(
            "https://d3c5012dwkvoyc.cloudfront.net/profile/profile001.jpeg",
            "https://d3c5012dwkvoyc.cloudfront.net/profile/profile002.jpg",
            "https://d3c5012dwkvoyc.cloudfront.net/profile/profile003.jpg",
            "https://d3c5012dwkvoyc.cloudfront.net/profile/profile004.jpg",
            "https://d3c5012dwkvoyc.cloudfront.net/profile/profile005.webp",
            "https://d3c5012dwkvoyc.cloudfront.net/profile/profile006.jpeg",
            "https://d3c5012dwkvoyc.cloudfront.net/profile/profile007.jpeg",
            "https://d3c5012dwkvoyc.cloudfront.net/profile/profile008.jpeg",
            "https://d3c5012dwkvoyc.cloudfront.net/profile/profile009.jpeg",
            "https://d3c5012dwkvoyc.cloudfront.net/profile/profile010.jpeg"
    );

    @Test
    @DisplayName("registerUser")
    void registerUserInfo() {
        for (int i = 1; i <= 10; i++) {
            AuthUserReqDto dto = AuthUserReqDto.builder()
                    .loginId("testuser0" + i / 10 + i % 10)
                    .password("Password123!")
                    .nickname("소복0" + i / 10 + i % 10)
                    .phone("010000000" + i / 10 + i % 10)
                    .roadFull("서울 서초구 서초중앙로 지하 31 (서초동)")
                    .addrDetail("B1" + i / 10 + i % 10 + "호")
                    .email("testuser0" + i / 10 + i % 10 + "@test.com")
                    .photo(photos.get(i - 1))
                    .build();

            registerService.userCreate(dto);
        }
    }

    List<String> names = List.of(
      "김배달", "이배달", "박배달", "조배달", "손배달", "정배달", "장배달", "탁배달", "최배달", "강배달"
    );

    @Test
    @DisplayName("registerRider")
    void registerRider() {
        for (int i = 1; i <= 10; i++) {
            AuthRiderReqDto dto = AuthRiderReqDto.builder()
                    .loginId("testrider0" + i / 10 + i % 10)
                    .password("Password123!")
                    .name(names.get(i - 1))
                    .phone("010060001" + i / 10 + i % 10)
                    .permissionNumber("1234567890" + i / 10 + i % 10)
                    .build();

            registerService.riderCreate(dto);
        }
    }

    List<String> shopNames = List.of(
            "서초점", "남부터미널점", "효령점"
    );

    List<String> addrs = List.of(
            "서울 서초구 남부순환로 2103 (방배동)",
            "서울 서초구 서초중앙로 지하 31 (서초동)",
            "서울 서초구 효령로 93 (방배동)"
    );


    @Test
    @DisplayName("registerShop")
    void registerShop() {
        for (int i = 1; i <= 3; i++) {
            AuthShopReqDto dto = AuthShopReqDto.builder()
                    .loginId("testshop0" + i / 10 + i % 10)
                    .password("Password123!")
                    .shopName(shopNames.get(i - 1))
                    .phone("010000002" + i / 10 + i % 10)
                    .roadFull(addrs.get(i - 1))
                    .ownerName(names.get(i - 1).replace("배달", "가게"))
                    .build();

            registerService.shopCreate(dto);
        }
    }

}