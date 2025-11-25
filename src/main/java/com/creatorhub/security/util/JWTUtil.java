package com.creatorhub.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JWTUtil {
    private final SecretKey secretKey;
    private final long accessTokenExpMinutes;
    private final long refreshTokenExpDays;

    public JWTUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-exp-min}") long accessTokenExpMinutes,
            @Value("${jwt.refresh-token-exp-days}") long refreshTokenExpDays
    ) {
        try {
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Failed to initialize JWT secret key", e);
            throw new IllegalStateException("JWT secret key 초기화 실패", e);
        }
        this.accessTokenExpMinutes = accessTokenExpMinutes;
        this.refreshTokenExpDays = refreshTokenExpDays;
    }

    public String createToken(Map<String, Object> claims, long expireMinutes) {

        ZonedDateTime now = ZonedDateTime.now();

        return Jwts.builder().header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .issuedAt(Date.from(now.toInstant()))
                .expiration((Date.from(now.plusMinutes(expireMinutes).toInstant())))
                .claims(claims)
                .signWith(secretKey)
                .compact();

    }

    public String createAccessToken(Map<String, Object> claims) {
        return createToken(claims, accessTokenExpMinutes);
    }

    public String createRefreshToken(Long id) {
        long refreshMinutes = refreshTokenExpDays * 24 * 60;
        return createToken(Map.of("id", id), refreshMinutes);
    }


    public Map<String, Object> validateToken(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        log.info("claims: {}", claims);

        return claims;

    }
}
