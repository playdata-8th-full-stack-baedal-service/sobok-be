package com.sobok.shopservice.shop.client;

import com.sobok.shopservice.common.config.FeignConfig;
import com.sobok.shopservice.shop.dto.response.CookPostGroupResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "post-service", configuration = FeignConfig.class)
public interface PostFeignClient {

    /**
     * 요리별로 좋아요 순으로 조회
     */
    @GetMapping("/api/post/cook-posts/{cookId}")
    ResponseEntity<CookPostGroupResDto> getCookPosts(@PathVariable Long cookId);
}
