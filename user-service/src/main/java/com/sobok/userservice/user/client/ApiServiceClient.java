package com.sobok.userservice.user.client;

import com.sobok.userservice.common.config.FeignConfig;
import com.sobok.userservice.common.dto.ApiResponse;
import com.sobok.userservice.user.dto.response.UserLocationResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "api-service", configuration = FeignConfig.class)
public interface ApiServiceClient {

    @GetMapping("/api/convert-addr")
    UserLocationResDto convertAddress(@RequestParam String roadFull);

    @DeleteMapping("/api/delete-S3-image")
    ResponseEntity<ApiResponse<String>> deleteS3Image(@RequestParam String key);

    @GetMapping("/api/presignFeign")
    String generatePresignedUrlFeign(@RequestParam String fileName, @RequestParam String category);

    @PatchMapping("/api/change-image")
    String changeImage(@RequestPart MultipartFile image, @RequestPart String category, @RequestPart String oldPhoto);
}
