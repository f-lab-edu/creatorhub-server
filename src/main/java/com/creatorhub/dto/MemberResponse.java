package com.creatorhub.dto;

import com.creatorhub.constant.Gender;
import com.creatorhub.constant.Role;
import com.creatorhub.entity.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public record MemberResponse(
        Long id,
        String email,
        String name,
        LocalDate birthday,
        Gender gender,
        Role role,
        LocalDateTime createdAt
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getBirthday(),
                member.getGender(),
                member.getRole(),
                member.getCreatedAt()
        );
    }
}