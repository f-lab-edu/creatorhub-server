package com.creatorhub.service;

import com.creatorhub.entity.FileObject;
import com.creatorhub.repository.FileObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileObjectService {
    private final FileObjectRepository fileObjectRepository;

    @Transactional // 더티체킹
    public void markUploaded(Long fileObjectId) {
        FileObject fo = fileObjectRepository.findById(fileObjectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 FileObject를 찾을 수 없습니다."));
        fo.markUploaded();
        // fo.markReady();
    }
}
