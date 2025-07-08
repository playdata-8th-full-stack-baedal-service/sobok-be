package com.sobok.apiservice.api.controller;

import com.sobok.apiservice.api.dto.address.AddressReqDto;
import com.sobok.apiservice.api.dto.address.LocationResDto;
import com.sobok.apiservice.api.dto.toss.TossPayReqDto;
import com.sobok.apiservice.api.dto.toss.TossPayResDto;
import com.sobok.apiservice.api.service.address.ConvertAddressService;
import com.sobok.apiservice.api.service.s3.S3Service;
import com.sobok.apiservice.api.service.toss.TossPayService;
import com.sobok.apiservice.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ApiController {

    private final S3Service s3Service;
    private final TossPayService tossPayService;
    private final ConvertAddressService convertAddressService;

    /**
     * S3 등록용 URL 발급
     */
    @GetMapping("/presign")
    public ResponseEntity<?> generatePresignedUrl(@RequestParam String fileName, @RequestParam String category) {
        String presignedUrl = s3Service.getS3PresignUrl(fileName, category);
        return ResponseEntity.ok(ApiResponse.ok(presignedUrl, "S3 버킷에 사진을 넣을 수 있는 URL이 성공적으로 발급되었습니다."));
    }

    @GetMapping("/presignFeign")
    public String generatePresignedUrlFeign(@RequestParam String fileName, @RequestParam String category) {
        return s3Service.getS3PresignUrl(fileName, category);
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
    public ResponseEntity<?> confirmPayment(@RequestBody TossPayReqDto reqDto) {
        TossPayResDto resDto = tossPayService.confirmPayment(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(resDto, "정상 처리되었습니다."));
    }

    @GetMapping("/convert-addr")
    public LocationResDto convertAddress(@RequestParam String roadFull) {
        return convertAddressService.getLocation(roadFull);
    }
}