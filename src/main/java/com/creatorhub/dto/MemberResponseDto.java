package com.creatorhub.dto;

import com.creatorhub.constant.Gender;
import com.creatorhub.constant.Role;
import com.creatorhub.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponseDto {

    private Long id;
    private String email;
    private String name;
    private LocalDate birthday;
    private Gender gender;
    private Role role;
    private LocalDateTime createdAt;

    // Entity -> DTO
    public static MemberResponseDto from(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .birthday(member.getBirthday())
                .gender(member.getGender())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
