package com.creatorhub.dto.s3;

public record ManuscriptPresignedRequest(
        String contentType,
        String originalFilename
) implements PresignedPutRequest {
    @Override public String resolveSuffix() {
        return ".jpg";
    }
}

