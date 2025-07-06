package com.sobok.userservice.user.client;

import com.sobok.userservice.common.config.FeignConfig;
import com.sobok.userservice.user.dto.response.UserLocationResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "api-service", configuration = FeignConfig.class)
public interface ApiServiceClient {

    @GetMapping("/api/convert-addr")
    UserLocationResDto convertAddress(@RequestParam String roadFull);

}
