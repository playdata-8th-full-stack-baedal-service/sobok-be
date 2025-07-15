package com.sobok.apiservice.common.util;

import com.sobok.apiservice.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.sobok.apiservice.common.util.Constants.*;
import static com.sobok.apiservice.common.util.Constants.EXT_TO_CONTENT_TYPE;
import static com.sobok.apiservice.common.util.Constants.validationFailMsg;

@Slf4j
public class S3Util {
    public S3Util() {
        throw new RuntimeException();
    }

    private static final Tika tika = new Tika();

    /**
     * 파일 이름 생성 메서드
     * @return temp/category/UUID/name
     */
    public static String getFileName(String name, String category, boolean isTempImg) {
        String temp = isTempImg ? "temp/" : "";
        return new String((temp + category + "/" + UUID.randomUUID() + name).getBytes(), StandardCharsets.UTF_8);
    }

    /**
     * 파일의 metadata 생성 메서드
     * @return {"original-filename" : "name", "upload-time" : "현재 시각"}
     */
    public static Map<String, String> getMetaData(String name) {
        return Map.of(
                "original-filename", name,
                "upload-time", Instant.now().toString()
        );
    }

    /**
     * Image URL 생성 메서드
     */
    public static String getImageUrl(String fileName) {
        return URL_FRONT + fileName;
    }

    /**
     * Image URL에서 key 값 분리 메서드
     */
    public static String detachImageUrl(String url) {
        if(url.contains("temp")) {
            return url.replace(URL_FRONT + "temp/", "");
        } else {
            return url.replace(URL_FRONT, "");
        }
    }

    /**
     * 이미지 유효성 검증
     * - 확장자와 이미지 파일 형식 비교
     * - 이미지 파일 형식이 허용되는 형식인지 검증
     */
    public static void checkImageValidation(MultipartFile image, String name) throws IOException {
        // 파일 이름에서 확장자 가져오기
        String ext = name.substring(name.lastIndexOf('.') + 1);

        // Tika를 통한 이미지 검증
        byte[] imgBytes = image.getBytes();
        String detectType = tika.detect(imgBytes);

        // 감지된 이미지 타입이 허용하는 이미지 타입인지 검사
        if (!ALLOWED_MIME_TYPES.contains(detectType)) {
            log.error("허용되지 않는 이미지 타입입니다. | detectType: {}", detectType);
            throw new CustomException(validationFailMsg, HttpStatus.FORBIDDEN);
        }

        // 확장자와 감지된 이미지 타입이 일치하는지 검사
        if (!Objects.equals(EXT_TO_CONTENT_TYPE.get(ext), detectType)) {
            log.error("이미지 타입이 확장자와 다릅니다. | ext: {}, detectType: {}", ext, detectType);
            throw new CustomException(validationFailMsg, HttpStatus.FORBIDDEN);
        }
    }

    public static String getContentType(String key) {
        String extension = key.substring(key.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            default:
                return "application/octet-stream"; // 기본값
        }
    }
}
