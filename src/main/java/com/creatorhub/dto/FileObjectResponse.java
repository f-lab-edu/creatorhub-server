package com.creatorhub.dto;

import com.creatorhub.entity.FileObject;

public record FileObjectResponse(
        Long id,
        String storageKey,
        String originalFilename,
        String contentType,
        Long sizeBytes,
        Integer width,
        Integer height
) {
    public static FileObjectResponse from(FileObject fileObject) {
        return new FileObjectResponse(
                fileObject.getId(),
                fileObject.getStorageKey(),
                fileObject.getOriginalFilename(),
                fileObject.getContentType(),
                fileObject.getSizeBytes(),
                fileObject.getWidth(),
                fileObject.getHeight()
        );
    }
}