package com.creatorhub.controller;

import com.creatorhub.dto.*;
import com.creatorhub.security.auth.CustomUserPrincipal;
import com.creatorhub.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<TokenPair> login(@RequestBody LoginRequest request) {
        log.info("로그인 요청 - email={}", request.email());
        TokenPair tokenPair = authService.login(request.email(), request.password());
        return ResponseEntity.ok(tokenPair);
    }

    /**
     * refresh 토큰 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@RequestBody RefreshRequest request) {
        log.info("토큰 재발급 요청");
        TokenPair tokenPair = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(tokenPair);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Long id = principal.id();
        log.info("로그아웃 요청 - id={}", id);
        authService.logout(id);
        return ResponseEntity.noContent().build();
   }
}
