package com.creatorhub.dto.s3;

import com.creatorhub.constant.CreationThumbnailType;

public record CreationThumbnailPresignedRequest(
        String contentType,
        CreationThumbnailType thumbnailType,
        String originalFilename
) implements PresignedPutRequest {
    @Override public String resolveSuffix() {
        return thumbnailType.resolveSuffix();
    }
}

