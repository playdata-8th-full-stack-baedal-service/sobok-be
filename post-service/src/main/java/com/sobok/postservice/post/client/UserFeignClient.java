package com.sobok.postservice.post.client;

import com.sobok.postservice.common.config.FeignConfig;
import com.sobok.postservice.post.dto.response.UserInfoResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserFeignClient {
    @GetMapping("/api/post-info")
    UserInfoResDto getUserInfo(@RequestParam Long userId);

    @GetMapping("/api/user/nickname")
    String getNicknameById(@RequestParam Long userId);
}
