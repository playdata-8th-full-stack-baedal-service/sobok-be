package com.sobok.authservice.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobok.authservice.common.dto.ApiResponse;
import com.sobok.authservice.common.dto.TokenUserInfo;
import com.sobok.authservice.common.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    @Value("${jwt.secretKey}")
    private String secretKey;

    private final ObjectMapper objectMapper;

    List<String> whiteList = List.of(
            "/auth/signup", "/auth/login", "/auth/test", "/auth/sms/send"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        for (String path : whiteList) {
            if(request.getRequestURI().equals(path)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        log.warn("JWT Filter에 돌고 있어요!");

        String authHeader = request.getHeader("Authorization");

        // 토큰이 존재하는 지 확인
        if (authHeader == null || authHeader.isEmpty() ) {
            log.warn("Authorization 헤더가 비어있습니다.");
            onError(response);
            return;
        }

        // Bearer 토큰인지 확인
        if (!authHeader.startsWith("Bearer ")) {
            log.warn("Authorization 헤더가 Bearer 형식이 아닙니다.");
            onError(response);
            return;
        }
        String token = authHeader.replace("Bearer ", "");

        // 토큰 유효성 검사
        if (!validateToken(token)) {
            onError(response);
            return;
        }

        // 토큰에서 사용자 정보 추출
        Claims claims = getClaims(token);
        if(claims == null) {
            onError(response);
            return;
        }

        long id;
        Role role;
        try {
            id = Long.parseLong(claims.getSubject());
            role = Role.from(claims.get("role", String.class));
        } catch (Exception e) {
            log.warn("토큰 정보가 유효하지 않습니다.");
            onError(response);
            return;
        }

        // @AuthenticationPrinciple, @PreAuthorize("hasRole('ADMIN')") 같은 로직을 사용하기 위한 로직
        TokenUserInfo tokenUserInfo = TokenUserInfo.builder().id(id).role(role.name()).build();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(tokenUserInfo, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 문제 없다면 진행
        filterChain.doFilter(request, response);
    }

    /**
     * claim 꺼내기
     * @param token
     * @return
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("토큰에서 Claim을 꺼내는 과정에서 오류가 발생했습니다.");
            return null;
        }
    }

    /**
     * 토큰 유효기간 검증
     * @param token
     * @return
     */
    private boolean validateToken(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return expiration.after(new Date());
        } catch (Exception e) {
            log.error("토큰 검증 과정에서 문제가 발생했습니다.");
            return false;
        }
    }

    /**
     * 인증 통과하지 못하면(토큰에 문제가 있다면) 에러 응답 전송
     * @param response
     * @throws IOException
     */
    private void onError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 공통 실패 응답 JSON으로 변환
        String body = objectMapper.writeValueAsString(ApiResponse.fail(666, "토큰 검증에 실패하였습니다."));
        response.getWriter().write(body);
    }
}
