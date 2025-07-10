package com.sobok.postservice.post.client;

import com.sobok.postservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cook-service", configuration = FeignConfig.class)
public interface CookFeignClient {


    /**
     * 요리 ID로 요리 이름을 조회
     */
    @GetMapping("/api/cook/name")
    String getCookNameById(@RequestParam Long cookId);
}

