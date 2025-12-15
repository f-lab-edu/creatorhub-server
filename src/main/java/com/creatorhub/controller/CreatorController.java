package com.creatorhub.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/creators")
@RequiredArgsConstructor
@Slf4j
public class CreatorController {

    // 인가 테스트용(삭제 예정)
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        log.info("creator list............");
        String[] arr = {"AAA", "BBB", "CCC"};
        return ResponseEntity.ok(arr);
    }
}
