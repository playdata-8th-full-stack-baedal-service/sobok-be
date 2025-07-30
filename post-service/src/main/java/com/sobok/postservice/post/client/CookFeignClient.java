package com.sobok.postservice.post.client;

import com.sobok.postservice.common.config.FeignConfig;
import com.sobok.postservice.post.dto.response.CookNameResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "cook-service", configuration = FeignConfig.class)
public interface CookFeignClient {

    /**
     * 요리 Id로 요리 이름을 조회
     */
    @GetMapping("/api/cook-name")
    ResponseEntity<String> getCookNameById(@RequestParam Long cookId);

    /**
     * 요리 Id 목록을 기반으로 요리 이름 정보를 조회
     */
    @PostMapping("/api/cook-names")
    ResponseEntity<List<CookNameResDto>> getCookNamesByIds(@RequestBody List<Long> cookIds);

    /**
     * 요리 썸네일 가져오기
     */
    @GetMapping("/api/cook-thumbnail")
    ResponseEntity<String> getCookThumbnail(@RequestParam Long cookId);
}

