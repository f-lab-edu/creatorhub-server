package com.creatorhub.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ThumbnailKeys {

    // 가로 원본이 이 suffix로 업로드된다고 가정
    public static final String ORIGINAL_SUFFIX = "_434x330.jpg";

    // 리사이징 6개 suffix
    public static final List<String> DERIVED_SUFFIXES = List.of(
            "_83x90.jpg",
            "_98x79.jpg",
            "_125x101.jpg",
            "_202x164.jpg",
            "_217x165.jpg",
            "_218x120.jpg"
    );

    public static List<String> allSuffixes() {
        List<String> all = new ArrayList<>();
        all.add(ORIGINAL_SUFFIX);
        all.addAll(DERIVED_SUFFIXES);
        return Collections.unmodifiableList(all);
    }
}
