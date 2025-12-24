package com.creatorhub.controller;

import com.creatorhub.dto.S3PresignedUrlResponse;
import com.creatorhub.service.S3PresignedUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Slf4j
public class S3PresignedUploadController {
    private final S3PresignedUploadService uploadService;

    /**
     * presigned url 요청
     */
    @PostMapping("/presigned-url")
    public S3PresignedUrlResponse createPresignedUrl(
            @RequestParam String contentType
    ) {
        log.info("S3 Presigned PUT URL 생성 요청 - contentType={}", contentType);
        return uploadService.generatePresignedPutUrl(contentType);
    }
}
