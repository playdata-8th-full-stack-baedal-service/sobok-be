package com.sobok.apiservice.common.enums;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ImageCategory {
    FOOD, POST, PROFILE;

    /**
     * 유효한 카테고리인지 확인
     */
    public static boolean isValidCategory(String category) {
        log.info("카테고리 유효성 검증 시작 | category : {}", category);
        ImageCategory[] values = ImageCategory.values();
        category = category.toUpperCase();

        // 카테고리 검증
        for (ImageCategory value : values) {
            if (value.name().equals(category)) {
                return true;
            }
        }

        return false;
    }
}
