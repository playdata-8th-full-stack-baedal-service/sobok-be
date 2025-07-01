package com.sobok.paymentservice.common.jwt;

import com.sobok.paymentservice.common.enums.Role;
import com.sobok.paymentservice.common.exception.CustomException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.feignSecretKey}")
    private String secretKey;
    @Value("${jwt.feignExpiration}")
    private Long expiration;

    private static final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN:";

    /**
     * Feign Access Token 발급 (예외가 발생했다면 빈 문자열)
     * @return
     */
    public String generateFeignToken() throws CustomException {
        log.info("Feign Token 생성");
        try {
            // 현재 시간
            Date now = new Date();
            // ? 분 * (60 초 / 1 분) * (1000 ms / 1 초)
            Date expiryDate = new Date(now.getTime() + expiration * 60 * 1000);

            return Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))
                    .setExpiration(expiryDate)
                    .setIssuedAt(now)
                    .setSubject("999")
                    .claim("role", Role.FEIGN.toString())
                    .compact();
        } catch (InvalidKeyException e) {
            log.error("액세스 토큰 생성 중 문제가 발생했습니다.");
            throw new CustomException("액세스 토큰 생성 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
