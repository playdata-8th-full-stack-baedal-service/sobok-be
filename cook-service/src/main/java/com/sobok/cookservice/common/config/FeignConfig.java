package com.sobok.cookservice.common.config;

import com.sobok.cookservice.common.jwt.JwtTokenProvider;
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
