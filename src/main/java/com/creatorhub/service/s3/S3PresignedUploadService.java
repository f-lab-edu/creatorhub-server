package com.creatorhub.service.s3;

import com.creatorhub.constant.FileObjectStatus;
import com.creatorhub.dto.s3.PresignedPutRequest;
import com.creatorhub.dto.s3.S3PresignedUrlResponse;
import com.creatorhub.entity.FileObject;
import com.creatorhub.repository.FileObjectRepository;
import org.springframework.beans.factory.annotation.Value;
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

    private final S3Presigner presigner;
    private final FileObjectRepository fileObjectRepository;
    private final String bucket;

    public S3PresignedUploadService(S3Presigner presigner,
                                    FileObjectRepository fileObjectRepository,
                                    @Value("${cloud.aws.s3.bucket}") String bucket) {
        this.presigner = presigner;
        this.fileObjectRepository = fileObjectRepository;
        this.bucket = bucket;
    }

    public S3PresignedUrlResponse generatePresignedPutUrl(PresignedPutRequest req) {

        // 1. 검증 (지금은 jpeg 고정 정책)
        validateContentType(req.contentType());

        // 2. storageKey 생성
        String storageKey = createObjectKey(req.resolveSuffix());

        // 3. FileObject 생성
        FileObject saved = saveFileObject(storageKey, req);

        // 4. presigned 발급
        PresignedPutObjectRequest presigned = presignPut(storageKey, req.contentType());

        // 5. 응답에 fileObjectId 포함
        return new S3PresignedUrlResponse(
                saved.getId(),
                presigned.url().toString(),
                storageKey
        );
    }

    private void validateContentType(String contentType) {
        if (!"image/jpeg".equals(contentType)) {
            throw new IllegalArgumentException("image/jpeg 타입만 허용 가능합니다.");
        }
    }

    private String createObjectKey(String suffix) {
        if (suffix == null || suffix.isBlank()) {
            throw new IllegalArgumentException("suffix가 비어있습니다.");
        }
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String base = "upload/" + datePath + "/" + UUID.randomUUID();
        return base + suffix;
    }

    private FileObject saveFileObject(String storageKey, PresignedPutRequest req) {
        FileObject fileObject = FileObject.create(
                storageKey,
                req.originalFilename(),
                FileObjectStatus.INIT,
                req.contentType(),
                0L
        );
        return fileObjectRepository.save(fileObject);
    }

    private PresignedPutObjectRequest presignPut(String key, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(2))
                .putObjectRequest(objectRequest)
                .build();

        return presigner.presignPutObject(presignRequest);
    }
}
