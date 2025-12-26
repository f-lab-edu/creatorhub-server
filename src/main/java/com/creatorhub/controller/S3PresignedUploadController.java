package com.creatorhub.controller;

import com.creatorhub.dto.S3PresignedUrlRequest;
import com.creatorhub.dto.S3PresignedUrlResponse;
import com.creatorhub.service.FileObjectService;
import com.creatorhub.service.S3PresignedUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Slf4j
public class S3PresignedUploadController {
    private final S3PresignedUploadService uploadService;
    private final FileObjectService fileObjectService;

    /**
     * presigned url 요청
     */
    @PostMapping("/presigned-url")
    public S3PresignedUrlResponse createPresignedUrl(@RequestBody S3PresignedUrlRequest req) {
        log.info("Presigned PUT 요청 - contentType={}, thumbnailType={}, originalFilename={}",
                req.contentType(), req.thumbnailType(), req.originalFilename());

        return uploadService.generatePresignedPutUrl(req);
    }

    /**
     * fileObject 상태 변경
     */
    @PostMapping("/{fileObjectId}/complete")
    public void complete(@PathVariable Long fileObjectId) {
        fileObjectService.markUploaded(fileObjectId);
    }
}
