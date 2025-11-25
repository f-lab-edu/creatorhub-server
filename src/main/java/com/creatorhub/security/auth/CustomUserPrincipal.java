package com.creatorhub.security.auth;

import lombok.RequiredArgsConstructor;
import java.security.Principal;


@RequiredArgsConstructor
public class CustomUserPrincipal implements Principal {

    private final String id;

    @Override
    public String getName() {
        return id;
    }
}

