package com.creatorhub.security.auth;

import java.security.Principal;

public record CustomUserPrincipal(Long id) implements Principal {

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
