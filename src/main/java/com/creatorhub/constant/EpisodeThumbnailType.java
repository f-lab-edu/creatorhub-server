package com.creatorhub.constant;

public enum EpisodeThumbnailType implements ThumbnailType {
    EPISODE_THUMBNAIL {
        @Override
        public String resolveSuffix() {
            return ThumbnailKeys.EPISODE_THUMBNAIL_SUFFIX;
        }
    },
    SNS {
        @Override
        public String resolveSuffix() {
            return ThumbnailKeys.SNS_SUFFIX;
        }
    }
}
