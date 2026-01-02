package com.creatorhub.dto.s3;

public record S3PresignedUrlResponse (
        Long fileObjectId,
        String uploadUrl,
        String objectKey
) { }
