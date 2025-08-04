package com.sobok.authservice.common.jwt;

import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.common.enums.Role;
import com.sobok.authservice.common.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN:";
    private static final String ACCESS_TOKEN_BLACKLIST_KEY = "BLACKLIST_ACCESS_TOKEN:";
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private Long expiration;
    @Value("${jwt.refreshSecretKey}")
    private String refreshSecretKey;
    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;
    @Value("${jwt.feignSecretKey}")
    private String feignSecretKey;
    @Value("${jwt.feignExpiration}")
    private Long feignExpiration;

    /**
     * Access Token 발급 (예외가 발생했다면 빈 문자열)
     *
     * @param auth
     * @return
     */
    public String generateAccessToken(Auth auth, Long roleId) throws CustomException {
        try {
            String claim = switch (auth.getRole()) {
                case USER -> "userId";
                case RIDER -> "riderId";
                case HUB -> "shopId";
                default -> "none";
            };

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
                    .claim(claim, roleId.toString())
                    .compact();
        } catch (InvalidKeyException e) {
            log.error("액세스 토큰 생성 중 문제가 발생했습니다.");
            throw new CustomException("액세스 토큰 생성 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Feign Access Token 발급 (예외가 발생했다면 빈 문자열)
     *
     * @return
     */
    public String generateFeignToken() throws CustomException {
        log.info("Feign Token 생성");
        try {
            // 현재 시간
            Date now = new Date();
            // ? 분 * (60 초 / 1 분) * (1000 ms / 1 초)
            Date expiryDate = new Date(now.getTime() + feignExpiration * 60 * 1000);

            return Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, feignSecretKey.getBytes(StandardCharsets.UTF_8))
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

    /**
     * Refresh Token 발급 (예외가 발생했다면 빈 문자열)
     *
     * @param auth
     * @return
     */
    public String generateRefreshToken(Auth auth, Long roleId) throws CustomException {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + refreshExpiration * 60 * 60 * 1000);

            return Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, refreshSecretKey.getBytes(StandardCharsets.UTF_8))
                    .setExpiration(expiryDate)
                    .setIssuedAt(now)
                    .setSubject(auth.getId().toString())
                    .claim("role", auth.getRole().toString())
                    .compact();
        } catch (InvalidKeyException e) {
            log.error("리프레시 토큰 생성 중 문제가 발생했습니다.");
            throw new CustomException("리프레시 토큰 생성 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 임시 Token 발급 (예외가 발생했다면 빈 문자열)
     */
    public String generateTempToken() throws CustomException {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + 30 * 60 * 1000); // 30분

            return Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, feignSecretKey.getBytes(StandardCharsets.UTF_8))
                    .setExpiration(expiryDate)
                    .setIssuedAt(now)
                    .setSubject("999")
                    .claim("role", Role.TEMP.toString())
                    .compact();
        } catch (InvalidKeyException e) {
            log.error("임시 토큰 생성 중 문제가 발생했습니다.");
            throw new CustomException("임시 토큰 생성 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 리프레시 토큰 Redis 저장
     *
     * @param auth
     * @param refreshToken
     */
    public void saveRefreshToken(Auth auth, String refreshToken) {
        // refresh 토큰 발급 시 redis에 저장
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_KEY + auth.getId().toString(),
                refreshToken,
                refreshExpiration * 60 * 60 * 1000,
                TimeUnit.MILLISECONDS
        );
    }

    // 토큰에서 클레임 꺼내오기
    public Claims getClaims(String token, String secretKey) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("토큰에서 Claim을 꺼내는 과정에서 오류가 발생했습니다.");
            throw e;
        }
    }

    // 로그아웃시 리프레시 토큰 삭제 후 액세스 토큰 블랙리스트에 추가
    public void logout(String accessToken, Long authId) {
        log.info("{}번 사용자의 로그아웃으로 리프레쉬 토큰 삭제", authId);

        try {
            Date expiration = getClaims(accessToken, secretKey).getExpiration();
            long remainingTime = expiration.getTime() - new Date().getTime(); // 액세스 토큰이 원래 만료될 때까지 남은 시간

            if (remainingTime > 0) { // 액세스 토큰이 유효 기간이 남았는지 확인 0보다 크면 블랙리스트 등록
                redisTemplate.opsForValue().set( // redis에 저장
                        ACCESS_TOKEN_BLACKLIST_KEY + authId,
                        "logout",
                        remainingTime,
                        TimeUnit.MILLISECONDS // 블랙리스트 항목의 만료시간 (액세스 토큰 유효기간이 남는동안만 동작하도록)
                );
                log.info("액세스 토큰을 블랙리스트에 추가. 남은시간: {}ms" ,remainingTime);
            }
        } catch (Exception e) {
            log.error("로그아웃 처리 중 액세스 토큰을 블랙리스트에 추가하는데 실패");

        }
    }


}
