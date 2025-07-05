package com.sobok.apiservice.api.service.s3;

import com.sobok.apiservice.common.enums.ImageCategory;
import com.sobok.apiservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;
    private final RedisTemplate<String, String> redisTemplate;
    private final S3Client s3Client;
    private final RestClient restClient;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String S3_UPLOAD_EXPIRATION_KEY = "s3:expiration:";
    private static final String S3_UPLOAD_CHECK_KEY = "s3:validation:";
    private static final long PRESIGN_URL_EXPIRATION = 10;
    private static final long PRESIGN_URL_CHECK_EXPIRATION = 20;

    /**
     * S3 Presign URL 생성
     */
    public String getS3PresignUrl(String fileName, String category) {
        log.info("S3 Presign URL 생성 시작 | fileName : {} | category : {} ", fileName, category);

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
                    .key("profile/Screenshot from 2025-05-04 13-28-01.png")
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("S3 이미지를 삭제하는 과정에서 오류가 발생했습니다. | error : {}", e.getMessage());
            throw new CustomException("S3 이미지를 삭제하는 과정에서 오류가 발생했습니다.",  HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //region Private Function

    private PresignedPutObjectRequest getPresignedPutRequest(String key) {
        // S3 PUT 요청 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("image/*")
                .build();

        // Presign 요청 생성
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PRESIGN_URL_EXPIRATION))
                .putObjectRequest(putObjectRequest)
                .build();

        // Presign 요청을 보낼 URL 생성
        return s3Presigner.presignPutObject(presignRequest);
    }

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
            log.error("Redis 키 생성 과정에서 오류가 발생했습니다. | key : {}", key);
            throw new CustomException("Redis 키 생성 과정에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //endregion

}
