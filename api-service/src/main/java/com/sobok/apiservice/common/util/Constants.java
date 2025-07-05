package com.sobok.apiservice.common.util;

public class Constants {
    // S3 유효성 검증용 상수
    public static final String S3_UPLOAD_EXPIRATION_KEY = "s3:expiration:";
    public static final String S3_UPLOAD_CHECK_KEY = "s3:validation:";
    public static final long PRESIGN_URL_EXPIRATION = 10;
    public static final long PRESIGN_URL_CHECK_EXPIRATION = 20;
}
