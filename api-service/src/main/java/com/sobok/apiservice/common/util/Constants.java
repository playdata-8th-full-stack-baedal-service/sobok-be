package com.sobok.apiservice.common.util;

import java.util.List;
import java.util.Map;

public class Constants {
    // S3 유효성 검증용 상수
    public static final String S3_UPLOAD_EXPIRATION_KEY = "s3:expiration:";
    public static final String S3_UPLOAD_CHECK_KEY = "s3:validation:";
    public static final long PRESIGN_URL_EXPIRATION = 10;
    public static final long PRESIGN_URL_CHECK_EXPIRATION = 20;


    public static final String URL_FRONT = "https://sobok-image.s3.ap-northeast-2.amazonaws.com/";

    public static final Map<String, String> EXT_TO_CONTENT_TYPE = Map.of(
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "webp", "image/webp"
    );

    public static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/png", "image/jpeg", "image/webp"
    );

    public static final List<String> ALLOWED_CATEGORIES = List.of(
            "profile", "food", "post"
    );

    public static final String validationFailMsg = "유효하지 않은 이미지 입력입니다.";
    public static final String internalServerErrorMsg = "S3를 업로드하는 과정에서 오류가 발생했습니다.";
}
