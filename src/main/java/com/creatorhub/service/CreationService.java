package com.creatorhub.service;

import com.creatorhub.constant.CreationThumbnailType;
import com.creatorhub.constant.FileObjectStatus;
import com.creatorhub.constant.ThumbnailKeys;
import com.creatorhub.dto.CreationRequest;
import com.creatorhub.entity.Creation;
import com.creatorhub.entity.CreationThumbnail;
import com.creatorhub.entity.Creator;
import com.creatorhub.entity.FileObject;
import com.creatorhub.exception.CreatorNotFoundException;
import com.creatorhub.repository.CreationRepository;
import com.creatorhub.repository.CreatorRepository;
import com.creatorhub.repository.FileObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreationService {
    private final CreationRepository creationRepository;
    private final FileObjectRepository fileObjectRepository;
    private final CreatorRepository creatorRepository;

    @Transactional
    public Long createCreation(CreationRequest req) {

        // 1. 해당 작가가 있는지 확인
        Creator creator = creatorRepository.findById(req.creatorId())
                .orElseThrow(CreatorNotFoundException::new);

        // 2. Creation 생성
        Creation creation = Creation.create(
                creator,
                req.format(),
                req.genre(),
                req.title(),
                req.plot(),
                req.isPublic()
        );
        creation.setPublishDays(req.publishDays());

        // 3. 원본 fileObject 조회 + baseKey 추출
        FileObject original = fileObjectRepository.findById(req.horizontalOriginalFileObjectId())
                .orElseThrow(() -> new IllegalArgumentException("FileObject를 찾을 수 없습니다: " + req.horizontalOriginalFileObjectId()));

        log.info("original 데이터 조회 결과- horizontalOriginalFileObjectId: {}, original: {}", req.horizontalOriginalFileObjectId(), original);

        // (선택) 상태 검증: READY 아니면 등록 막기
        if (original.getStatus() != FileObjectStatus.READY) {
            throw new IllegalStateException("원본 파일이 READY 상태가 아닙니다. status=" + original.getStatus());
        }

        String baseKey = original.extractBaseKey();

        // 4. 7개 key 만들어서 FileObject에서 해당 데이터들 조회
        List<String> keys = ThumbnailKeys.allSuffixes().stream()
                .map(suffix -> baseKey + suffix)
                .toList();

        List<FileObject> all = fileObjectRepository.findByStorageKeyIn(keys);
        Map<String, FileObject> byKey = new HashMap<>();
        for (FileObject fo : all) byKey.put(fo.getStorageKey(), fo);

        // 5. 누락 체크
        List<String> missing = new ArrayList<>();
        for (String k : keys) {
            if (!byKey.containsKey(k)) missing.add(k);
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException("썸네일 파일이 누락되었습니다.: " + missing);
        }

        // 6. CreationThumbnail 생성
        // 원본(가로) - displayOrder = 0
        CreationThumbnail originalThumb = CreationThumbnail.create(
                creation,
                byKey.get(baseKey + ThumbnailKeys.HORIZONTAL_SUFFIX),
                CreationThumbnailType.HORIZONTAL,
                (short) 0,
                null
        );
        creation.addThumbnail(originalThumb);

        // 리사이징 이미지 6개 - displayOrder = 1..6, sourceImage = originalThumb
        short order = 1;
        for (String suffix : ThumbnailKeys.DERIVED_SUFFIXES) {
            FileObject derivedFo = byKey.get(baseKey + suffix);

            CreationThumbnail derivedThumb = CreationThumbnail.create(
                    creation,
                    derivedFo,
                    CreationThumbnailType.DERIVED,
                    order,
                    originalThumb
            );
            creation.addThumbnail(derivedThumb);
            order++;
        }

        // 7. 저장 (cascade로 thumbnail도 저장됨)
        Creation saved = creationRepository.save(creation);
        return saved.getId();
    }
}
