package com.sobok.apiservice.api.controller;

import com.sobok.apiservice.common.dto.ApiResponse;
import com.sobok.apiservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.awscore.presigner.PresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class S3PresignController {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3 등록용 URL 발급
     * (추후 주석 작성 요망)
     */
    @GetMapping("/presign")
    public ResponseEntity<?> generatePresignedUrl(@RequestParam String fileName, @RequestParam String category) {
        // 카테고리 확인
        if(!(category.equals("food") || category.equals("post") || category.equals("profile"))) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(HttpStatus.BAD_REQUEST, "잘못된 카테고리입니다."));
        }

        PresignedPutObjectRequest presigned = null;
        try {
            // 키 이름 만들기
            String key = "uploads/" + UUID.randomUUID() + "_" + category + "_" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("image/jpeg")
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(putObjectRequest)
                    .build();

            presigned = s3Presigner.presignPutObject(presignRequest);
        } catch (Exception e) {
            // S3 토큰 발급 중 오류 발생
            throw new CustomException("URL 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(ApiResponse.ok(presigned.url().toString(), "S3 버킷에 사진을 넣을 수 있는 URL이 성공적으로 발급되었습니다."));
    }
}