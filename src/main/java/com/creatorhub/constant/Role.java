package com.creatorhub.constant;

import lombok.Getter;

import java.util.Set;

@Getter
public enum Role {

    MEMBER(Set.of("ROLE_MEMBER")),

    // CREATOR는 MEMBER 권한 + CREATOR 권한을 모두 가진다
    CREATOR(Set.of("ROLE_MEMBER", "ROLE_CREATOR"));

    private final Set<String> authorities;

    Role(Set<String> authorities) {
        this.authorities = authorities;
    }

}
