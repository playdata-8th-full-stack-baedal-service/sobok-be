package com.sobok.gatewayservice.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobok.gatewayservice.common.dto.ApiResponse;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {
    private final ObjectMapper objectMapper;

    @Value("${jwt.secretKey}")
    private String accessTokenSecretKey;

    public AuthorizationFilter(ObjectMapper objectMapper) {
        super(Config.class);
        this.objectMapper = objectMapper;
    }

    // 허용 Path 설정
    private static final List<String> whiteList = List.of(
            "/actuator",
            "/sms/send", "/sms/verify", "/auth/recover/**", "/auth/login", "/auth/reissue",
            "/auth/user-signup", "/auth/rider-signup", "/auth/shop-signup",
            "/auth/findLoginId", "/auth/verification", "/auth/reset-password", "/user/findByPhoneNumber",
            "/ingredient/keyword-search",
            "/auth/temp-token", "/auth/check-id", "/auth/check-nickname", "/auth/check-email", "/auth/check-permission",
            "/auth/check-shopName", "/auth/check-shopAddress"
            , "/cook/get-cook", "/cook/get-cook-category", "/cook/search-cook"
            , "/api/confirm",
            "/api/kakao-login"
    );

    /**
     * Token 유효성 검증 필터
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String path = exchange.getRequest().getURI().getPath();
            log.info("요청 path: {}", path); // 추가
            AntPathMatcher antPathMatcher = new AntPathMatcher();

            // 허용 url 리스트를 순회하면서 지금 들어온 요청 url과 하나라도 일치하면 true 리턴
            boolean isAllowed
                    = whiteList.stream()
                    .anyMatch(url -> antPathMatcher.match(url, path));
            log.info("isAllowed:{}", isAllowed);

            if (isAllowed || path.startsWith("/actuator")) {
                log.info("gateway filter 통과!");
                return chain.filter(exchange);
            }

            // Authorization 헤더에서 첫번째 값 꺼내기 -> null-safe
            String raw = request.getHeaders().getFirst("Authorization");

            // Authorization 헤더가 없거나 비어있다면?
            if (raw == null || raw.isEmpty()) {
                log.warn("Authorization 헤더가 없거나 비어있습니다.");
                return onError(response);
            }

            // Bearer 토큰이 아니라면
            if (!raw.startsWith("Bearer ")) {
                log.warn("Bearer 토큰이 아닙니다.");
                return onError(response);
            }

            // Bearer 떼기
            String token = raw.substring(7);

            // 토큰이 유효하지 않다면
            if (!validateToken(token)) {
                log.warn("토큰이 유효하지 않습니다.");
                return onError(response);
            }

            // Access Token이 유효하다면 헤더에 넣어준 뒤 다시 진행
            ServerHttpRequest modifiedReq = request.mutate()
                    .header("Authorization", "Bearer " + token)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedReq).build());
        };
    }

    /**
     * 토큰 유효성 검
     *
     * @param token
     * @return
     */
    private boolean validateToken(String token) {
        try {
            // 만료 기간 가져오기
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(accessTokenSecretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            // 토큰 유효하다면 통과
            return expiration.after(new Date());
        } catch (Exception e) {
            log.error("토큰 파싱 과정에서 오류가 발생했습니다.");
            return false;
        }
    }

    /**
     * Error 발셍 시 Common-Utils의 Error 응답 구조로 변환
     */
    private Mono<Void> onError(ServerHttpResponse response) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        // 에러 코드 설정
        response.setStatusCode(status);

        // 응답 유형 설정
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // String -> Json 변환 및 비동기 통신용 응답으로 변환
        try {
            String body = objectMapper.writeValueAsString(ApiResponse.fail(666, "토큰이 없습니다. 다시 발급해주세요."));
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return response.setComplete();
        }
    }


    public static class Config {
        // yml 설정값 넣고 싶으면 여기에 정의
    }
}


