package com.creatorhub.controller;

import com.creatorhub.dto.member.MemberRequest;
import com.creatorhub.dto.member.MemberResponse;
import com.creatorhub.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "제일 먼저 회원가입을 진행해 주세요.")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signup(@Valid @RequestBody MemberRequest req) {
        MemberResponse memberResponse = memberService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
    }
}
