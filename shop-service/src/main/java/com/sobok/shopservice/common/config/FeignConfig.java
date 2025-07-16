package com.sobok.shopservice.common.config;

import com.sobok.shopservice.common.exception.CustomErrorDecoder;
import com.sobok.shopservice.common.jwt.JwtTokenProvider;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
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

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 1000, 3); // 100ms ~ 1초, 최대 3번
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

}
