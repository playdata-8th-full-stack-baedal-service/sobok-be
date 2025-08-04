package com.sobok.userservice.user.client;

import com.sobok.userservice.common.config.FeignConfig;
import com.sobok.userservice.common.dto.CommonResponse;
import com.sobok.userservice.user.dto.response.UserLocationResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "api-service", url = "${DELIVERY_SERVICE_URL}", configuration = FeignConfig.class)
public interface ApiServiceClient {

    @GetMapping("/api/convert-addr")
    ResponseEntity<UserLocationResDto> convertAddress(@RequestParam String roadFull);

    @DeleteMapping("/api/delete-S3-image")
    ResponseEntity<CommonResponse<String>> deleteS3Image(@RequestParam String key);

    @PostMapping(value = "/api/change-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> changeImage(@RequestPart MultipartFile image, @RequestPart String category, @RequestPart String oldPhoto);
}
