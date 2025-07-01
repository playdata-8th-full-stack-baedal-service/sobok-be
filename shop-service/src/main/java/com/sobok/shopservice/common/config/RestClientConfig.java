package com.sobok.shopservice.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private final RestClient.Builder restClientBuilder;

    @Bean
    public RestClient kakaoRestClient() {
        // 카카오 API 호출을 위한 REST Client 객체 생성
        return restClientBuilder
                .baseUrl("https://dapi.kakao.com")
                .build();
    }
}
