package com.sobok.apiservice.api.service.s3;

import com.sobok.apiservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3DeleteService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

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
}
