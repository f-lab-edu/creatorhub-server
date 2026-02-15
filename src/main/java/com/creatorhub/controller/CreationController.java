package com.creatorhub.controller;


import com.creatorhub.dto.creation.CreationRequest;
import com.creatorhub.service.CreationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(
        name = "Creation",
        description = """
        작품등록 사용 순서
         1. 반드시 로그인 -> Authorize 버튼에 토큰 입력 후 진행(회원만 작가 등록 가능)
         2. 작가 등록 후 Auth의 'Refresh 토큰 재발급' 을 실행
         3. Authorize 버튼에 토큰 재등록
        """
)
@RestController
@RequestMapping("/api/creations")
@RequiredArgsConstructor
public class CreationController {
    private final CreationService creationService;

    @Operation(summary = "작품등록")
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createCreation(@Valid @RequestBody CreationRequest req) {
        Long id = creationService.createCreation(req);
        return ResponseEntity.ok(Map.of("creationId", id));
    }

}
