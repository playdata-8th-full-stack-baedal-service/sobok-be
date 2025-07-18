package com.sobok.apiservice.api.controller;

import com.sobok.apiservice.api.service.s3.S3Service;
import com.sobok.apiservice.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class S3Controller {

    private final S3Service s3Service;

     /**
     * S3 사진 삭제
     */
    @DeleteMapping("/delete-S3-image")
    public ResponseEntity<?> deleteS3Image(@RequestParam String key) {
        s3Service.deleteImage(key);
        return ResponseEntity.ok().body(ApiResponse.ok(key, "S3의 파일이 성공적으로 삭제되었습니다."));
    }

    /**
     * S3 이미지 업로드 - 10분 내 register 필요
     */
    @PutMapping("/upload-image/{category}")
    public ResponseEntity<?> putS3Image(@RequestPart MultipartFile image, @PathVariable String category) {
        String imgUrl = s3Service.uploadImage(image, category);
        return ResponseEntity.ok().body(ApiResponse.ok(imgUrl, "S3에 파일이 정상적으로 업로드되었습니다."));
    }


}
