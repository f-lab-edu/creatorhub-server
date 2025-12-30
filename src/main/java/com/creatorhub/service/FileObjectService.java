package com.creatorhub.service;

import com.creatorhub.constant.FileObjectStatus;
import com.creatorhub.dto.FileObjectResponse;
import com.creatorhub.entity.FileObject;
import com.creatorhub.repository.FileObjectRepository;
import com.creatorhub.service.s3.ImageProcessingChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileObjectService {
    private final FileObjectRepository fileObjectRepository;
    private final ImageProcessingChecker checker;

    /**
     * status 변경(UPLOADED)
     */
    @Transactional // 더티체킹
    public void markUploaded(Long fileObjectId) {
        FileObject fo = fileObjectRepository.findById(fileObjectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 FileObject를 찾을 수 없습니다."));
        fo.markUploaded();
    }

    @Transactional
    public FileObjectResponse checkAndGetStatus(Long fileObjectId) {
        FileObject fo = fileObjectRepository.findById(fileObjectId)
                .orElseThrow();

        if (fo.getStatus() == FileObjectStatus.UPLOADED) {
            String baseKey = fo.extractBaseKey();
            // 예: upload/2025/12/27/dab6cd52-4c4e-4a07-8f25-352d7dba2d6f

            if (checker.isAllDerivedImagesReady(baseKey)) {
                fo.markReady(); // status: READY로 변경
            }
        }

        return FileObjectResponse.from(fo);
    }
}
