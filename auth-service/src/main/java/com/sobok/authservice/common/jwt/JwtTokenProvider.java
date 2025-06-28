package com.sobok.authservice.common.jwt;

import com.sobok.authservice.auth.entity.Auth;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private Long expiration;
    @Value("${jwt.refreshSecretKey}")
    private String refreshSecretKey;
    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;

    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN:";

    /**
     * Access Token 발급 (예외가 발생했다면 빈 문자열)
     * @param auth
     * @return
     */
    public String generateAccessToken(Auth auth) {
        try {
            // 현재 시간
            Date now = new Date();
            // ? 시간 * (60 분 / 1 시간) * (60 초 / 1 분) * (1000 ms / 1 초)
            Date expiryDate = new Date(now.getTime() + expiration * 60 * 60 * 1000);

            return Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))
                    .setExpiration(expiryDate)
                    .setIssuedAt(now)
                    .setSubject(auth.getId().toString())
                    .claim("role", auth.getRole().toString())
                    .compact();
        } catch (InvalidKeyException e) {
            log.error("토큰 생성 중 문제가 발생했습니다.");
            return "";
        }
    }

    /**
     * Refresh Token 발급 (예외가 발생했다면 빈 문자열)
     * @param auth
     * @return
     */
    public String generateRefreshToken(Auth auth) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + refreshExpiration * 60 * 60 * 1000);

            String refreshToken = Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, refreshSecretKey.getBytes(StandardCharsets.UTF_8))
                    .setExpiration(expiryDate)
                    .setIssuedAt(now)
                    .setSubject(auth.getId().toString())
                    .claim("role", auth.getRole().toString())
                    .compact();

            // refresh 토큰 발급 시 redis에 저장
            redisTemplate.opsForValue().set(REFRESH_TOKEN_KEY + auth.getId().toString(), refreshToken, refreshExpiration * 60 * 60 * 1000);
            return refreshToken;
        } catch (InvalidKeyException e) {
            log.error("토큰 생성 중 문제가 발생했습니다.");
            return "";
        }
    }


}
