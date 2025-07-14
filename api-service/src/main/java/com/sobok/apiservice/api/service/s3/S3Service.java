package com.sobok.apiservice.api.service.s3;

import com.sobok.apiservice.api.dto.S3.S3ImgMetaDto;
import com.sobok.apiservice.common.enums.ImageCategory;
import com.sobok.apiservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.sobok.apiservice.common.util.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;
    private final RedisTemplate<String, String> redisTemplate;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final Map<String, String> EXT_TO_CONTENT_TYPE = Map.of(
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "webp", "image/webp",
            "gif", "image/gif"
    );


    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/png", "image/jpeg", "image/webp"
    );

    private static final Tika TIKA = new Tika();

    /**
     * S3 Presign URL 생성
     */
    public String getS3PresignUrl(String fileName, String category) {
        log.info("S3 Presign URL 생성 시작 | fileName : {} | category : {} ", fileName, category);

        // 카테고리 검증
        if (!ImageCategory.isValidCategory(category)) {
            log.error("유효하지 않은 카테고리입니다. | category : {}", category);
            throw new CustomException("유효하지 않은 카테고리입니다.", HttpStatus.BAD_REQUEST);
        }

        // 키 이름 만들기 - category/UUID_filename
        category = category.toLowerCase();
        String key = category + "/" + UUID.randomUUID() + "_" + fileName;
        log.info("S3에 업로드에 필요한 키 생성 완료 | key : {}", key);

        // S3 Presign PUT 요청 생성
        PresignedPutObjectRequest presigned = getPresignedPutRequest(key);

        // Redis에 유효성 검증을 위한 key-value 값 생성
        setRedisForCheckingValidation(key);

        return presigned.url().toString();
    }

    /**
     * S3 이미지 삭제
     */
    public void deleteS3Image(String key) {
        log.info("S3 이미지 삭제 시작 | key : {}", key);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("S3 이미지를 삭제하는 과정에서 오류가 발생했습니다. | error : {}", e.getMessage(), e);
            throw new CustomException("S3 이미지를 삭제하는 과정에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //region Private Function

    /**
     * S3 Presign 업로드 요청 생성
     */
    private PresignedPutObjectRequest getPresignedPutRequest(String key) {
        // 키 유효성 검사
        int extIndex = key.lastIndexOf(".") + 1;
        if (extIndex == 0 || key.length() <= extIndex) {
            log.error("올바르지 않은 파일명입니다. | key : {}", key);
            throw new CustomException("올바르지 않은 파일명입니다.", HttpStatus.BAD_REQUEST);
        }

        // 파일 확장자 검사
        String ext = key.substring(extIndex);
        String contentType = EXT_TO_CONTENT_TYPE.get(ext);
        if (contentType == null) {
            log.error("잘못된 파일 형식 입력입니다. | 확장자 : {}", ext);
            throw new CustomException("잘못된 파일 형식 입력입니다.", HttpStatus.BAD_REQUEST);
        }

        // S3 PUT 요청 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        // Presign 요청 생성
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PRESIGN_URL_EXPIRATION))
                .putObjectRequest(putObjectRequest)
                .build();

        // Presign 요청을 보낼 URL 생성
        return s3Presigner.presignPutObject(presignRequest);
    }

    /**
     * 이미지 유효성 검증을 위한 redis 키 생성
     */
    private void setRedisForCheckingValidation(String key) {
        try {
            // Redis 키 생성 - presign url 업로드 시간 체크용
            redisTemplate.opsForValue().set(
                    S3_UPLOAD_EXPIRATION_KEY + key,
                    "PENDING",
                    Duration.ofMinutes(PRESIGN_URL_EXPIRATION)
            );

            // Redis 키 생성 - Tika를 이용한 유효성 검증 체크용
            redisTemplate.opsForValue().set(
                    S3_UPLOAD_CHECK_KEY + key,
                    "COMPLETED",
                    Duration.ofMinutes(PRESIGN_URL_CHECK_EXPIRATION)
            );
        } catch (IllegalArgumentException e) {
            // 키는 반드시 두 개가 들어가도록 보장
            redisTemplate.delete(S3_UPLOAD_EXPIRATION_KEY + key);

            log.error("Redis 키 생성 과정에서 오류가 발생했습니다. | key : {}", key);
            throw new CustomException("Redis 키 생성 과정에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void putS3Image(MultipartFile image, S3ImgMetaDto reqDto) {
        // 1. 이미지 검증

        // 2. 올바른 이미지라면 업로드

    }

    /**
     * 단건 이미지에 대한 유효성 검사
     */
    private void checkImageValidationByCheckKey(String checkKey) {
        // File 키 값 가져오기
        String fileKey = checkKey.replace(S3_UPLOAD_CHECK_KEY, "");

        // 업로드 시간이 지났는지 확인
        String triggerKey = S3_UPLOAD_EXPIRATION_KEY + fileKey;

        // 아직 triggerKey가 살아있다면 검사 시점이 아님
        if (redisTemplate.hasKey(triggerKey)) return;

        try {
            log.info("검사 시작 | filekey : {}", fileKey);

            // S3에서 파일 꺼내기
            ResponseBytes<GetObjectResponse> s3Object = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileKey)
                            .build()
            );

            // 바이트 배열로 변환
            byte[] imageBytes = s3Object.asByteArray();

            // MIME 타입 검사
            String mimeType = TIKA.detect(imageBytes);

            if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
                log.warn("허용되지 않은 이미지 형식입니다. 삭제 대상: {}, MIME-TYPE: {}", fileKey, mimeType);

                // 파일 삭제
                deleteS3Image(fileKey);

                log.info("S3 객체 삭제 완료 | fileKey : {}", fileKey);
            } else {
                log.info("MIME 검사 통과 | MIME : {})", mimeType);
            }

        } catch (NoSuchKeyException e) {
            log.warn("파일이 이미 존재하지 않음 | fileKey: {}", fileKey);
        } catch (Exception e) {
            log.error("이미지 검사 중 예외가 발생했습니다. | error : {}", e.getMessage(), e);
        } finally {
            // check key 삭제
            redisTemplate.delete(checkKey);
        }
    }

    //endregion

}
