package com.creatorhub.dto;

public record S3PresignedUrlResponse (
        String uploadUrl,
        String objectKey
) { }
