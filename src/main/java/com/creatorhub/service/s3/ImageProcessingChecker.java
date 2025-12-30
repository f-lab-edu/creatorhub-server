package com.creatorhub.service.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.util.List;

@Service
public class ImageProcessingChecker {
    private final S3Client s3Client;
    private final String bucket;

    private static final List<String> DERIVED_SIZES = List.of(
            "_83x90.jpg",
            "_98x79.jpg",
            "_125x101.jpg",
            "_202x164.jpg",
            "_217x165.jpg",
            "_218x120.jpg"
    );

    public ImageProcessingChecker(
            S3Client s3Client,
            @Value("${cloud.aws.s3.bucket}") String bucket
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public boolean isAllDerivedImagesReady(String baseKey) {
        for (String suffix : DERIVED_SIZES) {
            String key = baseKey + suffix;
            try {
                // 비용과 성능을 위해 메타데이터만 조회
                s3Client.headObject(b -> b.bucket(bucket).key(key));
            } catch (NoSuchKeyException e) {
                return false;
            }
        }
        return true;
    }
}

