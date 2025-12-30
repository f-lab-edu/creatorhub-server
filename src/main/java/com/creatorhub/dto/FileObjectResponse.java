package com.creatorhub.dto;

import com.creatorhub.constant.FileObjectStatus;
import com.creatorhub.entity.FileObject;

public record FileObjectResponse(
        Long id,
        String storageKey,
        String originalFilename,
        FileObjectStatus status,
        String contentType,
        Long sizeBytes
) {
    public static FileObjectResponse from(FileObject fileObject) {
        return new FileObjectResponse(
                fileObject.getId(),
                fileObject.getStorageKey(),
                fileObject.getOriginalFilename(),
                fileObject.getStatus(),
                fileObject.getContentType(),
                fileObject.getSizeBytes()
        );
    }
}