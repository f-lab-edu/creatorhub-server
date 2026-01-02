package com.creatorhub.dto.s3;

import com.creatorhub.constant.EpisodeThumbnailType;

public record EpisodeThumbnailPresignedRequest(
        String contentType,
        EpisodeThumbnailType thumbnailType,
        String originalFilename
) implements PresignedPutRequest {
    @Override public String resolveSuffix() {
        return thumbnailType.resolveSuffix();
    }
}

