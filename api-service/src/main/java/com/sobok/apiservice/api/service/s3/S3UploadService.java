package com.sobok.apiservice.api.service.s3;

import com.sobok.apiservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3UploadService {
    private final S3Presigner s3Presigner;
    private final RedisTemplate<String, String> redisTemplate;
    private final S3Client s3Client;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String URL_FRONT = "https://sobok-image.s3.ap-northeast-2.amazonaws.com/";

    private static final Map<String, String> EXT_TO_CONTENT_TYPE = Map.of(
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "webp", "image/webp"
    );


    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/png", "image/jpeg", "image/webp"
    );

    private static final List<String> ALLOWED_CATEGORIES = List.of(
            "profile", "food", "post"
    );

    private static final String validationFailMsg = "유효하지 않은 이미지 입력입니다.";
    private static final String internalServerErrorMsg = "S3를 업로드하는 과정에서 오류가 발생했습니다.";

    private static Tika tika = new Tika();

    //--------------------------------------------------------------------------------------

    /**
     * S3 이미지 업로드
     * @return 이미지의 url
     */
    public String uploadImage(MultipartFile image, String category) throws CustomException {
        log.info("S3 이미지 임시 업로드 서비스 시작 | category: {}",  category);

        return putImage(image, category, true);
    }

    /**
     * <pre>
     *  S3 기존 이미지 변경
     *  1. 기존 이미지 삭제
     *  2. 새로운 이미지 등록
     * </pre>
     * @return 새로운 이미지 등록
     */
    public String changeImage(MultipartFile image, String category, String oldPhoto) throws CustomException{
        log.info("S3 이미지 변경 서비스 시작 | category: {} | oldPhoto: {}",  category, oldPhoto);

        // oldPhoto가 유효하다면 삭제 진행
        if (oldPhoto != null && !oldPhoto.isEmpty()) { // TODO : 기본 이미지 설정 추가해야 함
            s3Service.deleteS3Image(detachImageUrl(oldPhoto));
        }

        // 새로운 이미지 등록 (TEMP 저장 X)
        return putImage(image, category, false);
    }

    /**
     * <pre>
     *     S3 이미지 등록 확정
     *     1. URL에서 키 값 추출
     *     2. 기존 객체 정보 가져오기
     *     3. S3 COPY 요청
     * </pre>
     */
    public String registerImage(String url) {
        log.info("업로드 이미지 영구 변환 서비스 시작 | url: {}", url);

        // URL에서 키 값 가져오기
        String key = detachImageUrl(url);
        String tempKey = "temp/" + key;

        try {
            return copyImageToS3(tempKey, key);
        } catch (Exception e) {
            log.error("이미지 복사 과정에서 오류가 발생했습니다.", e);
            throw new CustomException(internalServerErrorMsg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ---------------------------------------------------------------------------------------
    /**
     * <pre>
     *     새로운 S3 Image 업로드 (검증 과정 포함)
     *     1. 카테고리 입력값 검증
     *     2. 이미지가 진짜 이미지인지 검증
     *     3. PUT 요청
     * </pre>
     */
    private String putImage(MultipartFile image, String category, boolean isTempImg) {
        log.info("이미지 업로드 서비스 시작 | category: {}",  category);

        // 0. 카테고리 입력값 검증
        if (!ALLOWED_CATEGORIES.contains(category)) {
            log.error("유효하지 않은 카테고리 입력입니다. | category: {}", category);
            throw new CustomException("유효하지 않은 카테고리 입력입니다.", HttpStatus.BAD_REQUEST);
        }

        // 1. image가 없거나 이미지 이름이 없다면 오류
        if (image.isEmpty() || image.getOriginalFilename() == null) {
            log.error("빈 이미지가 요청되었습니다.");
            throw new CustomException(validationFailMsg, HttpStatus.BAD_REQUEST);
        }
        String name = image.getOriginalFilename();

        // 2. 이미지가 진짜 이미지인지 검증
        try {
            checkImageValidation(image, name);
        } catch (IOException e) {
            log.error("이미지를 바이트배열로 변환하는 과정에서 오류가 발생했습니다.");
            throw new CustomException(internalServerErrorMsg, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("알수 없는 오류가 발생하였습니다.", e);
            throw new CustomException(internalServerErrorMsg, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 3. PUT 요청
        try {
            return putImageToS3(image, category, isTempImg, name);
        } catch (Exception e) {
            log.error("이미지 업로드 과정에서 알 수 없는 오류 발생", e);
            throw new CustomException(internalServerErrorMsg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * S3 PUT 요청
     */
    private String putImageToS3(MultipartFile image, String category, boolean isTempImg, String name) throws IOException {
        // PUT 요청 생성
        String fileName = getFileName(name, category, isTempImg);
        PutObjectRequest.Builder putRequestBuilder = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .metadata(getMetaData(name));

        // Temp 저장이라면 수명 설정
        if(isTempImg) putRequestBuilder.expires(Instant.now().plus(10L, ChronoUnit.MINUTES));

        // putRequest 생성
        PutObjectRequest putRequest = putRequestBuilder.build();

        // Request Body 생성
        RequestBody requestBody = RequestBody.fromInputStream(
                image.getInputStream(),
                image.getSize()
        );

        // S3 PUT 요청
        PutObjectResponse response = s3Client.putObject(putRequest, requestBody);
        log.info(response.toString());

        // 이미지 url 반환
        return getImageUrl(fileName);
    }

    /**
     * S3 COPY 요청
     */
    private String copyImageToS3(String tempKey, String key) {
        // 기존 객체 정보 가져오기
        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(tempKey)
                .build();

        HeadObjectResponse headResponse = s3Client.headObject(headRequest);

        // COPY 요청 생성
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(tempKey)
                .destinationBucket(bucket)
                .destinationKey(key)
                .metadataDirective(MetadataDirective.REPLACE)
                .metadata(headResponse.metadata())
                .contentType(headResponse.contentType())
                .build();

        // S3 COPY 요청
        s3Client.copyObject(copyObjectRequest);
        return getImageUrl(key);
    }

    /**
     * 이미지 유효성 검증
     * - 확장자와 이미지 파일 형식 비교
     * - 이미지 파일 형식이 허용되는 형식인지 검증
     */
    private static void checkImageValidation(MultipartFile image, String name) throws IOException {
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

    // -----------------------------------------------------------------------------------
    /**
     * 파일 이름 생성 메서드
     * @return temp/category/UUID/name
     */
    private static String getFileName(String name, String category, boolean isTempImg) {
        String temp = isTempImg ? "temp/" : "";
        return temp + category + "/" + UUID.randomUUID() + name;
    }

    /**
     * 파일의 metadata 생성 메서드
     * @return {"original-filename" : "name", "upload-time" : "현재 시각"}
     */
    private static Map<String, String> getMetaData(String name) {
        return Map.of(
                "original-filename", name,
                "upload-time", Instant.now().toString()
        );
    }

    /**
     * Image URL 생성 메서드
     */
    private String getImageUrl(String fileName) {
        return URL_FRONT + fileName;
    }

    /**
     * Image URL에서 key 값 분리 메서드
     */
    private String detachImageUrl(String url) {
        return url.replace(URL_FRONT + "temp/", "");
    }
}
