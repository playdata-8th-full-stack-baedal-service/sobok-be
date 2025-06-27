package com.sobok.authservice.common.jwt;

import com.sobok.authservice.auth.entity.Auth;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * Access Token 발급 (예외가 발생했다면 빈 문자열)
     * @param auth
     * @return
     */
    public String generateAccessToken(Auth auth) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expiration * 60 * 1000);

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
            Date expiryDate = new Date(now.getTime() + refreshExpiration * 60 * 1000);

            return Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, refreshSecretKey.getBytes(StandardCharsets.UTF_8))
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


}
