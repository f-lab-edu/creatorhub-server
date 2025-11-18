package com.creatorhub.entity;

import com.creatorhub.constant.Gender;
import com.creatorhub.constant.Role;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "enum('FEMALE','MALE','NONE') default 'NONE'")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "enum('MEMBER','ARTIST') default 'MEMBER'")
    private Role role;

    @Builder
    public Member(String email, String password, String name, LocalDate birthday, Gender gender, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender != null ? gender : Gender.NONE;  // gender 기본값
        this.role = role != null ? role : Role.MEMBER;        // role 기본값
    }
}
