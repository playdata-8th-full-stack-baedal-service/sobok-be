package com.sobok.userservice.common.config;

import com.sobok.userservice.common.jwt.JwtTokenProvider;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.form.spring.SpringFormEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

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
    public SpringFormEncoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters()));
    }

    private ObjectFactory<HttpMessageConverters> messageConverters() {
        return () -> new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 1000, 3); // 100ms ~ 1초, 최대 3번
    }
}
