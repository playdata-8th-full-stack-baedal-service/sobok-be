package com.sobok.postservice.post.client;

import com.sobok.postservice.common.config.FeignConfig;
import com.sobok.postservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "api-service", configuration = FeignConfig.class)
public interface ApiFeignClient {

    /**
     * S3 이미지 등록 - 업로드 후 실제 정보 저장이 완료되면 실행
     */
    @PostMapping("/api/register-image")
    String registerImg(@RequestParam String url);

    @DeleteMapping("/api/delete-S3-image")
    ResponseEntity<ApiResponse<String>> deleteS3Image(@RequestParam("key") String key);

}
