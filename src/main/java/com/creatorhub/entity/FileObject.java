package com.creatorhub.entity;

import com.creatorhub.constant.FileObjectStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Table(
        name = "file_object",
        uniqueConstraints = @UniqueConstraint(name = "uk_file_object_storage_key", columnNames = "storage_key")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE file_object SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class FileObject extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String storageKey;

    @Column(length = 255)
    private String originalFilename;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, columnDefinition = "VARCHAR(30)")
    private FileObjectStatus status;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private Long sizeBytes;

    private Integer width;

    private Integer height;

    @Builder(access = AccessLevel.PRIVATE)
    private FileObject(String storageKey,
                       String originalFilename,
                       FileObjectStatus status,
                       String contentType,
                       Long sizeBytes,
                       Integer width,
                       Integer height) {
        this.storageKey = storageKey;
        this.originalFilename = originalFilename;
        this.status = status;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.width = width;
        this.height = height;
    }

    public static FileObject create(String storageKey,
                                    String originalFilename,
                                    FileObjectStatus status,
                                    String contentType,
                                    Long sizeBytes,
                                    Integer width,
                                    Integer height) {
        return FileObject.builder()
                .storageKey(storageKey)
                .originalFilename(originalFilename)
                .status(status)
                .contentType(contentType)
                .sizeBytes(sizeBytes)
                .width(width)
                .height(height)
                .build();
    }

    public void markUploaded() { this.status = FileObjectStatus.UPLOADED; }
    public void markReady() { this.status = FileObjectStatus.READY; }
    public void markFailed() { this.status = FileObjectStatus.FAILED; }
}