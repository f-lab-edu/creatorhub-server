package com.creatorhub.dto;

import com.creatorhub.entity.Creator;
//import com.creatorhub.entity.FileObject;

public record CreatorResponse(
        Long id,
        Long memberId,
        String creatorName,
        String introduction
//        FileObjectResponse profileImage
) {
    public static CreatorResponse from(Creator creator) {
//        FileObject fileObject = creator.getFileObject();
        return new CreatorResponse(
                creator.getId(),
                creator.getMember().getId(),
                creator.getCreatorName(),
                creator.getIntroduction()
//                FileObjectResponse.from(fileObject)
        );
    }
}