package com.sobok.userservice.user.client;

import com.sobok.userservice.common.config.FeignConfig;
import com.sobok.userservice.user.dto.request.UserBookmarkReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "cook-service", configuration = FeignConfig.class)
public interface CookServiceClient {

    @GetMapping("/api/check-cook")
    ResponseEntity<?> checkCook(@RequestParam("cookId") Long cookId);

}
