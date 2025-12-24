package com.creatorhub.service;

import com.creatorhub.dto.S3PresignedUrlResponse;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class S3PresignedUploadService {

    private static final String BUCKET_NAME = "creatorhub-dev-bucket";

    private final S3Presigner presigner;

    public S3PresignedUploadService(S3Presigner presigner) {
        this.presigner = presigner;
    }

    public S3PresignedUrlResponse generatePresignedPutUrl(String contentType) {

        String key = createObjectKey(contentType);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(2))
                        .putObjectRequest(objectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest =
                presigner.presignPutObject(presignRequest);

        return new S3PresignedUrlResponse(
                presignedRequest.url().toString(),
                key
        );
    }

    private String createObjectKey(String contentType) {
        String ext = contentTypeToExtension(contentType);
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        return "upload/" + datePath + "/" + UUID.randomUUID() + ext;
    }

    private String contentTypeToExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
}
