package com.creatorhub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/creators")
@RequiredArgsConstructor
public class CreatorController {

    // 인가 테스트용(삭제 예정)
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        String[] arr = {"AAA", "BBB", "CCC"};
        return ResponseEntity.ok(arr);
    }
}
