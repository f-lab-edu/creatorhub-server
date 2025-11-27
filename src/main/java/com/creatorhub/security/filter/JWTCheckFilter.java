package com.creatorhub.security.filter;

import com.creatorhub.constant.ErrorCode;
import com.creatorhub.constant.Role;
import com.creatorhub.dto.TokenPayload;
import com.creatorhub.security.auth.CustomUserPrincipal;
import com.creatorhub.security.exception.JwtAuthenticationException;
import com.creatorhub.security.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTCheckFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        // 토큰 없이 접근 허용할 경로들
        return path.startsWith("/api/auth")          // 토큰 발급
                || path.equals("/api/members/signup");     // 회원가입
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("JWTCheckFilter doFilter............ ");
        log.info("requestURI: {}", request.getRequestURI());

        String headerStr = request.getHeader("Authorization");
        log.info("headerStr: {}", headerStr);

        // Access Token이 없는 경우
        if (headerStr == null || !headerStr.startsWith("Bearer ")) {
            throw new JwtAuthenticationException(ErrorCode.INVALID_TOKEN);
        }

        String accessToken = headerStr.substring(7);

        try {
            TokenPayload payload = jwtUtil.validateToken(accessToken);

            log.info("TokenPayload: {}", payload);

            Long id = payload.id();
            Role role = payload.role();

            // Role enum이 들고 있는 권한 세트로 GrantedAuthority 생성
            List<SimpleGrantedAuthority> authorities = role.getAuthorities().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            // 토큰 검증 결과를 이용해서 Authentication 객체를 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserPrincipal(id),
                            null,
                            authorities
                    );

            // SecurityContextHolder에 Authentication 객체 저장
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(context);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException(ErrorCode.EXPIRE_TOKEN, e);
        } catch (JwtException e) {
            throw new JwtAuthenticationException(ErrorCode.INVALID_TOKEN, e);
        }
    }
}