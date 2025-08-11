package com.sobok.paymentservice.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobok.paymentservice.common.dto.CommonResponse;
import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
    private static final String ACCESS_TOKEN_BLACKLIST_KEY = "BLACKLIST_ACCESS_TOKEN:";

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    List<String> whiteList = List.of(
            "/actuator/**", "/v3/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Payment Service에 요청이 발생했습니다.");

        // Path 점검
        String path = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();

        // 허용 url 리스트를 순회하면서 지금 들어온 요청 url과 하나라도 일치하면 true 리턴
        boolean isAllowed = whiteList.stream()
                .anyMatch(url -> antPathMatcher.match(url, path));

        // 허용 path라면 Filter 동작하지 않고 넘기기
        if (isAllowed || path.contains("swagger")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 필터 동작
        try {
            // 토큰이 존재하는 지 확인
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || authHeader.isEmpty()) {
                log.warn("Authorization 헤더가 비어있습니다.");
                throw new Exception();
            }

            // Bearer 토큰인지 확인
            if (!authHeader.startsWith("Bearer ")) {
                log.warn("Authorization 헤더가 Bearer 형식이 아닙니다.");
                throw new Exception();
            }

            // 토큰 유효성 검사
            String token = authHeader.replace("Bearer ", "");

            // 토큰에서 사용자 정보 추출
            Claims claims = getClaims(token);

            // 토큰에서 정보 추출
            long id = Long.parseLong(claims.getSubject());

            // 블랙리스트 검사 로직
            // Redis에서 현재 토큰이 블랙리스트에 등록되어 있는지 확인

            if (token.equals(redisTemplate.opsForValue().get(ACCESS_TOKEN_BLACKLIST_KEY + id))) {
                log.warn("블랙리스트에 등록된 토큰으로 접근 시도. 토큰: {}", token);
                onError(response,401, "블랙리스트에 등록된 토큰입니다.");
                return;
            }
            if (!validateToken(token)) {
                log.warn("토큰이 만료되었습니다.");
                throw new Exception();
            }

            Role role = Role.from(claims.get("role", String.class));

            // FEIGN일 경우 URI 검사
            if (role == Role.FEIGN && !antPathMatcher.match("/api/**", path)) {
                log.warn("FEIGN 역할이 허용되지 않은 URI에 접근하려 했습니다: {}", path);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "FEIGN은 이 경로에 접근할 수 없습니다.");
                return;
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
            onError(response,666, "토큰 검증에 실패하였습니다.");
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
    private void onError(HttpServletResponse response, int httpStatus, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 공통 실패 응답 JSON으로 변환
        String body = objectMapper.writeValueAsString(CommonResponse.fail(httpStatus, message));
        response.getWriter().write(body);
    }
}
