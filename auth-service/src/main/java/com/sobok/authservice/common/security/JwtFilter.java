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
import org.springframework.util.AntPathMatcher;
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
            "/actuator/**", "/auth/login", "/auth/reissue", "/sms/send", "/auth/recover/**", "/sms/verify",
            "/auth/user-signup", "/auth/rider-signup", "/auth/shop-signup", "/auth/findLoginId", "/auth/verification", "/auth/reset-password",
            "/auth/temp-token", "/auth/check-id", "/auth/check-nickname", "/auth/check-email", "/auth/check-permission", "/auth/check-shopName",
            "/auth/check-shopAddress", "/auth/social-user-signup"
    );

    private static final List<String> deniedPaths = List.of(
            "/auth/user-signup", "/auth/api/check-id", "/auth/temp-token",
            "/auth/login", "/auth/logout", "/auth/reissue", "/auth/delete", "/auth/recover/*", "/auth/rider-signup", "/auth/shop-signup",
            "/auth/findLoginId", "/auth/verification",
            "/auth/reset-password", "/auth/edit-password", "/auth/get-info", "/auth/social-user-signup",
            "/sms/send", "/sms/verify",
            "/api/active-rider", "/api/auth/info", "/api/findByOauthId", "/api/social-token", "/api/auth/login-id", "/api/get-rider-inactive"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Auth Service에 요청이 발생했습니다.");

        // Path 점검
        String path = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();

        // 허용 url 리스트를 순회하면서 지금 들어온 요청 url과 하나라도 일치하면 true 리턴
        boolean isAllowed = whiteList.stream()
                .anyMatch(url -> antPathMatcher.match(url, path));

        boolean isDenied = deniedPaths.stream().anyMatch(url -> antPathMatcher.match(url, path));

        // 허용 path라면 Filter 동작하지 않고 넘기기
        if (isAllowed) {
            filterChain.doFilter(request, response);
            return;
        }

        // 필터 동작
        try {
            // 토큰이 존재하는 지 확인
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || authHeader.isEmpty()) {
                log.warn("Authorization 헤더가 비어있습니다.");
                throw new Exception(); // TODO 추후 Exception에 의미를 전달하는 구조로 변경 가능
            }

            // Bearer 토큰인지 확인
            if (!authHeader.startsWith("Bearer ")) {
                log.warn("Authorization 헤더가 Bearer 형식이 아닙니다.");
                throw new Exception();
            }

            // 토큰 유효성 검사
            String token = authHeader.replace("Bearer ", "");
            if (!validateToken(token)) {
                log.warn("토큰이 만료되었습니다.");
                throw new Exception();
            }

            // 토큰에서 사용자 정보 추출
            Claims claims = getClaims(token);

            // 토큰에서 정보 추출
            long id = Long.parseLong(claims.getSubject());
            Role role = Role.from(claims.get("role", String.class));

            // TEMP일 경우 URI 검사
            if (role == Role.TEMP) {
                String uri = request.getRequestURI();
                log.info("uri:{}", uri);
                if (isDenied) {
                    log.warn("TEMP 역할이 허용되지 않은 URI에 접근하려 했습니다: {}", uri);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "TEMP는 이 경로에 접근할 수 없습니다.");
                    throw new Exception();
                }
            }

            // @AuthenticationPrinciple, @PreAuthorize("hasRole('ADMIN')") 같은 로직을 사용하기 위한 로직
            TokenUserInfo tokenUserInfo = TokenUserInfo.builder().id(id).role(role.name()).build();
            String roleClaim = switch (role) {
                case USER -> "userId";
                case HUB -> "shopId";
                case RIDER -> "riderId";
                default -> null;
            };
            if (roleClaim != null) {
                Long roleId = Long.parseLong(claims.get(roleClaim, String.class));
                switch (role) {
                    case USER -> tokenUserInfo.setUserId(roleId);
                    case HUB -> tokenUserInfo.setShopId(roleId);
                    case RIDER -> tokenUserInfo.setRiderId(roleId);
                }
            }
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(tokenUserInfo, "", authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (Exception e) {
            log.warn("토큰 정보가 유효하지 않습니다.");
            onError(response);
            return;
        }

        // 문제 없다면 진행
        filterChain.doFilter(request, response);
    }

    /**
     * claim 꺼내기
     *
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
            throw e;
        }
    }

    /**
     * 토큰 유효기간 검증
     *
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
            throw e;
        }
    }

    /**
     * 인증 통과하지 못하면(토큰에 문제가 있다면) 에러 응답 전송
     *
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
