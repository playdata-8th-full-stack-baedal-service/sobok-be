package com.sobok.userservice.user.client;

import com.sobok.userservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "post-service", url = "${POST_SERVICE_URL}", configuration = FeignConfig.class)
public interface PostServiceClient {

    /**
     * 게시글 존재 여부를 확인
     */
    @GetMapping("/api/check-post-exists")
    Boolean checkPostExists(@RequestParam Long postId);

}
