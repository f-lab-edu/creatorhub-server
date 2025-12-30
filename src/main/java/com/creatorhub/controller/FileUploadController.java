package com.creatorhub.controller;

import com.creatorhub.dto.FileObjectResponse;
import com.creatorhub.dto.S3PresignedUrlRequest;
import com.creatorhub.dto.S3PresignedUrlResponse;
import com.creatorhub.service.FileObjectService;
import com.creatorhub.service.s3.S3PresignedUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {
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
    @PostMapping("/{fileObjectId}/uploaded")
    public void complete(@PathVariable Long fileObjectId) {
        fileObjectService.markUploaded(fileObjectId);
    }

    @GetMapping("/{fileObjectId}/status")
    public FileObjectResponse getStatus(@PathVariable Long fileObjectId) {
        return fileObjectService.checkAndGetStatus(fileObjectId);
    }
}
