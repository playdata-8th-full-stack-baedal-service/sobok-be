package com.sobok.apiservice.api.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.util.List;
import java.util.Set;

import static com.sobok.apiservice.common.util.Constants.S3_UPLOAD_CHECK_KEY;
import static com.sobok.apiservice.common.util.Constants.S3_UPLOAD_EXPIRATION_KEY;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Scheduler {
    private final RedisTemplate<String, String> redisTemplate;
    private final S3Client s3Client;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/png", "image/jpeg", "image/webp"
    );

    private static final Tika TIKA = new Tika();

    /**
     * S3 Image 유효성 검사
     */
    @Scheduled(fixedRate = 180000) // 3분마다 실행
    public void checkS3ImageValidation() {
        log.info("S3 이미지 유효성 검사 시작");

        // Check Key 가져오기
        Set<String> checkKeys = redisTemplate.keys(S3_UPLOAD_CHECK_KEY + "*");
        if (checkKeys.isEmpty()) return;

        // 최대 검사 개수 30개로 제한하여 검사
        checkKeys.stream()
                .limit(30)
                .forEach(this::checkImageValidationByCheckKey);
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
//                s3Service.deleteS3Image(fileKey);

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
}
