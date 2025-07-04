package com.sobok.apiservice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobok.apiservice.api.dto.TossPayReqDto;
import com.sobok.apiservice.api.dto.TossPayResDto;
import com.sobok.apiservice.common.dto.ApiResponse;
import com.sobok.apiservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ApiController {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final RestClient restClient;

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
                    .contentType("image/png")
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

    /**
     * S3 사진 삭제
     */
    @DeleteMapping("/delete-S3-image")
    public ResponseEntity<?> deleteS3Image(@RequestParam String key) {
        log.info("삭제 레츠고");
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key("profile/Screenshot from 2025-05-04 13-28-01.png")
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
            return ResponseEntity.ok().body(ApiResponse.ok(key, "S3의 파일이 성공적으로 삭제되었습니다."));
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody TossPayReqDto reqDto) throws Exception {
        log.info(reqDto.getPaymentKey().toString());
        log.info(reqDto.getOrderId().toString());
        log.info(reqDto.getAmount().toString());


        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);


        log.info("토스에 요청 보내기");
        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        TossPayResDto resDto = restClient.post()
                .uri("/v1/payment/confirm")
                .header("Authorization", authorizations)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(reqDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error(res.getStatusText());
                })
                .body(TossPayResDto.class);

        log.info("토스 결제 성공!");
        return ResponseEntity.ok().body(ApiResponse.ok(resDto, "정상 처리되었습니다."));
    }


}