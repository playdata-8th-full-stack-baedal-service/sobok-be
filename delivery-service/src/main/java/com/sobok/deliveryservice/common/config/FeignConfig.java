package com.sobok.deliveryservice.common.config;

import com.sobok.deliveryservice.common.jwt.JwtTokenProvider;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String token = jwtTokenProvider.generateFeignToken();
            template.header("Authorization", "Bearer " + token);
        };
    }
}
