package com.sobok.apiservice.api.controller;

import com.sobok.apiservice.api.dto.TossPayReqDto;
import com.sobok.apiservice.api.dto.TossPayResDto;
import com.sobok.apiservice.api.service.s3.S3Service;
import com.sobok.apiservice.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ApiController {

    private final S3Service s3Service;
    private final S3Client s3Client;
    private final RestClient restClient;

    /**
     * S3 등록용 URL 발급
     */
    @GetMapping("/presign")
    public ResponseEntity<?> generatePresignedUrl(@RequestParam String fileName, @RequestParam String category) {
        String presignedUrl = s3Service.getS3PresignUrl(fileName, category);
        return ResponseEntity.ok(ApiResponse.ok(presignedUrl, "S3 버킷에 사진을 넣을 수 있는 URL이 성공적으로 발급되었습니다."));
    }

    /**
     * S3 사진 삭제
     */
    @DeleteMapping("/delete-S3-image")
    public ResponseEntity<?> deleteS3Image(@RequestParam String key) {
        s3Service.deleteS3Image(key);
        return ResponseEntity.ok().body(ApiResponse.ok(key, "S3의 파일이 성공적으로 삭제되었습니다."));
    }

    /**
     * 토스페이 결제
     */
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