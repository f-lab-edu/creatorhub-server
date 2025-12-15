package com.creatorhub.dto;

public record TokenPair(
        String accessToken,
        String refreshToken
) {}