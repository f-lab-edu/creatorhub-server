package com.creatorhub.controller;

import com.creatorhub.dto.creator.CreatorRequest;
import com.creatorhub.dto.creator.CreatorResponse;
import com.creatorhub.security.auth.CustomUserPrincipal;
import com.creatorhub.service.CreatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Creator",
        description = """
        사용 순서
         1. 반드시 로그인 -> Authorize 버튼에 토큰 입력 후 진행(회원만 작가 등록 가능)
         2. 작가 등록 후 Auth의 'Refresh 토큰 재발급' 을 실행
         3. Authorize 버튼에 토큰 재등록
        """
)
@RestController
@RequestMapping("/api/creators")
@RequiredArgsConstructor
public class CreatorController {

    private final CreatorService creatorService;

    @Operation(summary = "작가등록")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PostMapping("/signup")
    public ResponseEntity<CreatorResponse> signup(
            @Valid @RequestBody CreatorRequest req,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        CreatorResponse creatorResponse = creatorService.signup(principal.id(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(creatorResponse);
    }
}
