package com.creatorhub.controller;

import com.creatorhub.dto.LoginRequest;
import com.creatorhub.dto.TokenPayload;
import com.creatorhub.security.util.JWTUtil;
import com.creatorhub.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    @PostMapping("/make")
    public ResponseEntity<Map<String, String>> makeToken(@RequestBody LoginRequest loginRequest) {

        log.info("makeToken............");

        TokenPayload tokenPayload = memberService.authenticate(loginRequest.email(), loginRequest.password());

        Long id = tokenPayload.id();
        Map<String, Object> claims = tokenPayload.toMap();

        String accessToken = jwtUtil.createAccessToken(claims);
        String refreshToken = jwtUtil.createRefreshToken(id);

        log.debug("accessToken prefix: {}", accessToken.substring(0, 20));

        // 확인용(삭제 예정)
        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "accessToken", accessToken,
                        "refreshToken", refreshToken
                ));
    }
}
